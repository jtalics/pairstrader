package com.jtalics.onyx.visual.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * <p>This class implements a layout algorithm which reconsiders the
 * whole graph layout when executed. The algorithm will have a general
 * tendency to maintain the relative positions of nodes that are already
 * placed, if necessary it can move those nodes for a more optimal layout.</p>
 * 
 * <p>Note that this class does not implement {@link NodeLayoutManager} as it
 * is not an incremental layout algorithm.</p>
 */
class ForceDirectedLayout {
	
	/**
	 * The number of steps to advance at each round of the calculation.
	 */
	private static final double TIMESTEP = 0.1;
	
	/**
	 * The maximum time to work on the layout (in nanoseconds).
	 */
	private static final long MAX_TIME = 500000000L;
	
	/**
	 * The target energy level under which we should stop adjusting the layout.
	 */
	private static final double ENERGY_THRESHOLD = 1.0;
	
	/**
	 * The distance that (aside from other factors) an edge is at equilibrium.
	 */
	private static final double SPRING_DIST = 120;
	
	/**
	 * The constant multiplier for the force exerted by an edge to stretch/squeeze
	 * it to a length of {@link #SPRING_DIST}.
	 */
	private static final double SPRING_CONST = 2.0;
	
	/**
	 * Dampening constant. All node velocities are multiplied by this constant
	 * at each iteration. This value should be less than 1.0.
	 */
	private static final double DAMPENING = 0.99;
	
	/**
	 * Minimum distance to use for calculates. This is used to prevent
	 * NaN results from propagating through the calculation.
	 */
	private static final double MIN_SIM_DIST = 0.0001;
	
	/**
	 * The list of (wrapped) nodes that we need to lay out.
	 */
	private final List<Node> nodes;
	
	/**
	 * Map from raw graph nodes to the wrapped nodes that contain them.
	 */
	private final Map<Object, Node> nodeMap;
	
	/**
	 * Collection of raw graph edges to take into consideration while calculating
	 * the layout.
	 */
	private final Collection<? extends GraphEdge> edges;
	
	/**
	 * The position map that we should save the node positions to when we're done
	 * calculating the layout.
	 */
	private final Map<Object, Point2D> posMap;

	/**
	 * Creates a new object to lay out the specified nodes based on the collection
	 * of edges. Once the layout has been calculated, the position values for the
	 * nodes will be saved to <code>posMap</code>.
	 * 
	 * @param graphNodes - The nodes to lay out.
	 * @param graphEdges - The edges to consider for the layout.
	 * @param posMap - The position map to save the node positions to once the layout
	 *                 has been calculated.
	 */
	public ForceDirectedLayout(Collection<?> graphNodes, Collection<? extends GraphEdge> graphEdges, Map<Object, Point2D> posMap) {
		int nodeCount = graphNodes.size();
		int rowSize = (int)Math.ceil(Math.sqrt(nodeCount));
		int row = 0;
		int col = 0;
		
		this.nodes = new ArrayList<>(nodeCount);
		this.nodeMap = new IdentityHashMap<>(nodeCount);
		for (Object graphNode : graphNodes) {
			Node node = new Node();
			node.graphNode = graphNode;
			Point2D point = posMap.get(graphNode); 
			if (point == null) {
				node.px = col * SPRING_DIST;
				node.py = row * SPRING_DIST;
				col += 1;
				if (col >= rowSize) {
					col = 0;
					row += 1;
				} else { /* Don't need to advance the row number yet. */ ; }
			} else {
				node.px = point.getX();
				node.py = point.getY();
			}
			nodes.add(node);
			nodeMap.put(graphNode, node);
		}
		this.edges = graphEdges;
		for (GraphEdge edge : edges) {
			Node src = nodeMap.get(edge.getSrcNode());
			Node dest = nodeMap.get(edge.getDestNode());
			src.edgeCount += 1;
			dest.edgeCount += 1;
			src.neighbors.add(dest);
			dest.neighbors.add(src);
		}
		this.posMap = posMap;
		
		int groupId = 0;
		for (Node node : nodes) {
			if (node.groupId < 0) {
				node.markGroup(groupId);
				groupId += 1;
			} else { /* This node is already part of a group. */ ; }
		}
	}
	
	/**
	 * Calculates a layout for the nodes and edges passed to the constructor and
	 * saves the new node positions to the position map passed to the constructor.
	 * 
	 * @return - A recommended angle of rotation for the current layout.
	 */
	public double layout() {
		double energy;
		long lastTime = System.nanoTime();
		long timeSpent = 0;
		int nodeCount = nodes.size();
		
		/*
		 * Loop until the total energy of the system goes under the specified threshold,
		 * or we've exhausted our calculation time.
		 */
		do {
			energy = 0.0;
			
			// Apply spring forces along edges.
			for (GraphEdge edge : edges) {
				Node n1 = nodeMap.get(edge.getSrcNode());
				Node n2 = nodeMap.get(edge.getDestNode());
				double dist = distance(n1, n2);
				double force = (SPRING_CONST) * (dist - SPRING_DIST);
				double dx = n2.px - n1.px;
				double dy = n2.py - n1.py;
				double xPart = force * (dx / dist);
				double yPart = force * (dy / dist);
				n1.vx += xPart * TIMESTEP;
				n1.vy += yPart * TIMESTEP;
				n2.vx -= xPart * TIMESTEP;
				n2.vy -= yPart * TIMESTEP;
			}
			
			// Apply repelling forces between nodes.
			for (int i = 0 ; i < nodeCount ; i++) {
				Node n1 = nodes.get(i);
				for (int j = i + 1 ; j < nodeCount ; j++) {
					Node n2 = nodes.get(j);
					double dist = distance(n1, n2);
					
					double force;
					force = (SPRING_DIST * SPRING_DIST) / (dist * dist);
					
					double dx = n1.px - n2.px;
					double dy = n1.py - n2.py;
					double xPart = force * (dx / dist);
					double yPart = force * (dy / dist);
					double mult = (n1.groupId == n2.groupId) ? (n1.edgeCount + 1) * (n2.edgeCount + 1) : 0.1;
					n1.vx += xPart * mult * TIMESTEP;
					n1.vy += yPart * mult * TIMESTEP;
					n2.vx -= xPart * mult * TIMESTEP;
					n2.vy -= yPart * mult * TIMESTEP;
				}
				
				// Apply dampening factor to velocities.
				n1.vx *= DAMPENING;
				n1.vy *= DAMPENING;
				
				// Scale the velocity to a maximum value of 200.
				double velocity = Math.sqrt(n1.vx * n1.vx + n1.vy * n1.vy);
				double adjustFactor = Math.min(1.0, 200 / velocity);
				n1.vx *= adjustFactor;
				n1.vy *= adjustFactor;
				
				// Update node positions for this node and add this node's energy to the count.
				n1.px += n1.vx * TIMESTEP;
				n1.py += n1.vy * TIMESTEP;
				energy += n1.vx * n1.vx + n1.vy * n1.vy;
			}
			
			// Determine how long we've spent on this iteration and add it to our count.
			long newTime = System.nanoTime();
			timeSpent += newTime - lastTime;
			lastTime = newTime;
			
		} while (timeSpent < MAX_TIME && energy > ENERGY_THRESHOLD);
		
		return commitLayout();
	}
	
	/**
	 * Saves the node positions into the position map passed to the constructor.
	 * 
	 * @return - A recommended angle of rotation for the current layout.
	 */
	private double commitLayout() {
		/*
		 * Find the angle that we would need to rotate the graph so that the two points
		 * that are farthest from each other are placed along the same horizontal line.
		 * 
		 * First find the pair of points with the largest distance between them.
		 */
		double maxDist = 0.0;
		Node target1 = null;
		Node target2 = null;
		int nodeCount = nodes.size();
		for (int i = 0 ; i < nodeCount ; i++) {
			Node n1 = nodes.get(i);
			for (int j = i + 1 ; j < nodeCount ; j++) {
				Node n2 = nodes.get(j);
				double dist = distance(n1, n2);
				if (dist > maxDist) {
					target1 = n1;
					target2 = n2;
					maxDist = dist;
				} else { /* Not higher than the max distance. */ ; }
			}
		}
		final double angle;
		if (target1 != null) {
			angle = -Math.atan2(target2.py - target1.py, target2.px - target1.px);
		} else {
			angle = 0.0;
		}
		
		for (Node node : nodes) {
			Point2D p = new Point2D.Double(node.px, node.py);
			posMap.put(node.graphNode, p);
		}
		return angle;
	}
	
	/**
	 * Calculates the distance between the two nodes.
	 * 
	 * @param n1 - The first node.
	 * @param n2 - The second node.
	 * @return - The distance between the nodes.
	 */
	private double distance(Node n1, Node n2) {
		double dx = Math.abs(n1.px - n2.px);
		double dy = Math.abs(n1.py - n2.py);
		double dist = Math.sqrt(dx * dx + dy * dy);
		return Math.max(dist, MIN_SIM_DIST);
	}
}

/**
 * Node class used by the layout method defined in {@link ForceDirectedLayout}. This class
 * contains various temporary variables that we need to keep track of while the
 * layout is being created.<p>
 * 
 * These nodes are not intended to be the actual graph nodes. Instead they wrap
 * the actual graph nodes and carry along extra temporary information about those
 * nodes.
 */
class Node { 
	/**
	 * The graph node that we're wrapping.
	 */
	protected Object graphNode = null;
	
	/**
	 * The X velocity of this node.
	 */
	protected double vx = 0.0;
	
	/**
	 * The Y velocity of this node.
	 */
	protected double vy = 0.0;
	
	/**
	 * The X position of this node. (Note that this is not necessarily the same
	 * X position currently stored for this node in the graph.)
	 */
	protected double px = 0.0;
	
	/**
	 * The Y position of this node. (Note that this is not necessarily the same
	 * Y position currently stored for this node in the graph.)
	 */
	protected double py = 0.0;
	
	/**
	 * The number of edges connected to this node from either end.
	 */
	protected int edgeCount = 0;
	
	/**
	 * The list of neighbor nodes (connected by an edge).
	 */
	protected List<Node> neighbors = new LinkedList<>();
	
	/**
	 * The group ID for this node. While calculating a given layout, all nodes with
	 * a matching group ID are in some way connected. (That is, you could get from
	 * one to another by following some series of edges.) Groups with different IDs
	 * are disconnected.
	 */
	protected int groupId = -1;
	
	/**
	 * Mark this node and any other connected nodes with the specified group ID.
	 * 
	 * @param newGroupId - The group ID to mark this group of nodes with.
	 */
	public void markGroup(int newGroupId) {
		groupId = newGroupId;
		for (Node node : neighbors) {
			if (node.groupId != groupId) {
				node.markGroup(groupId);
			} else { /* This node is already marked. */ ; }
		}
	}
}

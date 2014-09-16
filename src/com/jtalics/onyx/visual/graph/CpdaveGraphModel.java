package com.jtalics.onyx.visual.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CpdaveGraphModel implements GraphModel {

	/**
	 * List of listeners registered with this graph model.
	 */
	private final Set<GraphChangeListener> listeners = new CopyOnWriteArraySet<>();
	
	/**
	 * List of nodes contained in this graph model.
	 */
	private final List<CpdaveGraphNode> nodes = new ArrayList<>();
	
	/**
	 * Immutable wrapper for the list of nodes for use when passing
	 * the list of nodes to an outside class.
	 */
	private final List<CpdaveGraphNode> wrappedNodes = Collections.unmodifiableList(nodes);
	
	/**
	 * List of edges between nodes in this graph model.
	 */
	private final List<CpdaveGraphEdge> edges = new ArrayList<>();
	
	/** 
	 * Immutable wrapper for the list of edges for use when passing
	 * the list of edges to an outside class.
	 */
	private final List<CpdaveGraphEdge> wrappedEdges = Collections.unmodifiableList(edges);
	
	
	/**
	 * Adds a node to this graph model
	 * 
	 * @param node
	 */
	public void addNode(CpdaveGraphNode node) {
		nodes.add(node);
		fireStructureChanged();
	}
	
	/**
	 * Removes a node from this graph model.
	 * 
	 * @param node
	 */
	public void removeNode(CpdaveGraphNode node) {
		nodes.remove(node);
		Iterator<CpdaveGraphEdge> edgeIterator = edges.iterator();
		while (edgeIterator.hasNext()) {
			CpdaveGraphEdge edge = edgeIterator.next();
			if (edge.getSrcNode() == node || edge.getDestNode() == node) {
				edgeIterator.remove();
			} else { ;
				// The node we're removing isn't one of the end points
				// on this node, so we don't need to delete it.
			}
		}
		fireStructureChanged();
	}
	
	/**
	 * Creates a link between the two specified nodes and returns the
	 * object representing that link. If there is already a link between
	 * the nodes, then the link object for that link is returned.
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	public CpdaveGraphEdge linkNodes(CpdaveGraphNode node1, CpdaveGraphNode node2) {
		if (!nodes.contains(node1) || !nodes.contains(node2)) {
			throw new IllegalArgumentException("cannot create link for unregistered nodes");
		} else {
			; // Both nodes exists - continue with the operation.
		}
		
		CpdaveGraphEdge result = getEdgeForNodes(node1, node2);
		if (result == null) {
			result = new CpdaveGraphEdge(node1, node2);
			edges.add(result);
			fireStructureChanged();
		} else { ;
			// We already have an edge between these two nodes, to  just return the
			// existing edge object (already assigned to result).
		}
		return result;
	}
	
	/**
	 * Deletes the link between the two specified nodes if one exists.
	 * If no link exists, then this method does nothing.
	 * 
	 * @param node1
	 * @param node2
	 */
	public void unlinkNodes(CpdaveGraphNode node1, CpdaveGraphNode node2) {
		CpdaveGraphEdge result = getEdgeForNodes(node1, node2);
		if (result != null) {
			edges.remove(result);
			fireStructureChanged();
		} else { ;
			// No edge is present, so we don't need to do anything.
		}
	}
	
	/**
	 * Gets the {@link CpdaveGraphEdge} object representing the link between
	 * the two specified nodes if an edge exists. If an edge does not exist,
	 * then this method returns {@code null}.
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	public CpdaveGraphEdge getEdgeForNodes(CpdaveGraphNode node1, CpdaveGraphNode node2) {
		CpdaveGraphEdge result = null;
		for (CpdaveGraphEdge edge : edges) {
			if (edge.checkEndpoints(node1, node2)) {
				result = edge;
				break;
			} else {
				; // Not a match - check the next node.
			}
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CpdaveGraphNode> getNodes() {
		return wrappedNodes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends GraphEdge> getEdges() {
		return wrappedEdges;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGraphChangeListener(GraphChangeListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeGraphChangeListener(GraphChangeListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Fires a structure changed event to all registered listeners.
	 */
	protected void fireStructureChanged() {
		for (GraphChangeListener listener : listeners) {
			listener.structureChanged(this);
		}
	}

	public void clearAll() {
		edges.clear();
		nodes.clear();
		fireStructureChanged();
	}

	public List<CpdaveGraphNode> getDestNodes(CpdaveGraphNode graphNode) {
		
		List<CpdaveGraphNode> destNodes = new ArrayList<CpdaveGraphNode>();
		for (CpdaveGraphEdge edge : edges) {
			if (edge.getSrcNode() == graphNode) {
				destNodes.add(edge.getDestNode());
			}
		}
		return destNodes;
	}
}

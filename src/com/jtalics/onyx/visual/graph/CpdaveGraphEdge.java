package com.jtalics.onyx.visual.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This class represents an edge between two {@link CpdaveGraphNode} objects.
 * The appearance of the edge can be specified by setting the color and edge
 * type (e.g. solid, dashed, thick, thin).
 */
public class CpdaveGraphEdge implements GraphEdge {
	
	/**
	 * The source node for this edge.
	 */
	private final CpdaveGraphNode node1;
	
	/**
	 * The destination node for this edge.
	 */
	private final CpdaveGraphNode node2;
	
	/**
	 * The color for this edge.
	 */
	private Color color = Color.DARK_GRAY;
	
	/**
	 * The edge type for this edge.
	 */
	private CpdaveEdgeType edgeType = CpdaveEdgeType.STANDARD_SOLID;
	
	/**
	 * Creates a new edge object for the two specified nodes.
	 * 
	 * @param node1
	 * @param node2
	 */
	public CpdaveGraphEdge(CpdaveGraphNode node1, CpdaveGraphNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}
	
	/**
	 * Gets the color for this edge.
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color for this edge.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Gets the edge type for this edge.
	 * @return
	 */
	public CpdaveEdgeType getEdgeType() {
		return edgeType;
	}
	
	/**
	 * Sets the edge type for this edge.
	 * 
	 * @param edgeType
	 */
	public void setEdgeType(CpdaveEdgeType edgeType) {
		this.edgeType = edgeType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CpdaveGraphNode getSrcNode() {
		return node1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CpdaveGraphNode getDestNode() {
		return node2;
	}
	
	/**
	 * Paints the edge.
	 * 
	 * @param g
	 */
	public void paint(Graphics2D g) {
		Shape edgeShape = getEdgeShape();
		g.setStroke(edgeType.getStroke());
		g.setColor(color);
		g.draw(edgeShape);
	}
	
	/**
	 * Checks whether the edge intersects with the specified rectangle.
	 * 
	 * @param rect
	 * @return
	 */
	public boolean intersects(Rectangle2D rect) {
		return getEdgeShape().intersects(rect);
	}
	
	/**
	 * Returns the line representing the edge.
	 * 
	 * @return
	 */
	protected Shape getEdgeShape() {
		return new Line2D.Double(node1.getX(), node1.getY(), node2.getX(), node2.getY());
	}
	
	/**
	 * Checks whether this edge has the two specified nodes as end points
	 * (regardless of their order).
	 * 
	 * @param nodeA
	 * @param nodeB
	 * @return
	 */
	public boolean checkEndpoints(CpdaveGraphNode nodeA, CpdaveGraphNode nodeB) {
		Object src = getSrcNode();
		Object dst = getDestNode();
		return (src == nodeA && dst == nodeB) || (src == nodeB && dst == nodeA);
	}

}

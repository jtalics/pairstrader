package com.jtalics.onyx.visual.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collections;
import java.util.List;


/**
 * This class defines a node which can be added to the {@link CpdaveGraphModel}
 * class.
 */
public class CpdaveGraphNode {
	
	private static final Stroke STANDARD_STROKE = new BasicStroke(
			1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final Stroke HIGHLIGHT_STROKE = new BasicStroke(
			3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static final Color HIGLIGHT_INNER = new Color(240, 240, 255, 255);
	private static final Color HIGLIGHT_OUTER = new Color(224, 224, 255, 0);
	
	/**
	 * The position of this node. Note that the {@link Point2D} instance
	 * should not be updated after construction since it gets passed to
	 * the {@link CpdaveGraph} class for use it its position map.
	 */
	private final Point2D pos = new Point2D.Double();
	
	/**
	 * The radius of the circle drawn for this node.
	 */
	private double radius = 50;
	
	/**
	 * The color for this node.
	 */
	private Color color1 = Color.LIGHT_GRAY;
	
	/**
	 * A secondary color for use in this node's background gradient.
	 */
	private Color color2 = Color.LIGHT_GRAY.brighter();
	
	/**
	 * Indicates the status (error, warning, etc.) of this node.
	 */
	private CpdaveNodeStatus nodeStatus = CpdaveNodeStatus.NORMAL;
	
	private CpdaveNodeType nodeType = CpdaveNodeType.RECTANGLE;
	
	private boolean collapsed = true;
	
	private String nodeName = null;
	private Object userObject;  // arbitrary object you want the node to carry around
	
	/**
	 * Creates a new graph node at the origin of the graph.
	 */
	public CpdaveGraphNode() {
		setLocation(0, 0);
	}
	
	/**
	 * Creates a new node at the specified location
	 * 
	 * @param x
	 * @param y
	 */
	public CpdaveGraphNode(double x, double y) {
		setLocation(x, y);
	}
	
	/**
	 * Gets the color for this node.
	 * 
	 * @return
	 */
	public Color getColor() {
		return color1;
	}
	
	/**
	 * Sets the color for this node. The second color used in the background
	 * gradient is assigned automatically by making an automatic adjustment
	 * to the main color.
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		this.color1 = color;
		this.color2 = color.brighter();
	}
	
	/**
	 * Gets the position of this node along the X axis.
	 * 
	 * @return
	 */
	public double getX() {
		return pos.getX();
	}
	
	/**
	 * Gets the position of this node along the Y axis.
	 * 
	 * @return
	 */
	public double getY() {
		return pos.getY();
	}

	/**
	 * Sets the radius for this node.
	 * 
	 * @param radius
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	/**
	 * Gets the radius for this node.
	 * 
	 * @return
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Sets the location of this node.
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(double x, double y) {
		this.pos.setLocation(x, y);
	}
	
	/**
	 * Gets the location of this node.
	 * 
	 * @return
	 */
	public Point2D getLocation() {
		return (Point2D)this.pos.clone();
	}
	
	/**
	 * Gets the integral {@link Point2D} object used by this node
	 * to keep track of its position. This method generally should't
	 * be called when trying to determine the position since the
	 * object that's returned is mutable. This method is intended for
	 * use for the {@link CpdaveNodePositionInitializer} class so
	 * that it can assign the actual {@link Point2D} object to the
	 * graph's position map.
	 * 
	 * @return
	 */
	Point2D getMutableLocation() {
		return pos;
	}
	
	/**
	 * Gets the status for this node.
	 * 
	 * @return
	 */
	public CpdaveNodeStatus getNodeStatus() {
		return nodeStatus;
	}
	
	/**
	 * Sets the status for this node.
	 * 
	 * @param nodeStatus
	 */
	public void setNodeStatus(CpdaveNodeStatus nodeStatus) {
		this.nodeStatus = nodeStatus;
	}
	
	/**
	 * Gets the bounds for this node.
	 * 
	 * @param selected
	 * @return
	 */
	public Rectangle2D getBounds(boolean selected) {
		return getNodeShape(0).getBounds2D();
	}
	
	/**
	 * Returns the children of this node.
	 * 
	 * Note: Grouping and children are not currently supported.
	 * 
	 * @return
	 */
	public List<CpdaveGraphNode> getChildren() {
		// Grouping and children are not currently supported.
		return Collections.emptyList();
	}
	
	/**
	 * Adds a node as a child of this node.
	 * 
	 * Note: Grouping and children are not currently supported.
	 * 
	 * @param node
	 */
	public void addChild(CpdaveGraphNode node) {
		// Grouping and children are not currently supported.
	}
	
	/**
	 * Removes a child from this node.
	 * 
	 * Note: Grouping and children are not currently supported.
	 * 
	 * @param node
	 */
	public void removeChild(CpdaveGraphNode node) {
		// Grouping and children are not currently supported.		
	}
	
	/**
	 * Gets the aggregate status for this  node. The aggregate status
	 * for a node takes into consideration its own status as well as
	 * the status of all nodes under it.
	 * 
	 * @return
	 */
	public CpdaveNodeStatus getAggregateStatus() {
		// Recursively determine the aggregate status.
		CpdaveNodeStatus result = this.getNodeStatus();
		for (CpdaveGraphNode child : getChildren()) {
			result = CpdaveNodeStatus.getMax(result, child.getAggregateStatus());
		}
		return result;
	}
	
	/**
	 * Determines whether this node should be sent mouse events at the specified
	 * coordinates. If this method returns true then mouse events at the specified
	 * coordinates should be passed to this node directly and not interpreted as
	 * part of a drag or selection operation (note: not yet implemented). If this
	 * method returns false, then mouse events at the specified coordinates should
	 * be handed by the graph panel directly (potentially resulting in a drag
	 * operation if the specified coordinates intersect with this node as reported
	 * by the {@link #intersects(Rectangle2D, boolean)} method).
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean receivesMouseEventsAt(double x, double y) {
		return false;
	}
	
	/**
	 * Paints this node.
	 * 
	 * @param g
	 * @param selected
	 */
	public void paint(Graphics2D g, boolean selected) {
		Shape nodeShape = getNodeShape(0);
		if (selected) {
			switch(nodeType) {
			case ROUND:
				g.setPaint(new RadialGradientPaint(0, 0, (float)(radius + 10), 
						new float[]{(float)(radius / (radius + 10)), 1.0f },
						new Color[]{HIGLIGHT_INNER, HIGLIGHT_OUTER}));
				break;
			case RECTANGLE:
				g.setPaint(new RadialGradientPaint(0, 0, (float)(radius), 
						new float[]{(float)0f, 1.0f },
						new Color[]{HIGLIGHT_INNER, HIGLIGHT_OUTER}));
				break;
			case OVAL:
				break;
			case SQUARE:
				break;
			default:
				break;
			}
			g.fill(getNodeShape(10));
			g.setPaint(new LinearGradientPaint(0f, (float)-radius, 0f, (float)radius, 
					new float[]{0.0f, 1.0f}, new Color[]{color2, color1}));
			g.setPaint(new LinearGradientPaint(0f, (float)-radius, 0f, (float)radius, 
					new float[]{0.0f, 1.0f}, new Color[]{color2, color1}));
			g.fill(nodeShape);			
			g.setStroke(HIGHLIGHT_STROKE);
			g.setPaint(Color.WHITE);
			g.draw(nodeShape);
		} else {
			g.setPaint(new LinearGradientPaint(0f, (float)-radius, 0f, (float)radius, 
					new float[]{0.0f, 1.0f}, new Color[]{color2, color1}));
			g.fill(nodeShape);			
			g.setStroke(STANDARD_STROKE);
			g.setPaint(Color.BLACK);
			g.draw(nodeShape);			
		}
		
		switch(nodeType) {
		case ROUND:
			if(nodeName != null) {
                // Get the Font metrics
                FontMetrics metrics = g.getFontMetrics();
                int lnWidth = metrics.stringWidth(nodeName);				
				g.drawString(nodeName, 0-(lnWidth/2), 0);
			}
			break;
		case RECTANGLE:
			if(nodeName != null) {
				g.drawString(nodeName, 0, 0);
			}
			break;
		case OVAL:
			break;
		case SQUARE:
			break;
		default:
			break;
		}
	}
	
	/**
	 * Determines whether this node intersects with the specified rectangle.
	 * 
	 * @param rect
	 * @param selected
	 * @return
	 */
	public boolean intersects(Rectangle2D rect, boolean selected) {
		return getNodeShape(0).intersects(rect);
	}
	
	/**
	 * Gets the shape for the outline of this node.
	 * 
	 * @param expand
	 * @return
	 */
	protected Shape getNodeShape(double expand) {
		Shape shape = null;
		switch(nodeType) {
		case ROUND:
			double r = radius + expand;
			shape = new Ellipse2D.Double(-r, -r, 2 * r, 2 * r);
			break;
		case RECTANGLE:
			r = radius + expand;
			shape = new RoundRectangle2D.Double(-r, -r/2, 2 * r, (2 * r)/2, 20, 20);
			break;
		case OVAL:
			break;
		case SQUARE:
			break;
		default:
			break;
		}
		return shape;
	}

	public CpdaveNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(CpdaveNodeType nodeType) {
		this.nodeType = nodeType;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		if(collapsed) {
			nodeType = CpdaveNodeType.RECTANGLE;
		} else {
			nodeType = CpdaveNodeType.ROUND;
		}
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject; 
	}
	
	public Object getUserObject() {
		return this.userObject;
	}
}

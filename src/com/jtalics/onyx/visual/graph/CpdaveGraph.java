package com.jtalics.onyx.visual.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * This class is an extension of {@link GraphView} which is responsible for
 * rendering a graph view.
 */
public class CpdaveGraph extends GraphView {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Fraction value for gradient.
	 */
	private static final float[] BACKGROUND_PAINT_FRACTIONS = new float[]{0.0f, 1.0f};
	
	/**
	 * Background colors for gradient.
	 */
	private static final Color[] BACKGROUND_PAINT_COLORS = new Color[]
			{new Color(224, 224, 224), new Color(196, 196, 196)};
	
	/**
	 * Creates a new graph component with the specified model.
	 * 
	 * @param model
	 */
	public CpdaveGraph(CpdaveGraphModel model) {
		super(model);
		this.setLayoutManager(new CpdaveNodePositionInitializer());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CpdaveGraphModel getModel() {
		return (CpdaveGraphModel)super.getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2D getNodeBounds(Object node, boolean selected) {
		return ((CpdaveGraphNode)node).getBounds(selected);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean intersects(Object node, Rectangle2D rect, boolean selected) {
		return ((CpdaveGraphNode)node).intersects(rect, selected);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean intersects(GraphEdge edge, Rectangle2D rect) {
		return ((CpdaveGraphEdge)edge).intersects(rect);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintNode(Object node, Graphics2D g, boolean selected) {
		((CpdaveGraphNode)node).paint(g, selected);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintEdge(GraphEdge edge, Graphics2D g) {
		((CpdaveGraphEdge)edge).paint(g);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintBackground(Graphics2D g) {
		int w = this.getWidth();
		int h = this.getHeight();
		
		Shape backgroundShape = new Rectangle2D.Float(0, 0, w, h);
		Paint backgroundPaint = new LinearGradientPaint(0, 0, 0, h, 
				BACKGROUND_PAINT_FRACTIONS, BACKGROUND_PAINT_COLORS);
		g.setPaint(backgroundPaint);
		g.fill(backgroundShape);
	}
}

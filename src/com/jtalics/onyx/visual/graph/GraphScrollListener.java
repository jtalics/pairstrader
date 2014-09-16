package com.jtalics.onyx.visual.graph;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;


/**
 * This class defines an adjustment listener to listen for scrolling events,
 * and appropriately pans the view in response to these events.
 */
class GraphScrollListener implements AdjustmentListener {
	
	/**
	 * The graph that this listener is registered with.
	 */
	private final GraphView graph;

	/**
	 * Creates a new scroll listener for the specified graph. 
	 * 
	 * @param graph - The graph that this scroll listener is
	 *                   being created for.
	 */
	GraphScrollListener(GraphView graph) {
		this.graph = graph;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (!this.graph.autoAdjustingScrollBars) {
			double ratio = (double) e.getValue() / GraphView.SCROLL_MAX;
			if (e.getSource() == this.graph.hScroll) {
				double x = ratio * (this.graph.maxX - this.graph.minX) + this.graph.minX;
				this.graph.centerX = x + this.graph.getViewRect().getWidth() / 2;
			} else if (e.getSource() == this.graph.vScroll) {
				double y = ratio * (this.graph.maxY - this.graph.minY) + this.graph.minY;
				this.graph.centerY = y + this.graph.getViewRect().getHeight() / 2;
			} else {
				throw new RuntimeException("unexpected event source");
			}
			this.graph.graphView.repaint();
		} else { /* This is a programatic adjustment, so ignore it. */ ; }
	}
}
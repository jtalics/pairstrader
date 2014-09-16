package com.jtalics.onyx.visual.graph;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Defines a default node layout manager which places all new nodes
 * at the original (0, 0).
 */
final class DefaultNodeLayoutManager implements NodeLayoutManager {
	@Override 
	public Point2D generateNodePos(GraphView graph, GraphModel model,
	                               Object node, Map<Object, Point2D> posMap) {

		return new Point2D.Float(0, 0);
	}
}
package com.jtalics.onyx.visual.graph;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * This class serves as the "layout manager" for the {@link CpdaveGraph} class
 * to ensure that the internal {@link Point2D} object in each node is used by the
 * actual graph class for the node's position data.
 */
public class CpdaveNodePositionInitializer implements NodeLayoutManager {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D generateNodePos(GraphView graph, GraphModel model, Object node, Map<Object, Point2D> posMap) {
		CpdaveGraphNode graphNode = (CpdaveGraphNode)node;
		return graphNode.getMutableLocation();
	}

}

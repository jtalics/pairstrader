package com.jtalics.onyx.visual.graph;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Interface to be implemented by classes that are capable of positioning nodes
 * as they're added to a graph.
 */
public interface NodeLayoutManager {
	
	/**
	 * This method is called when the graph needs a position for a new before it
	 * can display it.
	 * 
	 * @param graph - The graph that sent this message.
	 * @param model - The model of the graph that sent this message.
	 * @param node - The node that needs to be positioned.
	 * @param posMap - The map of positions that the graph is currently using.
	 * @return - The point at which the node should be placed.
	 */
	public Point2D generateNodePos(GraphView graph, GraphModel model, Object node, Map<Object, Point2D> posMap);
}

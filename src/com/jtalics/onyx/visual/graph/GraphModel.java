package com.jtalics.onyx.visual.graph;

import java.util.Collection;

/**
 * Defines the functionality that a graph model must provide.
 */
public interface GraphModel {
	/**
	 * Gets the nodes in the graph model.
	 * 
	 * @return - The nodes in the graph model.
	 */
	public Collection<?> getNodes();
	
	/**
	 * Gets the edges in the graph model.
	 * 
	 * @return - The edges in the graph model.
	 */
	public Collection<? extends GraphEdge> getEdges();
	
	/**
	 * Adds a graph change listener to this model.
	 * 
	 * @param listener - The graph change listener to add.
	 */
	public void addGraphChangeListener(GraphChangeListener listener);
	
	/**
	 * Removes a graph change listener to this model.
	 * 
	 * @param listener - The graph change listener to remove.
	 */
	public void removeGraphChangeListener(GraphChangeListener listener);
}

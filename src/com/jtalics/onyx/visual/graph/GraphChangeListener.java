package com.jtalics.onyx.visual.graph;

/**
 * Interface for classes that listen for changes to a graph.
 */
public interface GraphChangeListener {
	/**
	 * Called when a structural change has occurred in <code>model</code>,
	 * where a structural change is a removal or addition of a node/edge.
	 * An update of an existing node does not trigger this event.
	 * 
	 * @param model - The model in which the change occurred.
	 */
	public void structureChanged(GraphModel model);
}

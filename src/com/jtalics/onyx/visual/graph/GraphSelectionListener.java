package com.jtalics.onyx.visual.graph;

/**
 * Defines the interface for a listener which listeners
 * for updates to the selection in a graph.
 */
public interface GraphSelectionListener {
	
	/**
	 * Called when the selection changes in the graph.
	 * 
	 * @param source - The graph control in which the change occurred.
	 * @param node - The node for which the selection status changed.
	 * @param selected - True if the node became selected, false if the node
	 *                   became unselected.
	 */
	public void selectionChanged(GraphView source, Object node, boolean selected);
	
	/**
	 * Called when the selection is cleared in the graph.
	 * 
	 * @param source - The graph control in which the change occurred.
	 */
	public void selectionCleared(GraphView source);
}

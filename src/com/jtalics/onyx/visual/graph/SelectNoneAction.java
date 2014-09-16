package com.jtalics.onyx.visual.graph;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Defines an action to deselect all nodes in a given graph.
 */
public class SelectNoneAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The graph to deselect nodes in.
	 */
	private final GraphView graph;
	
	/**
	 * Creates a new action to deselect all nodes of the specified graph.
	 * 
	 * @param graph - The graph to deselect all nodes in.
	 */
	public SelectNoneAction(GraphView graph) {
		this.graph = graph;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		graph.clearSelection();
	}
}

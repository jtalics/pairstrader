package com.jtalics.onyx.visual.graph;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Defines an action to select all nodes in a given graph.
 */
public class SelectAllAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The graph to select nodes in.
	 */
	private final GraphView graph;
	
	/**
	 * Creates a new action to select all nodes of the specified graph.
	 * 
	 * @param graph - The graph to select all nodes in.
	 */
	public SelectAllAction(GraphView graph) {
		this.graph = graph;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		graph.clearSelection();
		for (Object node : graph.getModel().getNodes()) {
			graph.setSelected(node, true);
		}
	}
}

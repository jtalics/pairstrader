package com.jtalics.onyx.visual.graph;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implements the {@link #addGraphChangeListener(GraphChangeListener)} method
 * of the {@link GraphModel} interface, and provides the {@link #fireStructuralChangeEvent()}
 * for subclasses to send structure change events to all {@link GraphChangeListener}s.
 * 
 * @author kjhaenft
 */
public abstract class AbstractGraphModel implements GraphModel {

	/**
	 * List of {@link GraphChangeListener}s for this graph model.
	 */
	private Set<GraphChangeListener> listeners = new CopyOnWriteArraySet<>();
	
	/**
	 * Fires a structure change event to all registered {@link GraphChangeListener}s.
	 */
	protected void fireStructuralChangeEvent() {
		for (GraphChangeListener listener : listeners) {
			listener.structureChanged(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addGraphChangeListener(GraphChangeListener listener) {
		listeners.add(listener);
	}
}

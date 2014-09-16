package com.jtalics.onyx.visual.dnd;

/**
 * The Visual Drop Listener
 * 
 */
public interface VisualDropListener {
    /**
     * The component is being dropped
     *
     * @param e = The Visual drop event
     */
	public boolean componentDropped(VisualDropEvent e);
}

package com.jtalics.onyx.visual.resizer;

import java.awt.Component;

/**
 * Component Resizer Interface
 * Contains all of the public resize
 * methods for the VisualGroup
 * @author cpdave
 *
 */
public interface Resizer {
	/** Is this draggable? */
	public boolean isDraggable();
	/** Is this currently resizing? */
    public boolean isResizing();
    /** Was this just resized? */
    public boolean justResized();
    /** Reset resized */
    public void resetResized();
    /** Register this component for resize events */
    public void registerComponent(Component... components); 
    /** Unregister this component for resize events */
    public void deregisterComponent(Component... components);    
}

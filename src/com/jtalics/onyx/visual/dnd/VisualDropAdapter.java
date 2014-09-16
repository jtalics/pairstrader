package com.jtalics.onyx.visual.dnd;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Visual Drop Adapter for drop listeners
 * 
 */
public class VisualDropAdapter extends MouseAdapter {
    // The glass pane
    private VisualGlassPane glassPane;
    // The event action string
	private String action;
	// The drop listeners
	private List<VisualDropListener> listeners = new ArrayList<VisualDropListener>();
	// The user object
	private Object userObject = null;
	// Did the drop get handled
	private boolean handled = false;

	/**
	 * The default constructor
	 *
	 * @param glassPane - The current glassPane
	 * @param action - The Action String
	 */
    public VisualDropAdapter(VisualGlassPane glassPane, String action) {
        this.glassPane = glassPane;
        this.action = action;
    }

    /**
     * Add a Drop Listener
     *
     * @param listener - the new drop listener
     */
    public void addComponentDropListener(VisualDropListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        } else {
            ; // We already have this one
        }
    }

    /**
     * Get Drop Listeners
     *
     * @param listeners - the new drop listener
     */
    public List<VisualDropListener> getComponentDropListeners() {
        return listeners;
    }
    
    /**
     * Remove a Drop Listener
     *
     * @param listener - the drop listener to remove
     */
    public void removeComponentDropListener(VisualDropListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        } else {
            ; // listener is not in the list
        }
    }

    /**
     * Update all the listeners that a drop is happening
     *
     * @param evt - the drop event
     */
    protected void fireComponentDropEvent(VisualDropEvent evt) {
    	// Call all listeners until the drop is handled
        Iterator<VisualDropListener> it = listeners.iterator();
        handled = false;
        while (it.hasNext() && !handled) {
       		handled = it.next().componentDropped(evt);
        }
    }

    /**
     * Get the Action String
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Set the Action String
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Get the Glass Pane for dragging
     * @return the glassPane
     */
    public VisualGlassPane getGlassPane() {
        return glassPane;
    }

    /**
     * Set the Glass Pane for Dragging
     * @param glassPane the glassPane to set
     */
    public void setGlassPane(VisualGlassPane glassPane) {
        this.glassPane = glassPane;
    }

    /**
     * Get the User Object
     * @return the userObject
     */
    public Object getUserObject() {
        return userObject;
    }

    /**
     * Set the User Object
     * @param userObject the userObject to set
     */
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }
}
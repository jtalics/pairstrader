package com.jtalics.onyx.visual.dnd;

import java.awt.Point;

/**
 * The Visual Drop Event
 * 
 */
public class VisualDropEvent {
    // The drop position
	private Point point;
	// The Action string
	private String action;
	// The Source Object
	private Object object;

	/**
	 * The Default Constructor
	 *
	 * @param action - The action string
	 * @param point - The position
	 */
	public VisualDropEvent(String action, Point point) {
		this.action = action;
		this.point = point;
	}

	/**
	 * Get the Action String
	 *
	 * @return - action string
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Get the Drop Location
	 *
	 * @return - the location point
	 */
	public Point getDropLocation() {
		return point;
	}
	
	/**
	 * Get the user Object
	 *
	 * @return - user object
	 */
	public Object getUserObject() {
	    return object;
	}
	
	/**
	 * Set the user Object
	 *
	 * @param obj - the user object
	 */
	public void setUserObject(Object obj) {
	    this.object = obj;
	}
}

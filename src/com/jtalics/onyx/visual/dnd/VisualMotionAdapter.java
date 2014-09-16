package com.jtalics.onyx.visual.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.group.VisualGroup;

/**
 * The Visual Motion Adapter for mouse motion events
 * 
 */
public class VisualMotionAdapter extends MouseMotionAdapter
{
    // The glass pane
    private VisualGlassPane glassPane;

    /**
     * The Default Constructor
     *
     * @param glassPane
     */
	public VisualMotionAdapter(VisualGlassPane glassPane) {
		this.glassPane = glassPane;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void mouseDragged(MouseEvent e)
    {
    	boolean continueDrag = true;
	    // Get the component
        Component c = e.getComponent();
        if(c instanceof VisualGroup) {
        	VisualGroup group = (VisualGroup)c;
        	if(group.isResizing()) {
        		continueDrag = false;
        	} else {
        		; // leave it as it was
        	}
        } else {
        	; // nothing to do if not a Visual Group
        }
        if(continueDrag) {
	        // Get a copy of the position
	        Point p = (Point) e.getPoint().clone();
	        // Convert the point to screen coordinates
	        SwingUtilities.convertPointToScreen(p, c);
	        // Convert the point to glassPane coordinates
	        SwingUtilities.convertPointFromScreen(p, glassPane);
	        // set the point
	        glassPane.setPoint(p);
	        // repaint
	        glassPane.repaint();
        } else {
        	; // don't do anything
        }
    }
}
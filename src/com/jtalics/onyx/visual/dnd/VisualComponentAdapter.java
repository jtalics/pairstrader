package com.jtalics.onyx.visual.dnd;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.group.VisualGroup;

/**
 * The Visual Component Adapter
 * 
 */
public class VisualComponentAdapter extends VisualDropAdapter
{
    /**
     * The Default Constructor
     *
     * @param glassPane - The glass pane
     * @param action - The component action string
     */
    public VisualComponentAdapter(VisualGlassPane glassPane, String action) {
        super(glassPane, action);
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e)
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
        	; // we only handle VisualGroup - nothing to do
        }
        if(continueDrag) {
	        // get the buffered image of teh component
	        BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
	        // get the graphics for the image
	        Graphics g = image.getGraphics();
	        // paint the image
	        c.paint(g);
	        // set the glass pane visible
	        getGlassPane().setVisible(true);
	        // get the component position
	        Point p = (Point) e.getPoint().clone();
	        // convert to screen coordinates
	        SwingUtilities.convertPointToScreen(p, c);
	        // convert coordinates from the glass pane
	        SwingUtilities.convertPointFromScreen(p, getGlassPane());
	        // set the point for the glass pane
	        getGlassPane().setPoint(p);
	        // set the image for the glass pane
	        getGlassPane().setImage(image);
	        // repaint the glass pane
	        getGlassPane().repaint();
        } else {
        	; // don't do anything
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e)
    {
	    // Get the component
        Component c = e.getComponent();
        // get the component position
        Point p = (Point) e.getPoint().clone();
        // convert to screen coordinates
        SwingUtilities.convertPointToScreen(p, c);
        // copy and convert the component position using the glass pane
        Point eventPoint = (Point) p.clone();
        SwingUtilities.convertPointFromScreen(p, getGlassPane());
        // Set the point on the glass pane
        getGlassPane().setPoint(p);
        getGlassPane().setVisible(false);
        getGlassPane().setImage(null);
        // Create a Visual Drop Event
        VisualDropEvent visualDropEvent = new VisualDropEvent(getAction(), eventPoint);
        visualDropEvent.setUserObject(e);
        // Update the listeners
        fireComponentDropEvent(visualDropEvent);
    	/**
    	 * Get a copy of the current groups
    	 * Ensure all groups are handled
    	 */
    	for(VisualGroup group : VisualGroup.getGroupList()) {
    		// Stop the loop when the drop is handled
   			if(group.componentDropped(visualDropEvent)) {
   				// End the loop, if the Drop is handled
   				break;
   			} else {
   				; // continue until the drop is handled
   			}
    	}
    }
}
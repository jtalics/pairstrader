package com.jtalics.onyx.visual.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.group.VisualGroup;

/**
 * The Visual Picture Adapter for mouse events
 * 
 */
public class VisualPictureAdapter extends VisualDropAdapter
{
    // The buffered image
	private BufferedImage image;
	// Can the glass pane accept drops from drags
	private boolean canAcceptDrops = false;
	/**
	 * The Default Constructor
	 *
	 * @param glassPane - The glass pane for this adapter
	 * @param action - The Event action String
	 * @param picture - The image url
	 */
	public VisualPictureAdapter(VisualGlassPane glassPane, String action, String picture, boolean canAcceptDrop) {
	   super(glassPane, action);
	   this.canAcceptDrops = canAcceptDrop;
	   try {
	       // Create the image
	       this.image = ImageIO.read(new BufferedInputStream(new FileInputStream(picture)));
	   } catch (MalformedURLException mue) {
	       throw new IllegalStateException("Invalid picture URL.");
	   } catch (IOException ioe) {
           throw new IllegalStateException("Invalid picture or picture URL.");
       }
	}

	/**
	 * 
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
        	; // nothing to do if not a Visual Group
        }
        if(continueDrag) {
	        // Set the glass pane to visible
	        getGlassPane().setVisible(true);
	        // Get a copy of the component position
	        Point p = (Point) e.getPoint().clone();
	        // Convert the position to screen coordinates
	        SwingUtilities.convertPointToScreen(p, c);
	        // Convert to glass pane coordinates
	        SwingUtilities.convertPointFromScreen(p, getGlassPane());
	        // Set the point on the glass pane
	        getGlassPane().setPoint(p);
	        // Set the image on the class pane
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
        // Get a copy of the component position
        Point p = (Point) e.getPoint().clone();
        // Convert the position to screen coordinates
        SwingUtilities.convertPointToScreen(p, c);
        // Get a copy of the component position
        Point eventPoint = (Point) p.clone();
        // Convert to glass pane coordinates
        SwingUtilities.convertPointFromScreen(p, getGlassPane());
        // Set the point on the glass pane
        getGlassPane().setPoint(p);
        // hide the glass pane
        getGlassPane().setVisible(false);
        // destroy the image
        getGlassPane().setImage(null);
        // Do not try to drop the component if the glass pane cannot accept drops
        if(canAcceptDrops) {
	        // create a VisualDropEvent
	        VisualDropEvent visualDropEvent = new VisualDropEvent(getAction(), eventPoint);
	        // Set the MouseEvent as the object
	        visualDropEvent.setUserObject(e);
	        // Update the listeners
	        fireComponentDropEvent(visualDropEvent);
	    	/**
	    	 * Get a copy of the current groups
	    	 * Ensure all groups are handled
	    	 */
	    	for(VisualGroup group : VisualGroup.getGroupList()) {
	   	    	// Loop for all Visual Groups
	   			if(group.componentDropped(visualDropEvent)) {
	   				// End the loop, if the Drop is handled
	   				break;
	   			} else {
	   				; // continue loop until drop is handled or we reach the end of the loop
	   			}
	    	}
        } else {
        	; // nothing to do if we cannot accept the drop
        }
    }
}
package com.jtalics.onyx;

import java.awt.Color;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import com.jtalics.onyx.visual.zoom.ZoomHandler;


/**
 * The Main Panel for Onyx.
 */
public class MainPanel extends JPanel implements MouseWheelListener
{
	/* The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    /* The errors. */
    private String errors = "Errors Found";
    /* The parent. */
    private Main parent = null;
    
	/* Width and height of this drawing */
	private int height = 820;
	private int width = 1100;
	
	/* The Zoom Action Listener */
	private ZoomHandler graphicZoom = null;
   
    /**
     * Instantiates a new workflow panel3.
     *
     * @param parent the parent
     */
    public MainPanel(Main parent) {
        this.setFocusable(true);
    	// Intialize the workflow
    	this.parent = parent;
    	setBackground(Color.white);
        addMouseWheelListener(this);
        // Add the Key listener
        setOpaque(true);
        setDoubleBuffered(true);
        /**
         *  Create our zoom Action Listener with the applet, panel, and the drawing width and height
         *  This class handles all of our zoom work
         */
        graphicZoom = new ZoomHandler(parent, this, width, height);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}

	/**
	 * Sets the errors.
	 *
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}
    
	/**
	 * Fit to the Window Size
	 */
	public void fitToWindow() {
		getWorkflowZoom().fitToWindow();
	}

    /**
     * @return the workflowZoom
     */
    public ZoomHandler getWorkflowZoom() {
        return graphicZoom;
    }

    /**
     * @param workflowZoom the workflowZoom to set
     */
    public void setWorkflowZoom(ZoomHandler workflowZoom) {
        this.graphicZoom = workflowZoom;
    }

}

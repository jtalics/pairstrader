package com.jtalics.onyx.visual.zoom;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.jtalics.onyx.Main;


/**
 * The listener interface for receiving zoomAction events. The class that is
 * interested in processing a zoomAction event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addZoomActionListener<code> method. When
 * the zoomAction event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ZoomActionEvent
 */
public class ZoomHandler implements ActionListener {

    /* Is this resizing */
    /** The resizing. */
    private Boolean resizing = false;
    /* The Parent Applet */
    /** The parent. */
    private Main parent = null;
    /* The Workflow Panel */
    /** The panel. */
    private JPanel panel = null;
    /* The scale. */
    /** The scale. */
    private double scale = 0.6;
    /* timer for fit to window */
    /** The timer. */
    private Timer timer = new Timer(100, this);
    /* is this zooming out */
    /** The zooming out. */
    private Boolean zoomingOut = false;
    /* Is this a manual zoom from buttons or mouse wheel */
    /** The manual zoom. */
    private Boolean manualZoom = false;
    /* Width and height of this drawing */
    /** The height. */
    private int height = 820;
    
    /** The width. */
    private int width = 1100;
    
    /**
     * Default Constructor.
     * 
     * @param parent
     *            - The Workflow Applet
     * @param panel
     *            - The Current Workflow Panel
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public ZoomHandler(Main parent, JPanel panel, int width, int height) {
        this.parent = parent;
        this.panel = panel;
        this.width = width;
        this.height = height;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(timer.isRunning()) {
            // Continue if this is an auto fit to window
            if(!getManualZoom() && !resizing){
                // zooming out
                if(zoomingOut) {
                    zoom(-1);
                    // we are done when scroll bars are hidden
                    if(!parent.isScrollbarsVisible()) {
                        zoom(1);
                        timer.stop();
                        parent.invalidate();
                        parent.repaint();
                    } else {
                        ; // nothing to do
                    }
                } else {
                    // zooming in
                    zoom(1);
                    // we are done when scroll bars are displayed
                    if(parent.isScrollbarsVisible()) {
                        zoom(-1);
                        zoom(-1);
                        timer.stop();
                        parent.invalidate();
                        parent.repaint();
                    } else {
                        ; // nothing to do
                    }
                }
            } else {
                ; // nothing to do
            }
        } else {
            ; // we only listen for timer events
        }
    }

    /**
     * Zoom in or out.
     * 
     * @param type
     *            the type
     */
    public void zoom(int type) {
        if(parent.isInitialized()) {
            // Zoom Out
            if(type <= 0) {
                if(getScale() > 0.15) {
                    setScale(getScale() - 0.05);
                } else {
                    ; // nothing to do if we have zoomed all the way out
                }
            } else {
                // Zoom In
                if(getScale() < 3.0) {
                    setScale(getScale() + 0.05);
                } else {
                    ; // nothing to do if we have zoomed all the way in
                }
            }
            changePreferredSize();
            panel.invalidate();
            panel.revalidate();
            panel.repaint();
        } else {
            ; // don't do anything unless the applet is initialized
        }
    }
    
    /**
     * Change the Prefered Size of the Panel.
     */
    public void changePreferredSize() {
        Point dest = new Point();
        // Get the Graphics2D
        Graphics2D g2d = (Graphics2D)panel.getGraphics();
        
        if(g2d != null) {
            // Set the current scale
            g2d.scale(getScale(), getScale());
            // Get the Transform
            AffineTransform transform = g2d.getTransform();
            Point point = new Point(width, height);
            transform.transform(point, dest);
            panel.setPreferredSize(new Dimension(dest.x, dest.y));
        } else {
            ; // nothing to do
        }
        parent.invalidate();
        parent.repaint();
    }

    /**
     * Gets the scale.
     * 
     * @return the scale
     */
    public double getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     * 
     * @param scale
     *            the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;
    }
    
    /**
     * Fit to the Window Size.
     */
    public void fitToWindow() {
        if(parent.isScrollbarsVisible()) {
            zoomingOut = true;
        } else {
            zoomingOut = false;
        }
        // reset the start time
        if(!timer.isRunning()) {
            // this is not a manual zoom
            setManualZoom(false);
            timer.start();
        } else {
            ; // nothing to do
        }
    }

    /**
     * Gets the manual zoom.
     * 
     * @return the manualZoom
     */
    public Boolean getManualZoom() {
        return manualZoom;
    }

    /**
     * Sets the manual zoom.
     * 
     * @param manualZoom
     *            the manualZoom to set
     */
    public void setManualZoom(Boolean manualZoom) {
        this.manualZoom = manualZoom;
    }
    
}

package com.jtalics.onyx.visual.dnd;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * The Visual Glass Pane
 * 
 * 
 * @author rsbradsh
 */
public class VisualGlassPane extends JPanel
{
	/*
     * Default Serial Version UID
     */
    private static final long serialVersionUID = 1L;
    //  Used for transparency
    private AlphaComposite composite;
    // Is the image being dragged
    private BufferedImage dragged = null;
    // Screen location
    private Point location = new Point(0, 0);

    /**
     * The Default Constructor
     */
    public VisualGlassPane()
    {
        // this must be false for transparency to work
        setOpaque(false);
        // set to half transparency
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    }

    /**
     * Set the dragged image
     *
     * @param dragged
     */
    public void setImage(BufferedImage img)
    {
        this.dragged = img;
    }

    /**
     * Set the Screen location
     *
     * @param location
     */
    public void setPoint(Point loc)
    {
        this.location = loc;
    }

    /**
     * Get the Screen location
     *
     * @param location
     */
    public Point getPoint()
    {
        return location;
    }
    
    /**
     * {@inheritDoc}
     */
    public void paintComponent(Graphics g)
    {
        // don't paint if there is no drag
        if (dragged != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(composite);
            g2.drawImage(dragged,
                         ((int) (location.getX()) - (dragged.getWidth(this)  / 2)),
                         ((int) (location.getY()) - (dragged.getHeight(this) / 2)),
                         null);
        } else {
            ; // nothing to do if not dragged
        }
    }
}
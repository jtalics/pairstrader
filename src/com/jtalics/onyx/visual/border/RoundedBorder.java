package com.jtalics.onyx.visual.border;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.jtalics.onyx.visual.dnd.VisualGlassPane;


public class RoundedBorder implements Border {
    // The radius of the border
    private int radius;

    /**
     * The Default Constructor
     * @param radius - The radius of the round border
     */
    public RoundedBorder(int radius) {
        this.radius = radius;
    }
    
    /**
     * Get the Insets of the Border
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }
 
    /**
     * Get the border apacity
     */
    public boolean isBorderOpaque() {
        return true;
    }
    
    /**
     * The Distance left until this group overlaps with its parent
     *
     * @param parent - The parent container
     * @param child - The child container
     * @return - distance left
     */
    public int distanceLeft(Component parent, Component child) {
    	// Get the parent container radius
        int parentRadius = parent.getWidth()/2;
        // Get the child container radius
        int childRadius = child.getWidth()/2;
        // Get the child container x and y coordinates
        int childX = child.getX();
        int childY = child.getY();
        // Get the child container x and y center coordinates
        int childCenterX = childX + childRadius;
        int childCenterY = childY + childRadius;
        // Get the parent container x and y center coordinates 
        int parentCenterX = parentRadius;
        int parentCenterY = parentRadius;
        // Formula to get the distance of the inner circle compared to the outer circle
        double distance2 = Math.sqrt((parentCenterX - childCenterX) * (parentCenterX - childCenterX) + (parentCenterY - childCenterY) * (parentCenterY - childCenterY));
        //if(distance2 + childRadius <= parentRadius) {
        //    ; // Child is Inside Parent - Room Left = "+(parentRadius-(distance2+childRadius))
        //} else {
        //    ; // Child is NOT Fully Inside Parent 
        //}
        return (int)(parentRadius-(distance2+childRadius));
    }

    /**
     * Is this group completely contained within its parent
     *
     * @param parent - The parent container
     * @param child - The child container
     * @return - boolean isInside
     */
    public boolean isInside(Component parent, Component child) {
    	// the return var
        boolean isInside = true;
        // The parent radius
        int parentRadius = parent.getWidth()/2;
        // The Child Radius
        int childRadius = child.getWidth()/2;
        // Get the child container center x and y
        int childX = child.getX();
        int childY = child.getY();
        int childCenterX = childX + childRadius;
        int childCenterY = childY + childRadius;
        // Get the parent container center x and y
        int parentCenterX = parentRadius;
        int parentCenterY = parentRadius;
        // Formula to get the distance of the inner circle compared to the outer circle
        double distance2 = Math.sqrt((parentCenterX - childCenterX) * (parentCenterX - childCenterX) + (parentCenterY - childCenterY) * (parentCenterY - childCenterY));
        if(distance2 + childRadius <= parentRadius) {
            ; // Child is Inside Parent - Room Left = "+(parentRadius-(distance2+childRadius))
        } else {
            ; // Child is NOT Inside Parent - Room Left = "+(parentRadius-(distance2+childRadius)) 
            isInside = false;
        }
        return isInside;
    }
    
    /**
     * Is this group completely contained within its parent
     * This uses the VisualGlassPane Point instead of the child point
     *
     * @param parent - The parent VisualGroup
     * @param child - The Child Visual Group
     * @return
     */
    public boolean isInside(VisualGlassPane glassPane, Component parent, Component child) {
    	// Is the child component moving on the glass pane inside the parent component not on the glass pane
        boolean isInside = true;
        // The Parent Radius
        int parentRadius = parent.getWidth()/2;
        // The Child Radius
        int childRadius = child.getWidth()/2;
        // The Original GlassPane Point
        Point glassPoint = glassPane.getPoint();
        // Convert the glass pane center point to screen coordinates
        SwingUtilities.convertPointToScreen(glassPoint, glassPane);
        // Convert the glass pane center point to parent coordinates
        SwingUtilities.convertPointFromScreen(glassPoint, parent);
        // Parent Center point
        int parentCenterX = parentRadius;
        int parentCenterY = parentRadius;
        // Get the distance from the outside child circle to the edge of the parent circle
        double distance2 = Math.sqrt((parentCenterX - glassPoint.x) * (parentCenterX - glassPoint.x) + (parentCenterY - glassPoint.y) * (parentCenterY - glassPoint.y));
        if(distance2 + childRadius <= parentRadius) {
            ; // Child is Inside Parent - Room Left = "+(parentRadius-(distance2+childRadius))
        } else {
            ; // Child is NOT Inside Parent - Room Left = "+(parentRadius-(distance2+childRadius)) 
            isInside = false;
        }
        return isInside;
    }

    /**
     * Is the center x of this group on
     * the left of the parent center x
     *
     * @param parent - The parent container
     * @param child - The child container
     * @return - Is the drag from the left
     */
    public boolean isLeft(Component parent, Component child) {
    	// return var
        boolean isLeft = true;
        // Get the parent radius
        int parentRadius = parent.getWidth()/2;
        // Get the child radius and center x and y
        int childRadius = child.getWidth()/2;
        int childX = child.getX();
        int childCenterX = childX + childRadius;
        // Get the parent center coordinates
        int parentCenterX = parentRadius;
        // if child center x is less than parent center x - isLeft = true
        if(childCenterX < parentCenterX) {
            isLeft = true;
        } else {
            isLeft = false;
        }
        return isLeft;
    }

    /**
     * Is the center y of this group on
     * the top of the parent center y
     *
     * @param parent
     * @param child
     * @return
     */
    public boolean isTop(Component parent, Component child) {
    	// return var
        boolean isTop = true;
        // Get the parent radius
        int parentRadius = parent.getWidth()/2;
        // Get the child radius and center x and y
        int childRadius = child.getWidth()/2;
        int childY = child.getY();
        int childCenterY = childY + childRadius;
        // Get the parent center coordinates
        int parentCenterY = parentRadius;
        // if child center y is less than parent center y - isTop = true
        if(childCenterY < parentCenterY) {
            isTop = true;
        } else {
            isTop = false;
        }
        return isTop;
    }
    
    /**
     * Paint a round border 
     * {@inheritDoc}
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Set the color and draw the border
        g.setColor(Color.BLACK);
        g.drawOval(x+3, y+7, width-1-5, height-1-9);
    }
}
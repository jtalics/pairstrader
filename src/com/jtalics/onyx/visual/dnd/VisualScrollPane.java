package com.jtalics.onyx.visual.dnd;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JScrollPane;


/**
 * The Visual Scroll Pane for the Palette(s)
 * The allows for the viewport to be painted
 * 
 */
public class VisualScrollPane extends JScrollPane {

    // The Default Serial Version UID
    private static final long serialVersionUID = 1L;
    // The second color
    private Color color2;
    // The first color (white)
    private Color color1 = new Color(0xff,0xff,0xff);

    /**
     * Default Constructor
     *
     * @param color - The second color
     */
    public VisualScrollPane(Color color) {
        color2 = color;
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        // Get teh Graphics2D
        Graphics2D g2d = (Graphics2D) g;
        //to get height and width of the component
        int w = getWidth();
        int h = getHeight();
        //generating two colors for gradient pattern
        /*generating gradient pattern from two colors*/
        GradientPaint gp = new GradientPaint( 0, 0, color1, 0, h, color2 );
        g2d.setPaint( gp ); //set gradient color to graphics2D object
        g2d.fillRect( 0, 0, w, h ); //filling color
        setOpaque( false );
    }
    
}

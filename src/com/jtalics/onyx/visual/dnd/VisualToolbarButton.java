package com.jtalics.onyx.visual.dnd;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


/**
 * The Visual toolbar button
 */
public class VisualToolbarButton extends JButton implements ComponentListener, MouseListener {

    // The default Serial Version UID
    private static final long serialVersionUID = 1L;
    // Is the button currently selected
    private boolean selected = false;
    // The Modeless Dialog Box Palette
    private JDialog palette = null;
    // The Modeless Dialog Box Palette Title
    private String title;
    // The Number of Dialog Box Palettes open
    private static int numberOpened = 0;
    // Has the mouse entered this button
    private boolean entered = false;
    // Is this the first time
    private boolean isFirstTime = true;
    // Is the mouse been released
    private boolean isDone = true;
    // Has the mouse been pushed
    private boolean isStarted = false;
    // The Drag N Drop Type
    private VisualDragNDropType dndType;
    // The first color (white)
    private Color color1 = new Color(0xff,0xff,0xff);
    // The glass pane for the Modeless Dialog Box Palettes
    private VisualGlassPane dialogGlassPane = new VisualGlassPane();
    /**
     * The Default Constructor
     *
     * @param normalIcon
     * @param rolloverIcon
     * @param pressedIcon
     */
    public VisualToolbarButton(String normalIconUrl, String pressedIconUrl, 
            JFrame frame, VisualGlassPane frameGlassPane, VisualDropListener listener, VisualDragNDropType type,
            Map<String, String> iconMap, VisualDropListener trashCan) {
        // Set the icon for the button
        this.setIcon(new ImageIcon(normalIconUrl));
        // Set the pressed icon
        this.setPressedIcon(new ImageIcon(pressedIconUrl));
        // Set the selected icon
        this.setSelectedIcon(new ImageIcon(pressedIconUrl));
        // Add this as a mouse listener
        this.addMouseListener(this);
        // Create the Palette for this button
        palette = new JDialog(frame);
        // Set the glass pane for the palette
        palette.setGlassPane(dialogGlassPane);
        // Set the palette title
        title = type.stringValue();
        palette.setTitle(title);
        // set the palette size
        palette.setSize(160, 400);
        // Set to hide when user closes the palette
        palette.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        // Ensure this is always on top
        palette.setAlwaysOnTop(true);
        // Create the panel with icons and drag N drop features for the palette
        palette.add(new VisualToolbarPalette(dialogGlassPane, frameGlassPane, listener, type, iconMap, trashCan));
        // Add this as a component listener for palette events
        palette.addComponentListener(this);
        // set the Drag N Drop type
        this.dndType = type;
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        // Continue if the Drag N Drop type is Not Trash 
        if(dndType != VisualDragNDropType.Trash) {
            // Get the Graphics2D
            Graphics2D g2d = (Graphics2D) g;
            //generating two colors for gradient pattern
            Color color2;
            // if this button is selected get the Drag N Drop color - else use default color
            if(selected) {
                color2 = dndType.getColor();
            } else {
                color2 = new Color(0xd1,0xd2,0xd7);
            }
            /*generating gradient pattern from two colors*/
            int w = getWidth();
            int h = getHeight();
            // Paint the face of the button - covers the rollover icon so one button can be used for all buttons
            GradientPaint gp = new GradientPaint( 7, 7, color1, w-26, h-20, color2 );
            g2d.setPaint( gp ); //set gradient color to graphics2D object
            // Paint the unselected portion of the button
            if(!selected) {
                g2d.fillRect( 7, 7, w-26, h-20 ); //filling color
            } else {
                // Paint the selected portion of the button
                g2d.fillRect( 9, 9, w-26, h-20 ); //filling color
            }
            // continue if the button is enabled - else leave it gray
            if(isEnabled()) {
                if(entered || selected) {
                    // change text to black
                    g2d.setColor(Color.black);
                } else {
                    // change text to gray
                    g2d.setColor(Color.gray);
                }
                if(!selected) {
                    // Draw the text for unselected button
                    g2d.drawString(title, 10, 19);
                } else {
                    // Draw the text for selected button
                    g2d.drawString(title, 12, 21);
                }
            } else {
                // gray color and draw text
                g2d.setColor(Color.gray);
                g2d.drawString(title, 10, 19);
            }
        } else {
            ; // nothing to do if this is not for the Trash Can
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(ComponentEvent e) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(ComponentEvent e) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(ComponentEvent e) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(ComponentEvent e) {
        // unselect button
        selected = false;
        setSelected(false);
        // repaint
        invalidate();
        validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if(isEnabled() && !selected) {
            // mouse pressed
            isStarted = true;
            // we are not done
            isDone = false;
            // the button is now selected
            selected = true;
            // set it selected
            setSelected(true);
            // show the palette modeless dialog box
            palette.setVisible(true);
            // increment number opened
            VisualToolbarButton.incrementNumberOpened();
            // If this is the first time it has been shown
            if(isFirstTime) {
                isFirstTime = false;
                // Set the location
                palette.setLocation((100 + (26*numberOpened)), (60 + (26*numberOpened)));
            } else {
                ; // Display it where ever the user last put it
            }
        } else {
            ; // don't do anything if not enabled
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Is the mouse currently pressed and it is not finished
        if(isStarted && !isDone) {
            // We are now done
            isStarted = false;
            isDone = true;
        } else if(isEnabled() && selected) {
                // Hide the palette and set selected to false - decrement number opened
                selected = false;
                setSelected(false);
                palette.setVisible(false);
                VisualToolbarButton.decrementNumberOpened();
        } else {
            ; // don't do anything if not enabled
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Set entered to true
        entered = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // set entered to false
        entered = false;
    }
    
    /**
     * Increment the number of windows opened
     */
    private static void incrementNumberOpened() {
        numberOpened++;
    }
    
    /**
     * Decrement the number of windows opened
     */
    private static void decrementNumberOpened() {
        numberOpened--;
    }
    
}

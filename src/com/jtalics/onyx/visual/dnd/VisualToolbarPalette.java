package com.jtalics.onyx.visual.dnd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The Visual Toolbar Pallete is responsible for
 * holding all of the images in a Scrollpane and
 * for the Drag N Drop features
 * 
 */
public class VisualToolbarPalette extends JPanel {

    // Default Serial Version UID
    private static final long serialVersionUID = 1L;
    // The Drag N Drop Type
    private VisualDragNDropType dndType;
    // The second color
    private Color color2;
    // The first color (white)
    private Color color1 = new Color(0xff,0xff,0xff);
    // The scroll pane
    private VisualScrollPane scrollPane;
    // The Visual Picture Adapter
    private VisualPictureAdapter pictureAdapter;
    // create a list to display the JLabels with Icons
    private List<VisualPaletteLabel> imageLabelList = new ArrayList<VisualPaletteLabel>();
    // The Visual Drop Listeners 
    private static List<VisualDropListener> listeners = new ArrayList<VisualDropListener>();
    
    /**
     * The Default Constructor
     *
     * @param dialogGlassPane - The Dialog Box Palette Glass Pane
     * @param frameGlassPane - The Main Frame Glass Pane
     * @param tlgListener - The Top Level Group Drop Listener
     * @param type - The Drag N Drop type
     * @param iconMap - The Name/URL icon map
     * @param trashCan - The Trash Can Drop Listener
     */
    public VisualToolbarPalette(VisualGlassPane dialogGlassPane,VisualGlassPane frameGlassPane, VisualDropListener tlgListener, 
            VisualDragNDropType type, Map<String, String> iconMap, VisualDropListener trashCan) {
        // Set border layout
        setLayout(new BorderLayout());
        // Set Drag N Drop type
        dndType = type;
        // Set Drag N Drop Color
        color2 = dndType.getColor();
        // Set the Scroll Pane
        scrollPane = new VisualScrollPane(color2);
        // need to do this so we can paint it
        scrollPane.getViewport().setOpaque(false);
        // Update the Images from the map
        if(iconMap != null && iconMap.size() > 0) {
            // Get the Entry Set
            Set<Entry<String, String>> set = iconMap.entrySet();
            // loop for each entry
            for(Entry<String, String> entry : set) {
                // Get the key
                String key = entry.getKey();
                // Get the value
                String value = entry.getValue();
                // Update the Image list with a new JLabel
                imageLabelList.add(createLabel(key, value, dialogGlassPane, frameGlassPane, tlgListener, trashCan));
            }

            // Create the Box to hold all of the JLabels
            Box box = Box.createVerticalBox();
            box.add(Box.createHorizontalGlue());
            box.add(Box.createVerticalGlue());
            // Loop through all labels and add them to the box
            for(JLabel label : imageLabelList) {
                box.add(label);
            }
            box.add(Box.createHorizontalGlue());
            box.add(Box.createVerticalGlue());
            // add the box to the scroll pane
            scrollPane.getViewport().add(box);
            // add the scroll pane to this panel
            add(scrollPane);
        } else {
            ; // nothing to do if the map is empty
        }
    }
    
    /**
     * Create and return the JLabel for the palette
     *
     * @param name - JLabel Text
     * @param iconUrl - Icon Url String
     * @param dialogGlassPane - Dialog Box Glass Pane
     * @param frameGlassPane - The Main Frame Glass Pane
     * @param tlgListener - The Top Level Group Drop Listener
     * @param trashCan - The Trash Can Drop Listener
     * @return
     */
    private VisualPaletteLabel createLabel(String name, String iconUrl, VisualGlassPane dialogGlassPane,VisualGlassPane 
            frameGlassPane, VisualDropListener tlgListener, VisualDropListener trashCan) {
        // Create a new JLabel
    	VisualPaletteLabel label = new VisualPaletteLabel(new ImageIcon(iconUrl));
        // Set label name
        label.setText(name);
        // Create a Picture Adapter for the Dialog Glass Pane
        pictureAdapter = new VisualPictureAdapter(dialogGlassPane, name, iconUrl, false);
        // Add the picture Adapter as the mouse listener
        label.addMouseListener(pictureAdapter);
        // Create a Picture Adapter for the Main Frame Glass Pane
        pictureAdapter = new VisualPictureAdapter(frameGlassPane, name, iconUrl, true);
        // Add the picture Adapter as the mouse listener
        label.addMouseListener(pictureAdapter);
        // Always Add the Top Level Group as a drop listener
        pictureAdapter.addComponentDropListener(tlgListener);
        // Always Add the Trash Can as a drop listener
        pictureAdapter.addComponentDropListener(trashCan);
        // Add the Mouse Motion Listeners
        label.addMouseMotionListener(new VisualMotionAdapter(dialogGlassPane));
        label.addMouseMotionListener(new VisualMotionAdapter(frameGlassPane));
        // Place the text Bottom Center
        label.setVerticalTextPosition(SwingConstants.BOTTOM);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        return label;
    }
    
    /**
     * Add any Drop Listeners
     *
     * @param listener
     */
    public static void addDropListener(VisualDropListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        } else {
            ; // do not add if we already have it
        }
    }

    /**
     * Remove any Drop Listeners
     *
     * @param listener
     */
    public static void removeDropListener(VisualDropListener listener) {
        if(listeners.contains(listener) && listeners.size() > 2) {
            listeners.remove(listener);
        } else {
            ; // do not remove the topLevelGroupListener
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        // Get the Graphics2D
        Graphics2D g2d = (Graphics2D) g;
        //to get height and width of the component
        int w = getWidth();
        int h = getHeight();
        //generating two colors for gradient pattern
        /*generating gradient pattern from two colors*/
        GradientPaint gp = new GradientPaint( 0, 0, color1, 0, h, color2 );
        g2d.setPaint( gp ); //set gradient color to graphics2D object
        g2d.fillRect( 0, 0, w, h ); //filling color
        setOpaque( true );
    }

}
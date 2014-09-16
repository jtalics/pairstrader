package com.jtalics.onyx.visual.dnd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JToolBar;

/**
 * The Floatable Visual Toolbar
 * 
 */
public class VisualToolbar extends JToolBar {
    // Default Serial Version UID 
    private static final long serialVersionUID = 1L;
    // Import Files Button 
    private VisualToolbarButton importButton = null;
    // IP Subnet Button 
    private VisualToolbarButton ipSubnetButton = null;
    // Radio Button    
    private VisualToolbarButton radioButton = null;
    // Preset Templates Button
    private VisualToolbarButton presetTemplatesButton = null;
    // Voice call book button
    private VisualToolbarButton vcbButton = null;
    // IP Address button
    private VisualToolbarButton ipAddressButton = null;
    // Group button
    private VisualToolbarButton groupButton = null;
    // Icon Button
    private VisualToolbarButton iconButton = null;
    // Export Files button
    private VisualToolbarButton exportButton = null;
    // These images are used for all buttons
    private String iconUrl = "images/BlankButton.gif";
    // Pressed icon
    private String iconPressedUrl = "images/BlankButtonPressed.gif";
    
    /**
     * Default Constructor for the Visual tool bar
     *
     * @param frame - T - name/url pairhe main frame for the application
     * @param listener - The Top Level Group Listener
     * @param importIconMap - name/url pair
     * @param ipSubnetIconMap - name/url pair
     * @param radioIconMap - name/url pair
     * @param presetIconMap - name/url pair
     * @param vcbIconMap - name/url pair
     * @param ipAddressIconMap - name/url pair
     * @param groupIconMap - name/url pair
     * @param iconIconMap - name/url pair
     * @param exportIconMap - name/url pair
     */
    public VisualToolbar(JFrame frame, VisualGlassPane glassPane, VisualJLabelComponent trashCan, 
            VisualDropListener listener, Map<String, String> importIconMap,
            Map<String, String> ipSubnetIconMap, Map<String, String> radioIconMap, Map<String, String> presetIconMap,
            Map<String, String> vcbIconMap, Map<String, String> ipAddressIconMap, Map<String, String> groupIconMap,
            Map<String, String> iconIconMap, Map<String, String> exportIconMap) {
        // Create all of the toolbar buttons - drag n drop features and drag dialog boxes for each button
        importButton = new VisualToolbarButton(iconUrl, iconPressedUrl, frame, glassPane, listener, VisualDragNDropType.Import, importIconMap, trashCan);
        ipSubnetButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.IPSubnet, ipSubnetIconMap, trashCan);
        radioButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.Radio, radioIconMap, trashCan);
        presetTemplatesButton = new VisualToolbarButton(iconUrl, iconPressedUrl, frame, glassPane, listener, VisualDragNDropType.PresetTemplate, presetIconMap, trashCan);
        vcbButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.VoiceCallBook, vcbIconMap, trashCan);
        ipAddressButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.IPAddress, ipAddressIconMap, trashCan);
        groupButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.Group, groupIconMap, trashCan);
        iconButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.Icon, iconIconMap, trashCan);
        exportButton = new VisualToolbarButton(iconUrl, iconPressedUrl,frame, glassPane, listener, VisualDragNDropType.Export, exportIconMap, trashCan);
       
        
        //importButton.setEnabled(false);
        //exportButton.setEnabled(false);
        
        // Add all of the buttons to this toolbar
        add(Box.createHorizontalGlue());
        add(Box.createVerticalGlue());
        add(new JToolBar.Separator());
        add(importButton);
        add(new JToolBar.Separator());
        add(Box.createHorizontalGlue());
        add(Box.createVerticalGlue());
        add(ipSubnetButton);
        add(new JToolBar.Separator());
        add(radioButton);
        add(new JToolBar.Separator());
        add(presetTemplatesButton);
        add(new JToolBar.Separator());
        add(vcbButton);
        add(new JToolBar.Separator());
        add(ipAddressButton);
        add(new JToolBar.Separator());
        add(groupButton);
        add(new JToolBar.Separator());
        add(iconButton);
        add(Box.createHorizontalGlue());
        add(Box.createVerticalGlue());
        add(new JToolBar.Separator());
        add(exportButton);
        add(Box.createHorizontalGlue());
        add(Box.createVerticalGlue());
        trashCan.setMaximumSize(new Dimension(90, 40));
        add(trashCan);
        add(Box.createVerticalGlue());
        add(Box.createHorizontalGlue());
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
        Color color1 = new Color(0xff,0xff,0xff);
        Color color2 = new Color(0xff,0xff,0xd1);
        /*generating gradient pattern from two colors*/
        GradientPaint gp = new GradientPaint( 0, 0, color1, 0, h, color2 );
        g2d.setPaint( gp ); //set gradient color to graphics2D object
        g2d.fillRect( 0, 0, w, h ); //filling color
        setOpaque( true );
    }
    
}

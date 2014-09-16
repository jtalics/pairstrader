package com.jtalics.onyx.visual.dnd;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;                                         // Imported Libraries 
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.group.VisualGroup;

/**
 * The Visual Top Level Group is responsible for containing
 * all other visual components and for the drag n drop features
 * with the components
 * 
 */
public class VisualJPanelComponent extends JPanel implements VisualDropListener, MouseListener, MouseMotionListener {

    // Default Serial Version UID
    private static final long serialVersionUID = 1L;
    // Has the mouse entered this panel
    private boolean canDrop = false;
    // The Main Frame Glass Pane
    private VisualGlassPane frameGlassPane;
    // the Trash Can
    private VisualJLabelComponent trashCan;
    // Menu Bar Height
    private int menubarHeight = 21;
    // Other height to deal with
    private int tabHeight = 44;
    
    /**
     * Default Constructor for the Top Level Group
     */
    public VisualJPanelComponent(VisualGlassPane glassPane, VisualJLabelComponent trashCan) {
        // Set the Layout, mouse listeners, glass pane, and trash can
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        frameGlassPane = glassPane;
        this.trashCan = trashCan;
        //menubarHeight = CpdaveApplication.getMenuBarSize().height;
    }

    /**
     * Default Constructor for the Top Level Group
     */
    public VisualJPanelComponent(LayoutManager layout, VisualGlassPane glassPane, VisualJLabelComponent trashCan) {
    	super(layout);
        // Set the Layout, mouse listeners, glass pane, and trash can
        this.setLayout(null);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        frameGlassPane = glassPane;
        this.trashCan = trashCan;
        //menubarHeight = CpdaveApplication.getMenuBarSize().height;
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
        /*parameters are concentration of Red, Blue and Green color in HEX  format*/
        Color color1 = new Color(0xff,0xff,0xff);
        Color color2 = new Color(0xfa,0xec,0xe1);
        /*generating gradient pattern from two colors*/
        GradientPaint gp = new GradientPaint( 0, 0, color1, 0, h, color2 );
        g2d.setPaint( gp ); //set gradient color to graphics2D object
        g2d.fillRect( 0, 0, w, h ); //filling color
        setOpaque( false );
        setOpaque( true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean componentDropped(VisualDropEvent e) {
    	// Did the drop get handled
    	boolean handled = false;
        // Continue if we can drop here
        if(canDrop) {
            // Get the user object
            Object obj = e.getUserObject();
            if(obj instanceof MouseEvent) {
                MouseEvent event = (MouseEvent)obj;
                // Get the source object
                Object sourceObject = event.getSource();
                if(sourceObject instanceof VisualPaletteLabel) {
                	VisualPaletteLabel paletteLabel = (VisualPaletteLabel)sourceObject;
                    // continue if this is not the parent of the component
                	if(paletteLabel.getText() != null && paletteLabel.getText().equals("GROUP")) {
                		handled = true;
                        // Create a new Visual Component for the drop
                        VisualGroup group = new VisualGroup(frameGlassPane, trashCan, this);
                        // add the component
                        this.add(group);
                        // Get the Point and convert it from the glass pane
                        int height = 200;
                        int width = 200;
                        Point point = e.getDropLocation();
                        SwingUtilities.convertPointFromScreen(point, frameGlassPane);
                        // Set the component position
                        group.setBounds(point.x-(width/2), point.y-(height/2), width, height);
                        // Add the Drag N Drop Features for the component
                        VisualComponentAdapter componentAdapter = new VisualComponentAdapter(frameGlassPane, "Group");
                        componentAdapter.addComponentDropListener(group);
                        // Add the Top Level Panel as a drop listener for this component
                        componentAdapter.addComponentDropListener(this);
                        // Add the Trash Can as a drop listener for this component
                        componentAdapter.addComponentDropListener(trashCan);
                        // Add the Top Level Glass Pane as a mouse motion listener for this component
                        group.addMouseMotionListener(new VisualMotionAdapter(frameGlassPane));
                        group.addMouseListener(componentAdapter);
                	} else {
                		handled = true;
                        // Create a new Visual Component for the drop
                        VisualJLabelComponent label = new VisualJLabelComponent(paletteLabel.getIcon(), VisualDragNDropType.Radio);
                        // add the component
                        this.add(label);
                        // Get the Point and convert it from the glass pane
                        int height = paletteLabel.getHeight();
                        int width = paletteLabel.getWidth();
                        Point point = e.getDropLocation();
                        SwingUtilities.convertPointFromScreen(point, frameGlassPane);
                        // Set the component position
                        label.setBounds(point.x-(width/2), (point.y-(height/2)) -menubarHeight-tabHeight, width, height);
                        // Add the Top Level Glass Pane as a mouse motion listener for this component
                        label.addMouseMotionListener(new VisualMotionAdapter(frameGlassPane));
                	}                        
                } else if(sourceObject instanceof VisualGroup) {
            		handled = true;
                	// Add the source
                	VisualGroup group = (VisualGroup) sourceObject;
                	if(!group.justResized() && !group.isResizing()) {
	                    int height = group.getHeight();
	                    int width = group.getWidth();
	                    Point point = e.getDropLocation();
	                    SwingUtilities.convertPointFromScreen(point, frameGlassPane);
	                    // Set the component position
	                    group.setBounds(point.x-(width/2), point.y-(height/2)-menubarHeight-tabHeight, width, height);
	                    if(!group.getParent().equals(this)) {
	                    	this.add(group);
	                    } else {
	                    	; // The group already belongs to this
	                    }
                	} else {
                		group.resetResized();
                	}
            		group.setVisible(true);
                } else if(sourceObject instanceof VisualJLabelComponent) {
            		handled = true;
                	// Add the source
                	VisualJLabelComponent vLabel = (VisualJLabelComponent) sourceObject;
                    int height = vLabel.getHeight();
                    int width = vLabel.getWidth();
                    Point point = e.getDropLocation();
                    SwingUtilities.convertPointFromScreen(point, frameGlassPane);
                    // Set the component position
                    vLabel.setBounds(point.x-(width/2), point.y-(height/2)-menubarHeight-tabHeight, width, height);
                    this.add(vLabel);
                    vLabel.setVisible(true);
                } else {
                	; // We only handle the above components
                }

            } else {
                ; // this did not come from the palette
            }
        } else {
            ; // nothing to do if the mouse hasn't entered
        }
        return handled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent e) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        canDrop = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        canDrop = false;
    }
}
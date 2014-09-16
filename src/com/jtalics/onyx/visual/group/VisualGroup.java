package com.jtalics.onyx.visual.group;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.dnd.VisualDragNDropType;
import com.jtalics.onyx.visual.dnd.VisualDropEvent;
import com.jtalics.onyx.visual.dnd.VisualDropListener;
import com.jtalics.onyx.visual.dnd.VisualGlassPane;
import com.jtalics.onyx.visual.dnd.VisualJLabelComponent;
import com.jtalics.onyx.visual.dnd.VisualJPanelComponent;
import com.jtalics.onyx.visual.dnd.VisualMotionAdapter;
import com.jtalics.onyx.visual.dnd.VisualPaletteLabel;
import com.jtalics.onyx.visual.border.RoundedBorder;
import com.jtalics.onyx.visual.dnd.VisualComponentAdapter;
import com.jtalics.onyx.visual.resizer.ComponentResizer;
import com.jtalics.onyx.visual.resizer.Resizer;


/**
 * This is the Visual Group Container that can contain
 * any other group of groups or icons
 * 
 */
public class VisualGroup extends JPanel implements VisualDropListener, MouseListener, MouseMotionListener, MouseWheelListener {

    // Default Serial Version UID
	private static final long serialVersionUID = 1L;
    // The first color (white)
    private Color color1 = new Color(0xff,0xff,0xff);
    // Has the mouse entered this panel
    private boolean canDrop = false;
    // The Main Frame Glass Pane
    private VisualGlassPane frameGlassPane;
    // The Trash Can
    private VisualJLabelComponent trashCan;
    // The Group Resizer
    private static Resizer groupResizer = new ComponentResizer();
    // Menu Bar Height
    private int menubarHeight = 21;
    // Other Height to take into consideration
    private int adjustHeight = 24;
    // The List of Groups
    private static List<VisualGroup> groupList = new ArrayList<VisualGroup>();
    // The Top-Level Group 
    private VisualJPanelComponent topLevelPanel;
    
    /**
     * The Default Constructor
     * @param glassPane - The Main Frame Glass Pane
     * @param trashCan - The Trash Can
     * @param tlPanel - The Top-Level Group
     */
    public VisualGroup(VisualGlassPane glassPane, VisualJLabelComponent trashCan, VisualJPanelComponent tlPanel) {
    	// This must not be opaque
        setOpaque(false);
        // Set the Rounded Border
        setBorder(new RoundedBorder(100));
        // Set the Main Frame Glass Pane
        this.frameGlassPane = glassPane;
        // Set the Trash Can
        this.trashCan = trashCan;
        // Add this as a mouse listener
        this.addMouseListener(this);
        // Add this as a mouse motion listener
        this.addMouseMotionListener(this);
        // Add this to the static list
        groupList.add(this);
        // Set the Top-Level Group
        topLevelPanel = tlPanel;
        // Set the null layout
        this.setLayout(null);
        // Add this as a mouse wheel listener for resizing
        this.addMouseWheelListener(this);
        // Register this for resize events
        groupResizer.registerComponent(this);
    }
    
    /**
     * Register a group for resize events
     * @param group
     */
    public static void registerVisualGroupResizer(VisualGroup group) {
        groupResizer.registerComponent(group);
    }
    
    /**
     * Unregister a group for resize events
     * @param group
     */
    public static void deregisterVisualGroupResizer(VisualGroup group) {
        groupResizer.deregisterComponent(group);
    }
    
    /*
     * Get the second color for this group
     */
    private Color getColor() {
    	// The return color
        Color color;
        // Are we done?
        boolean done = false;
        // Group level
        int level = 0;
        // Get the parent
        Container parent = this.getParent();
        // Continue until we are at the Top-Level Group
        while(!done) {
            if(parent instanceof VisualGroup) {
            	// increment the level and get the parent
                level++;
                parent = parent.getParent();
            } else {
            	// we are done
                done = true;
            }
        }
        // Change the color according to the group level
        switch(level) {
        case 0:
            color = new Color(0xdb9370);
            break;
        case 1:
            color = new Color(0x8e6b23);
            break;
        case 2:
            color = new Color(0xebc79e);
            break;
        case 3:
            color = new Color(0xfbe9dd);
            break;
        case 4:
            color = new Color(0xafd2f3);
            break;
        case 5:
            color = new Color(0xf7cb77);
            break;
        default:
            color = new Color(0x80ffff);
            break;
        }
        return color;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Get the Graphics2D
        Graphics2D g2d = (Graphics2D) g;
        //generating two colors for gradient pattern
        /*generating gradient pattern from two colors*/
        GradientPaint gp = new GradientPaint( 0, 0, color1, 0, this.getHeight(), getColor() );
        g2d.setPaint( gp ); //set gradient color to graphics2D object
        g2d.fillOval(3, 8, this.getWidth()-8, this.getHeight()-11 ); //filling color
    }

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
                if(sourceObject != this) {
	                if(sourceObject instanceof VisualPaletteLabel) {
	                	VisualPaletteLabel paletteLabel = (VisualPaletteLabel)sourceObject;
	                    // continue if this is not the parent of the component
	                	if(paletteLabel.getText() != null && paletteLabel.getText().equals("GROUP")) {
	                		handled = true;
	                        // Create a new Visual Component for the drop
	                        VisualGroup group = new VisualGroup(frameGlassPane, trashCan, topLevelPanel);
	                        // Get the Point and convert it from the glass pane
	                        int height = 200;
	                        int width = 200;
	                        Point point = e.getDropLocation();
	                        SwingUtilities.convertPointFromScreen(point, frameGlassPane);
	                        point = SwingUtilities.convertPoint(topLevelPanel, point.x, point.y, this);
	                        // Set the component position
	                        group.setBounds(point.x-(width/2), (point.y-(height/2)) -menubarHeight-adjustHeight, width, height);
	                        // Add the Drag N Drop Features for the component
	                        VisualComponentAdapter componentAdapter = new VisualComponentAdapter(frameGlassPane, "Group");
	                        
	                        group.addMouseListener(componentAdapter);
	                        componentAdapter.addComponentDropListener(group);
	                        // Add the Top Level Panel as a drop listener for this component
	                        componentAdapter.addComponentDropListener(topLevelPanel);
	                        // Add the Trash Can as a drop listener for this component
	                        componentAdapter.addComponentDropListener(trashCan);
	                        // Add the Top Level Glass Pane as a mouse motion listener for this component
	                        group.addMouseMotionListener(new VisualMotionAdapter(frameGlassPane));
	                        if(group.getParent() == null || !group.getParent().equals(this)) {
		                        // add the component
		                        this.add(group);
	                        } else {
	                        	; // do not add the group to this parent
	                        }
	                	} else {
	                		handled = true;
	                        // Create a new Visual Component for the drop
	                        VisualJLabelComponent label = new VisualJLabelComponent(paletteLabel.getIcon(), VisualDragNDropType.Radio);
	                        // Get the Point and convert it from the glass pane
	                        int height = paletteLabel.getHeight();
	                        int width = paletteLabel.getWidth();
	                        Point point = e.getDropLocation();
	                        SwingUtilities.convertPointFromScreen(point, frameGlassPane);
	                        point = SwingUtilities.convertPoint(topLevelPanel, point.x, point.y, this);
	                        // Set the component position
	                        label.setBounds(point.x-(width/2), (point.y-(height/2)) -menubarHeight-adjustHeight, width, height);
	                        // Add the Drag N Drop Features for the component
	                        VisualComponentAdapter componentAdapter = new VisualComponentAdapter(frameGlassPane, label.getText());
	                        label.addMouseListener(componentAdapter);
	                        // Add the Top Level Panel as a drop listener for this component
	                        componentAdapter.addComponentDropListener(topLevelPanel);
	                        // Add the Trash Can as a drop listener for this component
	                        componentAdapter.addComponentDropListener(trashCan);
	                        // Add the Top Level Glass Pane as a mouse motion listener for this component
	                        label.addMouseMotionListener(new VisualMotionAdapter(frameGlassPane));
	                        // add the component
	                        this.add(label);
	                	}                        
	                } else if(sourceObject instanceof VisualGroup) {
                		handled = true;
	                	VisualGroup group = (VisualGroup) sourceObject;
	                	if(!group.isResizing() && !group.justResized()) {
    	                    // Get the Point and convert it from the glass pane
    	                    int height = group.getHeight();
    	                    int width = group.getWidth();
    	                    Point p = e.getDropLocation();
    	                    SwingUtilities.convertPointFromScreen(p, frameGlassPane);
    	                    p = SwingUtilities.convertPoint(topLevelPanel, p.x, p.y, this);
    	                    // Set the component position
    	                    group.setBounds(p.x-(width/2), (p.y-(height/2)) - menubarHeight-adjustHeight, width, height);
	                	} else {
	                		group.resetResized();
	                	}
	                	if(!this.equals(group.getParent())) {
	                		this.add(group);
	                	} else {
	                		; // do not add the group to this parent
	                	}
	                    group.setVisible(true);
	                } else if(sourceObject instanceof VisualJLabelComponent) {
                		handled = true;
	                	VisualJLabelComponent vLabel = (VisualJLabelComponent) sourceObject;
	            		// Add the component to this group
	                    // Get the Point and convert it from the glass pane
	                    int height = vLabel.getHeight();
	                    int width = vLabel.getWidth();
	                    Point point = e.getDropLocation();
	                    SwingUtilities.convertPointFromScreen(point, frameGlassPane);
	                    point = SwingUtilities.convertPoint(topLevelPanel, point.x, point.y, this);
	                    // Set the component position
	                    vLabel.setBounds(point.x-(width/2), (point.y-(height/2)) - menubarHeight-adjustHeight, width, height);
	                    // add the component
	                    this.add(vLabel);
	                    vLabel.setVisible(true);
	                } else {
	                	; // this is all we handle at this point
	                }
	            } else {
	                    ; // we only handle JLabel from the palette
	            }
            } else {
            	; // don't do anything if this is the same object
            }
        } else {
            ; // nothing to do if the mouse hasn't entered
        }
        return handled;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
        // continue if this is a Visual Component
        Object obj = e.getSource();
        if(obj instanceof Component) {
            // get the component
            Component component = (Component)obj;
            if(!isResizing()) {
            	if(!isDraggable()) {
		            // Hide the component until the drag is dropped - the glass pane will handle it
		            component.setVisible(false);
            	} else {
            		; // leave it visible
            	}
            } else {
            	; // else do not hide it
            }
        } else {
            ; // don't worry about any other type of object
        }
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		canDrop = true;
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		canDrop = false;
	}
	
	/**
	 * Remove ourselves from the VisualGroup List
	 */
	public static void removeFromList(VisualGroup group) {
		if(groupList.contains(group)) {
			groupList.remove(group);
		} else {
			; // groupList does not contain the group
		}
	}
	
	/**
	 * Get the Group List
	 * @return - the groupList
	 */
	public static final List<VisualGroup> getGroupList() {
		// Return a Copy of the list
		return new ArrayList<VisualGroup>(groupList);
	}
	
	/**
	 * Is this group currently resizing
	 * @return
	 */
	public boolean isResizing() {
		return groupResizer.isResizing();
	}
	
	/**
	 * Was this group recently resized
	 * @return
	 */
	public boolean justResized() {
		return groupResizer.justResized();
	}
	
	/**
	 * Reset resized
	 */
	public void resetResized() {
		if(groupResizer != null) {
			groupResizer.resetResized();
		} else {
			; // nothing to do if null
		}
	}
	
	/**
	 * Is this group in a draggable state
	 * @return
	 */
	public boolean isDraggable() {
		return groupResizer.isDraggable();
	}

	/**
	 * Resize this group with the mouse wheel
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// multiple the amount times 20
        int amount =  e.getWheelRotation() * 20;
        // get the x, y, width and height
        int width = this.getWidth();
        int height = this.getHeight();
        int x = this.getX();
        int y = this.getY();
        width = width + (amount/2);
        height = height + (amount/2);
        x -= (amount/4);
        y -= (amount/4);
        // change the size of this group
        this.setBounds(x, y, width, height);
        repaint();
	}
}

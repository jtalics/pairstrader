package com.jtalics.onyx.visual.dnd;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.jtalics.onyx.visual.group.VisualGroup;


/**
 * A Visual Component for the dragging of a palette icon
 * 
 */
public class VisualJLabelComponent extends JLabel implements VisualDropListener, MouseListener, MouseMotionListener  {

    /**
     * Default Serial Version UID
     */
    private static final long serialVersionUID = 1L;
    // Has the mouse entered this panel
    private boolean trashCanDrop = false;
    // The drag n drop type
    private VisualDragNDropType dndType;
    // Mouse Entered
    private boolean entered = false;
    
    /**
     * The default constructor
     *
     * @param icon - The JLable icon
     * @param glassPane - the frame glass pane
     * @param type - the Drag N Drop type
     */
    public VisualJLabelComponent(Icon icon, VisualDragNDropType type) {
        super(icon);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        dndType = type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean componentDropped(VisualDropEvent e) {
    	boolean handled = false;
        /*
         * If this is dropped onto the trash can - remove it
         */
        if(trashCanDrop) {
    		handled = true;
            // get the object from a mouse event 
            if(e.getUserObject() instanceof MouseEvent) {
                MouseEvent event = (MouseEvent)e.getUserObject();
                Object obj = event.getSource();
                // continue if this is a visual component
                if(obj instanceof VisualJLabelComponent) {
                    VisualJLabelComponent label = (VisualJLabelComponent)obj;
                    // get the parent
                    Container parent = label.getParent();
                    // remove the object
                	parent.remove(label);
                    entered = false;
                    trashCanDrop = false;
                    invalidate();
                    validate();
                } else if(obj instanceof VisualGroup) {
            		handled = true;
                    VisualGroup group = (VisualGroup)obj;
                    // get the parent
                    Object parentObj = group.getParent();
                    // remove the object
                    ((Container)parentObj).remove(group);
                    VisualGroup.removeFromList(group);
                    entered = false;
                    trashCanDrop = false;
                    invalidate();
                    validate();
                } else {
                	; // nothing to do if not one of the components we handle
                }
            } else {
                ; // ignore it if not a mouse event
            }
        } else {
            // get the object from a mouse event 
            if(e.getUserObject() instanceof MouseEvent) {
                MouseEvent event = (MouseEvent)e.getUserObject();
                Object obj = event.getSource();
                // continue if this is a visual component
                if(obj instanceof VisualJLabelComponent) {
                    VisualJLabelComponent component = (VisualJLabelComponent)obj;
                    component.setVisible(true);
                } else {
                    ; // we only support the VisualComponent
                }
            } else {
                ; // ignore it if not a mouse event
            }
        }
        return handled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // continue if this is a Visual Component
        Object obj = e.getSource();
        if(obj instanceof VisualJLabelComponent) {
            // get the component
            VisualJLabelComponent component = (VisualJLabelComponent)obj;
            // Hide the component until the drag is dropped - the glass pane will handle it
            component.setVisible(false);
        } else {
            ; // we only support the Visual Component
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        ; // not using
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        ; // not using
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        ; // not using
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
        // Mouse Entered
        entered = true;
        // continue if this is a visual component
        if(e.getComponent() instanceof VisualJLabelComponent) {
            VisualJLabelComponent component = (VisualJLabelComponent)e.getComponent();
            // is this a Trash type
            if(component.getDndType().equals(VisualDragNDropType.Trash)) {
                // we have entered the trash can
                trashCanDrop = true;
            } else {
                ; // ignore it if not a trash can drop
            }
        } else {
            ; // ignore it if not a visual component
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // we have exited the trash can
        trashCanDrop = false;
        // Mouse Exited
        entered = false;
        invalidate();
        validate();
    }
    
    /**
     * Get the Drag n Drop type
     *
     * @return - the Drag n Drop type
     */
    public VisualDragNDropType getDndType() {
        return dndType;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if(entered && dndType == VisualDragNDropType.Trash) {
            super.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1, false));  //appling border to JPanel
        } else {
            super.setBorder(null);
        }
        setOpaque( false );
    }
    
    
}

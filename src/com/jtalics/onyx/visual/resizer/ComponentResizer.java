package com.jtalics.onyx.visual.resizer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.jtalics.onyx.visual.border.RoundedBorder;
import com.jtalics.onyx.visual.group.VisualGroup;

/**
 *  The ComponentResizer allows you to resize a component by dragging a border
 *  of the component.
 */
public class ComponentResizer extends MouseAdapter implements Resizer
{
	// minimum size
	private final static Dimension MINIMUM_SIZE = new Dimension(30, 30);
	// maximum size
	private final static Dimension MAXIMUM_SIZE =
		new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	// Static Cursor Map
	private static Map<Integer, Integer> cursors = new HashMap<Integer, Integer>();
	
	static {
		cursors.put(1, Cursor.N_RESIZE_CURSOR);
		cursors.put(2, Cursor.W_RESIZE_CURSOR);
		cursors.put(4, Cursor.S_RESIZE_CURSOR);
		cursors.put(8, Cursor.E_RESIZE_CURSOR);
		cursors.put(3, Cursor.NW_RESIZE_CURSOR);
		cursors.put(9, Cursor.NE_RESIZE_CURSOR);
		cursors.put(6, Cursor.SW_RESIZE_CURSOR);
		cursors.put(12, Cursor.SE_RESIZE_CURSOR);
	}
	
	// Drag Insets
	private Insets dragInsets;
	// Snap Size
	private Dimension snapSize;
	// Drag Direction
	private int direction;
	protected static final int NORTH = 1;
	protected static final int WEST = 2;
	protected static final int SOUTH = 4;
	protected static final int EAST = 8;
	// Source Cursor
	private Cursor sourceCursor;
	// Is resizing
	private boolean resizing;
	// source bounds
	private Rectangle bounds;
	// Pressed point
	private Point pressed;
	// Auto Scrolling
	private boolean autoscrolls;
	// Minimum size dimension
	private Dimension minimumSize = MINIMUM_SIZE;
	// Maximum size dimension
	private Dimension maximumSize = MAXIMUM_SIZE;
	// Was this just resized?
	private boolean justResized = false;
	// Number to increase the drag size (> 1)
	private int dragMultiplier = 5;
	// Is this currently draggable?
	private boolean isDraggable = false;

	/**
	 *  Convenience contructor. All borders are resizable in increments of
	 *  a single pixel. Components must be registered separately.
	 */
	public ComponentResizer()
	{
		this(new Insets(10, 10, 10, 10), new Dimension(1, 1));
	}

	/**
	 *  Convenience contructor. All borders are resizable in increments of
	 *  a single pixel. Components can be registered when the class is created
	 *  or they can be registered separately afterwards.
	 *
	 *  @param components components to be automatically registered
	 */
	public ComponentResizer(Component... components)
	{
		this(new Insets(10, 10, 10, 10), new Dimension(1, 1), components);
	}

	/**
	 *  Convenience contructor. Eligible borders are resisable in increments of
	 *  a single pixel. Components can be registered when the class is created
	 *  or they can be registered separately afterwards.
	 *
	 *  @param dragInsets Insets specifying which borders are eligible to be
	 *                    resized.
	 *  @param components components to be automatically registered
	 */
	public ComponentResizer(Insets dragInsets, Component... components)
	{
		this(dragInsets, new Dimension(1, 1), components);
	}

	/**
	 *  Create a ComponentResizer.
	 *
	 *  @param dragInsets Insets specifying which borders are eligible to be
	 *                    resized.
	 *  @param snapSize Specify the dimension to which the border will snap to
	 *                  when being dragged. Snapping occurs at the halfway mark.
	 *  @param components components to be automatically registered
	 */
	public ComponentResizer(Insets dragInsets, Dimension snapSize, Component... components)
	{
		setDragInsets( dragInsets );
		setSnapSize( snapSize );
		registerComponent( components );
	}

	/**
	 *  Get the drag insets
	 *
	 *  @return  the drag insets
	 */
	public Insets getDragInsets()
	{
		return dragInsets;
	}

	/**
	 *  Set the drag dragInsets. The insets specify an area where mouseDragged
	 *  events are recognized from the edge of the border inwards. A value of
	 *  0 for any size will imply that the border is not resizable. Otherwise
	 *  the appropriate drag cursor will appear when the mouse is inside the
	 *  resizable border area.
	 *
	 *  @param  dragInsets Insets to control which borders are resizeable.
	 */
	public void setDragInsets(Insets dragInsets)
	{
		validateMinimumAndInsets(minimumSize, dragInsets);

		this.dragInsets = dragInsets;
	}

	/**
	 *  Get the components maximum size.
	 *
	 *  @return the maximum size
	 */
	public Dimension getMaximumSize()
	{
		return maximumSize;
	}

	/**
	 *  Specify the maximum size for the component. The component will still
	 *  be constrained by the size of its parent.
	 *
	 *  @param maximumSize the maximum size for a component.
	 */
	public void setMaximumSize(Dimension maximumSize)
	{
		this.maximumSize = maximumSize;
	}

	/**
	 *  Get the components minimum size.
	 *
	 *  @return the minimum size
	 */
	public Dimension getMinimumSize()
	{
		return minimumSize;
	}

	/**
	 *  Specify the minimum size for the component. The minimum size is
	 *  constrained by the drag insets.
	 *
	 *  @param minimumSize the minimum size for a component.
	 */
	public void setMinimumSize(Dimension minimumSize)
	{
		validateMinimumAndInsets(minimumSize, dragInsets);

		this.minimumSize = minimumSize;
	}

	/**
	 *  Remove listeners from the specified component
	 *
	 *  @param editor  the component the listeners are removed from
	 */
	public void deregisterComponent(Component... components)
	{
		// loop for all components and remove this as a listener
		for (Component component : components)
		{
			component.removeMouseListener( this );
			component.removeMouseMotionListener( this );
		}
	}

	/**
	 *  Add the required listeners to the specified component
	 *
	 *  @param editor  the component the listeners are added to
	 */
	public void registerComponent(Component... components)
	{
		// loop for all components and add this as a listener
		for (Component component : components)
		{
			component.addMouseListener( this );
			component.addMouseMotionListener( this );
		}
	}

	/**
	 *	Get the snap size.
	 *
	 *  @return the snap size.
	 */
	public Dimension getSnapSize()
	{
		return snapSize;
	}

	/**
	 *  Control how many pixels a border must be dragged before the size of
	 *  the component is changed. The border will snap to the size once
	 *  dragging has passed the halfway mark.
	 *
	 *  @param snapSize Dimension object allows you to separately spcify a
	 *                  horizontal and vertical snap size.
	 */
	public void setSnapSize(Dimension snapSize)
	{
		this.snapSize = snapSize;
	}

	/**
	 *  When the components minimum size is less than the drag insets then
	 *	we can't determine which border should be resized so we need to
	 *  prevent this from happening.
	 */
	private void validateMinimumAndInsets(Dimension minimum, Insets drag)
	{
		// get the minimum width and height
		int minimumWidth = drag.left + drag.right;
		int minimumHeight = drag.top + drag.bottom;
		// throw and exception if this is less than the allowed minimum width or height
		if (minimum.width  < minimumWidth ||  minimum.height < minimumHeight)
		{
			String message = "Minimum size cannot be less than drag insets";
			throw new IllegalArgumentException( message );
		} else {
			; // continue - everything is ok
		}
	}

	/**
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		// Get the component
		Component source = e.getComponent();
		// Get the mouse point
		Point location = e.getPoint();
		// Set the drag direction
		direction = 0;

		if (location.x < dragInsets.left) {
			direction += WEST;
		} else if (location.x > source.getWidth() - dragInsets.right - 1) {
			direction += EAST;
		} else if (location.y < dragInsets.top) {
			direction += NORTH;
		} else if (location.y > source.getHeight() - dragInsets.bottom - 1) {
			direction += SOUTH;
		} else {
			; // nothing to do - we only handle the above
		}
		//  Mouse is no longer over a resizable border
		if (direction == 0)
		{
			source.setCursor( sourceCursor );
		}
		else  // use the appropriate resizable cursor
		{
			int cursorType = cursors.get( direction );
			Cursor cursor = Cursor.getPredefinedCursor( cursorType );
			source.setCursor( cursor );
			isDraggable = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Set the cursor back to normal if not resizing
		if (! resizing)
		{
			Component source = e.getComponent();
			sourceCursor = source.getCursor();
			isDraggable = false;
		} else {
			; // leave it as it was
		}
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// Set the cursor back to normal if done
		if (! resizing)
		{
			Component source = e.getComponent();
			source.setCursor( sourceCursor );
			isDraggable = false;
		} else {
			; // leave it as it was
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		//	The mouseMoved event continually updates this variable
		if (direction != 0) {
	
			//  Setup for resizing. All future dragging calculations are done based
			//  on the original bounds of the component and mouse pressed location.
			resizing = true;
			// Get the point and convert to screen coordinates
			Component source = e.getComponent();
			pressed = e.getPoint();
			SwingUtilities.convertPointToScreen(pressed, source);
			bounds = source.getBounds();
	
			//  Making sure autoscrolls is false will allow for smoother resizing
			//  of components
			if (source instanceof JComponent)
			{
				JComponent jc = (JComponent)source;
				autoscrolls = jc.getAutoscrolls();
				jc.setAutoscrolls( false );
			} else {
				; // We only handle JComponent
			}
		} else {
			; // don't do anything if direction == 0
		}
	}

	/**
	 *  Restore the original state of the Component
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// If we were resizing, set justResized to true
		if(resizing) {
			justResized = true;
		} else {
			; // leave it as it was
		}
		resizing = false;
		// Get the source component
		Component source = e.getComponent();
		// Set the cursor back to normal
		source.setCursor( sourceCursor );
		// continue if the source is a JComponent
		if (source instanceof JComponent)
		{
			((JComponent)source).setAutoscrolls( autoscrolls );
		} else {
			; // we only handle JComponent
		}
	}

	/**
	 *  Resize the component ensuring location and size is within the bounds
	 *  of the parent container and that the size is within the minimum and
	 *  maximum constraints.
	 *
	 *  All calculations are done using the bounds of the component when the
	 *  resizing started.
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		// Continue if we are resizing
		if (resizing) {
			// Get the source and point and convert to screen coordinates
			Component source = e.getComponent();
			Point dragged = e.getPoint();
			SwingUtilities.convertPointToScreen(dragged, source);
			// Update the component size
			changeBounds(source, direction, bounds, pressed, dragged);
		} else {
			; // don't do anything
		}
	}

	/**
	 * Update the size of the source object based on the drag
	 * @param source - The source object
	 * @param direction - The direction of the drag
	 * @param bounds - The bounds of the source object
	 * @param pressed - The original pressed point before the drag
	 * @param current - The current point of the drag
	 */
	protected void changeBounds(Component inSource, int inDirection, Rectangle inBounds, Point inPressed, Point inCurrent)
	{
		//  Start with original location and size
		int x = inBounds.x;
		int y = inBounds.y;
		int width = inBounds.width;
		int height = inBounds.height;
		int drag = 0;
		
		
		if (WEST == (inDirection & WEST))
		{
			// Resizing the West or North border affects the size and location
			drag = getDragDistance(inPressed.x, inCurrent.x, snapSize.width);
			int maximum = Math.min(width + x, maximumSize.width);
			drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);
		} else if (NORTH == (inDirection & NORTH)) {
			drag = getDragDistance(inPressed.y, inCurrent.y, snapSize.height);
			int maximum = Math.min(height + y, maximumSize.height);
			drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);
		} else if (EAST == (inDirection & EAST)) {
			//  Resizing the East or South border only affects the size
			drag = getDragDistance(inCurrent.x, inPressed.x, snapSize.width);
			Dimension boundingSize = getBoundingSize( inSource );
			int maximum = Math.min(boundingSize.width - x, maximumSize.width);
			drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);
		} else if (SOUTH == (inDirection & SOUTH)) {
			drag = getDragDistance(inCurrent.y, inPressed.y, snapSize.height);
			Dimension boundingSize = getBoundingSize( inSource );
			int maximum = Math.min(boundingSize.height - y, maximumSize.height);
			drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);
		} else {
			; // it has to be one of the above - these are all that we handle
		}
		// Add a multiplier so that we don't have to drag forever
		drag *= dragMultiplier;
		// Keep the center point of the component by adjusting width, height, and x and y
        width = width + (drag/2);
        height = height + (drag/2);
        x -= (drag/4);
        y -= (drag/4);
        
        // Continue if this is a Visual Group
        if(inSource instanceof VisualGroup) {
        	// Get the Visual Group
        	VisualGroup group = (VisualGroup)inSource;
        	// Continue if this has a rounded border
        	if(group.getBorder() instanceof RoundedBorder) {
        		// Get the Rounded Border
        		RoundedBorder border = (RoundedBorder)group.getBorder();
        		// Check if this is fully inside the parent
        		if(!border.isInside(group.getParent(), group)) {
	        		Container container = group.getParent();
        			// continue if the parent is a Visual Group
	        		if(container instanceof VisualGroup) {
	        			// This is not intended to do anything at this point in time
	        			VisualGroup parent = (VisualGroup) container;
	        			parent.getName();
	        		} else {
	        			; // don't do anything if the parent is not a Visual Group
	        		}
        		} else {
        			; // we need to do something, if this is no longer inside the parent Visual Group
        		}
        	} else {
        		; // We only handle Rounded Borders
        	}
        } else {
        	; // We only Handle Visual Groups
        }
        // change the size of the component
		inSource.setBounds(x, y, width, height);
		inSource.validate();
	}

	/*
	 *  Determine how far the mouse has moved from where dragging started
	 */
	private int getDragDistance(int larger, int smaller, int inSnapSize)
	{
		// Get the half way point
		int halfway = inSnapSize / 2;
		// Get the drag size
		int drag = larger - smaller;
		// Add the half way point to the drag
		drag += (drag < 0) ? -halfway : halfway;
		// adjust for snap size
		drag = (drag / inSnapSize) * inSnapSize;

		return drag;
	}

	/*
	 *  Adjust the drag value to be within the minimum and maximum range.
	 */
	private int getDragBounded(int drag, int inSnapSize, int dimension, int minimum, int maximum)
	{
		// Adjust for minimum size
		while (dimension + drag < minimum) {
			drag += inSnapSize;
		}
		// Adjust for maximum size
		while (dimension + drag > maximum) {
			drag -= inSnapSize;
		}

		return drag;
	}

	/*
	 *  Keep the size of the component within the bounds of its parent.
	 */
	private Dimension getBoundingSize(Component source)
	{
		// The return dimension
		Dimension dim;
		
		// Continue if the source is a Window
		if (source instanceof Window) {
			// Set the Dimension
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle winBounds = env.getMaximumWindowBounds();
			dim = new Dimension(winBounds.width, winBounds.height);
		} else {
			// Get the dimension of the parent
			dim = source.getParent().getSize();
		}
		return dim;
	}

	/**
	 * Is this component currently resizing	
	 */
	@Override
	public boolean isResizing() {
		return resizing;
	}

	/**
	 * Has this component recently resized	
	 */
	@Override
	public boolean justResized() {
		return justResized;
	}

	/**
	 * Reset resizing to false	
	 */
	@Override
	public void resetResized() {
		resizing = false;
		justResized = false;
	}
	
	/**
	 * Is this component currently draggable	
	 */
	@Override
	public boolean isDraggable() {
		return isDraggable;
	}
}
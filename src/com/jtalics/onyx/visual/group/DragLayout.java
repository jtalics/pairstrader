package com.jtalics.onyx.visual.group;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;

/**
 * The Layout Manager for the Visual Groups
 */
public class DragLayout implements LayoutManager, java.io.Serializable
{
    // Default Serial Version UID
	private static final long serialVersionUID = 1L;

	/**
	 * The Default Constructor
	 */
	public DragLayout() {
    }

    /**
     * Adds the specified component with the specified name to the layout.
     * @param name the name of the component
     * @param comp the component to be added
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {}


    /**
     * Removes the specified component from the layout.
     *
     * @param comp the component to be removed
     */
    @Override
    public void removeLayoutComponent(Component component)
    {
    }

    /**
     *  Determine the minimum size on the Container
     *
     *  @param   target   the container in which to do the layout
     *  @return  the minimum dimensions needed to lay out the
     *           subcomponents of the specified container
     */
    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        synchronized (parent.getTreeLock())
        {
            return preferredLayoutSize(parent);
        }
    }

    /**
     *  Determine the preferred size on the Container
     *
     *  @param   parent   the container in which to do the layout
     *  @return  the preferred dimensions to lay out the
     *           subcomponents of the specified container
     */
    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        synchronized (parent.getTreeLock())
        {
            return getLayoutSize(parent);
        }
    }

    /*
     *  The calculation for minimum/preferred size it the same. The only
     *  difference is the need to use the minimum or preferred size of the
     *  component in the calculation.
     *
     *  @param   parent  the container in which to do the layout
     */
    private Dimension getLayoutSize(Container parent)
    {
        Insets parentInsets = parent.getInsets();
        int x = parentInsets.left;
        int y = parentInsets.top;
        int width = 0;
        int height = 0;

        //  Get extreme values of the components on the container
        for (Component component: parent.getComponents())
        {
            if (component.isVisible())
            {
            	// Get the component bounds
                Point p = component.getLocation();
                Dimension d = component.getPreferredSize();
                x = Math.min(x, p.x);
                y = Math.min(y, p.y);
                width = Math.max(width, p.x + d.width);
                height = Math.max(height, p.y + d.height);
            } else {
            	; // do nothing if not visible
            }
        }

        // Width/Height is adjusted if any component is outside left/top edge
        if (x < parentInsets.left) {
            width += parentInsets.left - x;
        } else {
        	; // leave it as it was
        }
        if (y < parentInsets.top) {
            height += parentInsets.top - y;
        } else {
        	; // leave it as it was
        }
        //  Adjust for insets
        width += parentInsets.right;
        height += parentInsets.bottom;
        Dimension d = new Dimension(width, height);

        return d;
    }

    /**
     * Lays out the specified container using this layout.
     *
     * @param     target   the container in which to do the layout
     */
    @Override
    public void layoutContainer(Container parent)
    {
		synchronized (parent.getTreeLock())
		{
		    Insets parentInsets = parent.getInsets();
		
		    int x = parentInsets.left;
		    int y = parentInsets.top;
		
		    //  Get X/Y location outside the bounds of the panel
		    for (Component component: parent.getComponents())
		    {
		        if (component.isVisible())
		        {
		            Point location = component.getLocation();
		            x = Math.min(x, location.x);
		            y = Math.min(y, location.y);
		        } else {
		        	; // do nothing if not visible
		        }
		    }
		
		    x = (x < parentInsets.left) ? parentInsets.left - x : 0;
		    y = (y < parentInsets.top) ? parentInsets.top - y : 0;
		
		    //  Set bounds of each component
		    for (Component component: parent.getComponents())
		    {
		        if (component.isVisible())
		        {
		            Point p = component.getLocation();
		            Dimension d = component.getPreferredSize();
		
		            component.setBounds(p.x + x, p.y + y, d.width, d.height);
		        } else {
		        	; // do nothing if not visible
		        }
		    }
		}
    }

    /**
     * Returns the string representation of this column layout's values.
     * @return   a string representation of this layout
     */
    public String toString()
    {
        return "["
            + getClass().getName()
            + "]";
    }
}
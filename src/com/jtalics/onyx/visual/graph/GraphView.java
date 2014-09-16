package com.jtalics.onyx.visual.graph;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;


/**
 * <p>This class represents a custom graph component.</p>
 * 
 * <p>Drawing of the nodes/edges are delegated to child classes, as is bounding
 * and intersection checking. This class handles the logic for panning/scrolling,
 * zooming, selection, and dragging.</p>
 */
public abstract class GraphView extends JComponent {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Cursor showing an open hand.
	 */
	static final Cursor HAND_OPEN;
	
	/**
	 * Cursor showing a closed hand.
	 */
	static final Cursor HAND_CLOSED;
	
	/**
	 * Create the open/closed hand cursors from image files.
	 */
	static {
		try {
			BufferedImage handOpenImg = ImageIO.read(GraphView.class.getResource("handOpen.gif"));
			BufferedImage handClosedImg = ImageIO.read(GraphView.class.getResource("handClosed.gif"));
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			HAND_OPEN = toolkit.createCustomCursor(handOpenImg, 
					new Point(handOpenImg.getWidth() / 2,
					          handOpenImg.getHeight() / 2), "handOpen");
			HAND_CLOSED = toolkit.createCustomCursor(handClosedImg, 
					new Point(handClosedImg.getWidth() / 2,
					          handClosedImg.getHeight() / 2), "handClosed");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Constant which determines the maximum distance from a node that a click
	 * can be to still be considered a click "on" the node.
	 */
	private static final int CLICK_DELTA = 4;
	
	/**
	 * The max value for the scroll bars. Since scroll bar values are limited to
	 * integers, this limits the number of possible values that the scroll bar
	 * can have (and therefore the granularity of the scroll).
	 */
	static final int SCROLL_MAX = 10000;
	
	/**
	 * Constant which determines the speed at which auto-scrolling occurs if it
	 * is enabled. This constant should be greater than 0.0.
	 */
	static final double AUTOSCROLL_FACTOR = 0.5;
	
	/**
	 * Delta value used to mask floating point rounding errors.
	 */
	static final double FP_DELTA = 0.001;
	
	/**
	 * The component on which the graph is drawn.
	 */
	protected final InnerGraphView graphView;
	
	/**
	 * The horizontal scroll bar.
	 */
	protected final JScrollBar hScroll;
	
	/**
	 * The vertical scroll bar.
	 */
	protected final JScrollBar vScroll;
	
	/**
	 * If true, then the scroll bars are currently being adjusted
	 * due to a view change (panning/dragging). While true, scroll
	 * adjustment messages should be ignored.
	 */
	protected boolean autoAdjustingScrollBars = false;
	
	/**
	 * Selection listeners registered with this graph.
	 */
	private CopyOnWriteArraySet<GraphSelectionListener> selectionListeners
		= new CopyOnWriteArraySet<>();
	
	/**
	 * The minimum X value for the graph. No objects (including the
	 * current view) have a left bound less than this value.
	 */
	protected double minX = 0;
	
	
	/**
	 * The maximum X value for the graph. No objects (including the
	 * current view) have a right bound greater than this value.
	 */
	protected double maxX = 0;
	
	/**
	 * The minimum Y value for the graph. No objects (including the
	 * current view) have an upper bound less than this value.
	 */
	protected double minY = 0;
	
	/**
	 * The maximum y value for the graph. No objects (including the
	 * current view) have a lower bound greater than this value.
	 */
	protected double maxY = 0;
	
	/**
	 * The model for this graph. The model holds a list of nodes, their
	 * locations, and the links between them.
	 */
	protected GraphModel model;

	/**
	 * This set contains the currently selected nodes. Nodes within
	 * a selection region that is currently being dragged are not
	 * listed in this set.
	 */
	protected Set<Object> selectedNodes = new LinkedHashSet<>();
	
	/**
	 * This set contains nodes that are in a "temp selection" status.
	 * That is, they are within a selection region currently being dragged.
	 * If the xorTempSelection flag is false, then these nodes will be added
	 * to the main selection set once the drag operation is complete. If the
	 * xorTempSelection flag is true, then nodes that are not already present
	 * in the main selection set will be added, while nodes that are preset
	 * in the main selection set will be removed from the selection.
	 */
	protected Set<Object> tempSelection = new LinkedHashSet<>();
	
	/**
	 * Indicates the selection mode for the current drag operation. If
	 * true, then the nodes in the temp selection will be added to the
	 * main selection once the drag operation is complete. Otherwise, nodes
	 * that are not yet in the main selection (but in the temp selection)
	 * will be added to the selection, while nodes that are already in
	 * the main selection (and in the temp selection) will be removed from
	 * the selection.
	 */
	protected boolean xorTempSelection = false;
	
	/**
	 * If true indicates that the user is currently panning the view.
	 */
	protected boolean panning = false;
	
	/**
	 * If true indicates that the user is currently dragging one or more nodes.
	 */
	protected boolean dragging = false;
	
	/**
	 * If true indicates that the user is currently dragging out a selection region.
	 */
	protected boolean selecting = false;
	
	/**
	 * If a drag operation is taking place, this indicates the X coordinate (in the
	 * standard raw coordinate system) of the beginning of the drag operation.
	 */
	protected int scrStartX = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the Y coordinate (in the
	 * standard coordinate system) of the beginning of the drag operation.
	 */
	protected int scrStartY = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the X coordinate (in the
	 * standard coordinate system) of current position of the mouse.
	 */
	protected int scrCurX = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the Y coordinate (in the
	 * standard coordinate system) of current position of the mouse.
	 */
	protected int scrCurY = 0;
	
	/**
	 * This indicates X coordinate of the center of the viewing window in the
	 * graph's coordinate system.
	 */
	protected double centerX = 0;
	
	/**
	 * This indicates Y coordinate of the center of the viewing window in the
	 * graph's coordinate system.
	 */
	protected double centerY = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the starting X coordinate
	 * of the drag in the graph's coordinate system.
	 */
	protected double startX = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the starting Y coordinate
	 * of the drag in the graph's coordinate system.
	 */
	protected double startY = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the previous X coordinate
	 * of the drag in the graph's coordinate system.
	 */
	protected double lastX = 0;
	
	/**
	 * If a drag operation is taking place, this indicates the previous Y coordinate
	 * of the drag in the graph's coordinate system.
	 */
	protected double lastY = 0;
	
	/**
	 * If a selection region is currently being dragged out, this indicates the width
	 * of the selected region (with the unit of the measurement being the same as a
	 * unit in the graph's coordinate system). Note that this value will be negative
	 * if the user is dragging out the selection to the left.
	 */
	protected double selectWidth = 0;
	
	/**
	 * If a selection region is currently being dragged out, this indicates the height
	 * of the selected region (with the unit of the measurement being the same as a
	 * unit in the graph's coordinate system). Note that this value will be negative
	 * if the user is dragging out the selection upward.
	 */
	protected double selectHeight = 0;
	
	/**
	 * Indicates the scaling factor for the current view. Values greater than 1.0 indicate
	 * that the view is zoomed in, while values less than 1.0 (but greater than 0.0) indicate
	 * that the view is zoomed out. This value should always be greater than 0.0.
	 * 
	 * As an example, a value of 2.0 would indicate that all items in the graph are displayed
	 * at twice their actual size, while a value of 0.5 would indicate that each item is
	 * displayed at half of its actual size (in both width and height).
	 */
	protected double zoom = 1.0;
	
	/**
	 * Map from nodes to their location on this particular graph. The same nodes may have
	 * a differed position on other graphs that make use of them.
	 */
	protected Map<Object, Point2D> posMap = new HashMap<>();
	
	/**
	 * Layout manager to determine the position of new nodes.
	 */
	protected NodeLayoutManager layoutManager = new DefaultNodeLayoutManager();

	/**
	 * Indicates the currently selected interaction type.
	 */
	protected GraphInteractionType interactionType = GraphInteractionType.SELECTION;
	
	/**
	 * Indicates whether the layout should be rebuilt on each update.
	 */
	protected boolean layoutOnUpdate = false;
	
	/**
	 * Indicates whether the current layout is stale. If the layout is stale
	 * and {@link #getLayoutOnUpdate()} returns true, then the layout is
	 * rebuilt on the next redraw.
	 */
	protected boolean layoutStale = false;

	/**
	 * Creates a new graph component using the given model. If changes are made to the
	 * model after the graph is created, then the repaint method should be called on
	 * the graph to ensure that the changes show up in the view.
	 * 
	 * @param model - The model to use in the graph component.
	 */
	public GraphView(GraphModel model) {
		this.model = model;
		graphView = new InnerGraphView(this);
		ToolTipManager.sharedInstance().registerComponent(graphView);
		graphView.setOpaque(true);
		hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
		vScroll = new JScrollBar(JScrollBar.VERTICAL);
		GraphScrollListener scrollListener = new GraphScrollListener(this);
		hScroll.addAdjustmentListener(scrollListener);
		vScroll.addAdjustmentListener(scrollListener);
		hScroll.setMinimum(0);
		hScroll.setMaximum(SCROLL_MAX);
		vScroll.setMinimum(0);
		vScroll.setMaximum(SCROLL_MAX);
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
		this.add(graphView, BorderLayout.CENTER);
		this.add(vScroll, BorderLayout.EAST);
		hScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, vScroll.getPreferredSize().width));
		this.add(hScroll, BorderLayout.SOUTH);
		
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl A"), "SelectAll");
		this.getInputMap().put(KeyStroke.getKeyStroke("ctrl BACK_SLASH"), "SelectNone");
		this.getActionMap().put("SelectAll", new SelectAllAction(this));
		this.getActionMap().put("SelectNone", new SelectNoneAction(this));
		
		/*
		 * Watch for structural changes in the model so we know when
		 * we need to rebuild the layout.
		 */
		model.addGraphChangeListener(new ModelObserver(this));
	}

	/**
	 * Gets the scaling factor for the current view of this graph. Values greater than 1.0
	 * indicate that the view is zoomed in, while values less than 1.0 (but greater than 0.0)
	 * indicate that the view is zoomed out. This value should always be greater than 0.0.
	 * 
	 * @return - The current scaling factor.
	 */
	public double getZoom() {
		return zoom;
	}
	
	/**
	 * Sets the layoutOnUpdate property of this graph. If layoutOnUpdate is true, then the
	 * graph layout is recalculated each time a structural change occurs. Otherwise, nodes
	 * are not moved by the layout algorithm once they have been placed.
	 * 
	 * @param layoutOnUpdate - True to rebuild layout on each structural change, false otherwise.
	 */
	public void setLayoutOnUpdate(boolean layoutOnUpdate) {
		this.layoutOnUpdate = layoutOnUpdate;
	}
	
	/**
	 * Gets the layoutOnUpdate property of this graph. If layoutOnUpdate is true, then the
	 * graph layout is recalculated each time a structural change occurs. Otherwise, nodes
	 * are not moved by the layout algorithm once they have been placed.
	 * 
	 * @return - True if the graph is configured to rebuild its layout on each structural change,
	 *           false otherwise.
	 */
	public boolean getLayoutOnUpdate() {
		return layoutOnUpdate;
	}

	/**
	 * Sets the scaling factor for the current view of this graph. Values greater than 1.0
	 * indicate that the view is zoomed in, while values less than 1.0 (but greater than 0.0)
	 * indicate that the view is zoomed out. This value should always be greater than 0.0.
	 * 
	 * @param zoom - The new scaling factor.
	 * @throws IllegalArgumentException If <code>zoom</code> is not greater than 0.0.
	 */
	public void setZoom(double zoom) {
		if (zoom <= 0) {
			throw new IllegalArgumentException("invalid scaling factor");
		}
		else {
			this.zoom = zoom;
			graphView.repaint();
		}
	}

	/**
	 * Gets the center point for the current view of this graph. By default the center point
	 * is set to (0.0, 0.0).
	 * 
	 * @return - The center point.
	 */
	public Point2D getCenter() {
		return new Point2D.Double(centerX, centerY);
	}

	/**
	 * Sets the center point for the current view of this graph. By default the center point
	 * is set to (0.0, 0.0).
	 * 
	 * @param point - The new center point.
	 */
	public void setCenter(Point2D point) {
		this.centerX = point.getX();
		this.centerY = point.getY();
		graphView.repaint();
	}
	
	/**
	 * Rebuilds the layout, potentially moving nodes that have already been placed.
	 */
	public void layoutAll(boolean updateView) {
		ForceDirectedLayout layoutAll = new ForceDirectedLayout(model.getNodes(), model.getEdges(), posMap);
		double angle = layoutAll.layout();
		
		if (updateView) {
			AffineTransform transform = AffineTransform.getRotateInstance(angle);
			for (Entry<Object, Point2D> entry : posMap.entrySet()) {
				Point2D p = entry.getValue();
				transform.transform(p, p);
			}
			centerOnNodes();
		} else { /* We don't need to update the view, so we're done. */ ; }
	}
	
	/**
	 * Updates the view so as to move the center of the minimum rectangle
	 * containing all current nodes to the center of graph.<p>
	 * 
	 * If any nodes are not yet placed by the installed layout manager, they
	 * will be placed before updating the view. If there are no nodes in the
	 * model, then the view is left unchanged.<p>
	 */
	public void centerOnNodes() {
		/*
		 * Force the layout to assign positions to nodes with edges
		 * first. (This matches the behavior of the graph drawing
		 * code).
		 */
		Collection<?> nodes = model.getNodes();
		Collection<? extends GraphEdge> edges = model.getEdges();
		for (GraphEdge edge : edges) {
			this.getNodePos(edge.getSrcNode());
			this.getNodePos(edge.getDestNode());
		}
		
		/*
		 * Determine the x/y minimum/maximum values of all nodes.
		 * Any nodes that don't have any edges, will have their
		 * positions assigned as necessary by the graph.
		 */
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		int visibleCount = 0;
		for (Object node : nodes) {
			Point2D pos = this.getNodePos(node);
			Rectangle2D bounds = getNodeBounds(node, isSelected(node));
			if (bounds != null) {
				visibleCount += 1;
				if (pos.getX() + bounds.getMinX() < xMin) {
					xMin = pos.getX() + bounds.getMinX();
				} else { /* Not the minimum, so go on. */ ; }
				if (pos.getX() + bounds.getMaxX() > xMax) {
					xMax = pos.getX() + bounds.getMaxX();
				} else { /* Not the maximum, so go on. */ ; }
				if (pos.getY() + bounds.getMinY() < yMin) {
					yMin = pos.getY() + bounds.getMinY();
				} else { /* Not the minimum, so go on. */ ; }
				if (pos.getY() + bounds.getMaxY() > yMax) {
					yMax = pos.getY() + bounds.getMaxY();
				} else { /* Not the maximum, so go on. */ ; }
			} else { /* This node isn't visible so ignore it. */ ; }
		}
		
		if (visibleCount > 0) {
			/*
			 * Position the view so that the center of the rectangle containing
			 * all of the nodes is at the center of the screen. If the nodes
			 * in their current layout can all fit on the screen at once, this
			 * will ensure that the view is such that they do.
			 */
			this.setCenter(new Point2D.Double((xMin + xMax)/2, (yMin + yMax)/2));
			
			// Determine how far we need to zoom out to fit all nodes on the screen.
			double hZoom = graphView.getWidth()/(xMax - xMin);
			hZoom = Double.isNaN(hZoom) ? 1.0 : hZoom;
			double vZoom = graphView.getHeight()/(yMax - yMin);
			vZoom = Double.isNaN(vZoom) ? 1.0 : vZoom;
			double newZoom = Math.min(hZoom, vZoom);
			
			// Zoom out if necessary.
			setZoom(Math.min(newZoom, 1.0));
		} else { /* No nodes were visible, so don't try to center the view.*/ ; }
	}

	/**
	 * Gets a rectangle defining the current view parameters. This is the rectangle
	 * centered at the graph's center point, extending to the edges of the screen.
	 * 
	 * @return - The rectangle for the current view.
	 */
	public Rectangle2D getViewRect() {
		double viewWidth = graphView.getWidth() / zoom;
		double viewHeight = graphView.getHeight() / zoom;
		return new Rectangle2D.Double(centerX - viewWidth / 2, centerY - viewHeight / 2, viewWidth, viewHeight);
	}

	/**
	 * Gets the current interaction type of this graph.
	 * 
	 * @return - The interaction type.
	 */
	public GraphInteractionType getInteractionType() {// NO_UCD: interactionType is property bean of this graph  
		return interactionType;
	}

	/**
	 * Sets the interaction type for this graph.
	 * 
	 * @param interactionType - The new interaction type.
	 */
	public void setInteractionType(GraphInteractionType interactionType) {
		this.interactionType = interactionType;
		if (interactionType == GraphInteractionType.PAN) {
			graphView.setCursor(HAND_OPEN);
		} else {
			graphView.setCursor(null);
		}
	}

	/**
	 * Gets a set containing all selected nodes in this graph.
	 * If no nodes are selected, then this method will return
	 * an empty set.
	 * 
	 * @return - All nodes that are currently selected.
	 */
	public Set<?> getSelectedNodes()
	{
		/*
		 * Drop any nodes that the model no longer knows about.
		 */
		Collection<?> modelNodes = model.getNodes();
		selectedNodes.retainAll(modelNodes);
		tempSelection.retainAll(modelNodes);
		
		/*
		 * Build the selection set.
		 */
		LinkedHashSet<Object> nodes = new LinkedHashSet<>();
		nodes.addAll(selectedNodes);
		if (xorTempSelection) {
			for (Object node : tempSelection) {
				if (nodes.contains(node)) {
					nodes.remove(node);
				} else {
					nodes.add(node);
				}
			}
		} else {
			nodes.addAll(tempSelection);
		}
		return nodes;
	}

	/**
	 * Deselects all nodes in this graph.
	 */
	public void clearSelection() {
		selectedNodes.clear();
		tempSelection.clear();
		graphView.repaint();
		fireSelectionCleared();
	}

	/**
	 * Selects or deselects the given node. If the specified node
	 * is not part of graph model, then this call is ignored.
	 * 
	 * @param node - The node to select/deselect.
	 * @param selected - True to select the node, false to deselect the node.
	 */
	public void setSelected(Object node, boolean selected)
	{
		if (model.getNodes().contains(node)) {
			if (selected) {
				if (!isSelected(node)) {
					selectedNodes.add(node);
					tempSelection.remove(node);
					graphView.repaint();
					fireSelectionChanged(node, true);
				} else { /* The node was already selected, so do nothing.. */ ; }
			} else {
				if (isSelected(node)) {
					selectedNodes.remove(node);
					tempSelection.remove(node);
					graphView.repaint();
					fireSelectionChanged(node, false);
				} else { /* The node was already deselected, so do nothing. */ ; }
			}
		} else { /* This node isn't currently in the model, so do nothing. */ ; }
	}

	@Override
	public void setAutoscrolls(boolean autoscrolls) {
		super.setAutoscrolls(autoscrolls);
		this.graphView.setAutoscrolls(autoscrolls);
	}

	/**
	 * Determines whether the specified node is currently selected.
	 * (The temporary selection is taken into account).
	 * 
	 * @param node - The node to check the selection status for.
	 * @return - True if the node is selected, false otherwise.
	 */
	public boolean isSelected(Object node) {
		final boolean result;
		if (xorTempSelection) {
			result = selectedNodes.contains(node) ^ tempSelection.contains(node); 
		} else {
			result = selectedNodes.contains(node) || tempSelection.contains(node);
		}
		return result;
	}

	/**
	 * Gets the node for the given point. If more than one node exists at the point,
	 * the matching node that is farthest along in the models iteration order is returned.
	 * The point should be in the graph's coordinate system.
	 * 
	 * @param p - The point at which to check for a node.
	 * @return - The node at the point if one exists, or null if no node is present.
	 */
	public Object getNodeForPoint(Point2D p) {
		Object targetedNode = null;
		for (Object node : model.getNodes()) {
			Point2D pos = getNodePos(node);
			Rectangle2D rect = new Rectangle2D.Double(p.getX() - pos.getX() - CLICK_DELTA / (2 * zoom),
			                                          p.getY() - pos.getY() - CLICK_DELTA / (2 * zoom),
			                                          CLICK_DELTA / zoom, CLICK_DELTA / zoom);
			if (intersects(node, rect, isSelected(node))) {
				targetedNode = node;
			} else { /* This node doesn't intercept the rectangle, so move on to the next node. */ ; }
		}
		return targetedNode;
	}

	/**
	 * Gets the edge for the given point. If more than one edge exists at the point,
	 * the matching edge that is farthest along in the models iteration order is returned.
	 * The point should be in the graph's coordinate system.<p>
	 * 
	 * Note that this method may return an edge even if the portion of that near the
	 * specified point is behind a node. Thus callers may only want to call this method
	 * after they have determined that the point is not on a node.
	 * 
	 * @param p - The point at which to check for an edge.
	 * @return - The edge at the point if one exists, or null if no edge is present.
	 */
	public GraphEdge getEdgeForPoint(Point2D p) {
		GraphEdge targetedEdge = null;
		Rectangle2D rect = new Rectangle2D.Double(p.getX() - CLICK_DELTA / (2 * zoom),
		                                          p.getY() - CLICK_DELTA / (2 * zoom),
		                                          CLICK_DELTA / zoom, CLICK_DELTA / zoom);
		for (GraphEdge edge : model.getEdges()) {
			if (intersects(edge, rect)) {
				targetedEdge = edge;
			} else { /* This edge doesn't intercept the rectangle, so move on to the next edge. */ ; }
		}
		return targetedEdge;
	}

	/**
	 * Translate's the given component x and y values to a point in the graph's
	 * coordinate system.
	 */
	public Point2D translateRawCoordinates(int x, int y) {
		AffineTransform t = AffineTransform.getTranslateInstance(centerX - graphView.getWidth() / (2 * zoom),
		                                                         centerY - graphView.getHeight() / (2 * zoom));
		t.scale(1 / zoom, 1 / zoom);
		return t.transform(new Point2D.Double(x, y), null);
	}
	
	/**
	 * Registers a selection listener with this graph.
	 * 
	 * @param listener - The listener to register.
	 */
	public void addSelectionListener(GraphSelectionListener listener) {
		selectionListeners.add(listener);
	}
	
	/**
	 * Returns true if the user is currently interacting with this graph causing it to change.
	 * This will be true when the user pans the view (dragging with the hand tool or dragging
	 * the scroll bars), when the user drags a selection region, and when the user drags
	 * one or more nodes.
	 * 
	 * @return - True if the state of the graph is currently changing, false otherwise.
	 */
	public boolean isGraphChanging() {
		return dragging || panning || selecting || hScroll.getValueIsAdjusting() || vScroll.getValueIsAdjusting();
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		graphView.addMouseListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		graphView.removeMouseListener(listener);
	}

	/**
	 * Fires a selection change event to all selection listeners registered
	 * with this class.
	 * 
	 * @param node - The node for which the selection state has changed.
	 * @param selected - The new selection state.
	 */
	void fireSelectionChanged(Object node, boolean selected) {
		boolean focusOwner = this.isFocusOwner();
		
		for (GraphSelectionListener listener : selectionListeners) {
			listener.selectionChanged(this, node, selected);
		}
		
		/*
		 * For some reason we sometimes loose the focus when selection change events are fired,
		 * so if we were the focus owner before firing the events, reclaim focus now.
		 */
		if (focusOwner) {
			this.requestFocusInWindow();
		} else { /* We weren't the focus owner, so don't claim focus. */ ; }
	}
	
	/**
	 * Fires a selection cleared event to all selection listeners registered
	 * with this class.
	 */
	void fireSelectionCleared() {
		for (GraphSelectionListener listener : selectionListeners) {
			listener.selectionCleared(this);
		}
	}

	/**
	 * If a selection is currently being dragged out, this method will
	 * return the selection region. The returned rectangle will not use
	 * a negative width or height.
	 * 
	 * @return - The currently selected rectangle (if a selection is being dragged out).
	 */
	Rectangle2D getSelectionRect() {
		double x;
		double y;
		if (selectWidth < 0) {
			x = startX + selectWidth;
		} else {
			x = startX;
		}
		if (selectHeight < 0) {
			y = startY + selectHeight;
		} else {
			y = startY;
		}
		// Return a rectangle with positive width and height.
		return new Rectangle2D.Double(x, y, Math.max(Math.abs(selectWidth), Double.MIN_VALUE),
		                                    Math.max(Math.abs(selectHeight), Double.MIN_VALUE));
	}
	
	/**
	 * Gets the location of the given node within this graph. If the node doesn't have a position
	 * within this graph yet, a new position is determined for the node and returned.<p>
	 * 
	 * The Point2D object returned by this method may be modified internally or externally, but
	 * the graph must be repainted explicitly for the changes to be visible immediately.
	 * 
	 * @param node - The node to get the position for.
	 * @return - The position of the node.
	 */
	public Point2D getNodePos(Object node) {
		Point2D pos = posMap.get(node);
		if (pos == null) {
			pos = layoutManager.generateNodePos(this, model, node, posMap);
			posMap.put(node, pos);
		} else {
			/*
			 * We already have a position for this node, so we don't need
			 * to generate a position. Just return the current position.
			 */ ;
		}
		return pos;
	}
	
	/**
	 * Sets the node layout manager for this graph.
	 * 
	 * @param layoutManager - The new node layout manager.
	 */
	public void setLayoutManager(NodeLayoutManager layoutManager) {
		if (layoutManager == null) {
			throw new NullPointerException("layoutManager must not be null");
		} else {
			this.layoutManager = layoutManager;
		}
	}
	
	/**
	 * Gets the model for this graph.
	 * 
	 * @return - The model for this graph.
	 */
	public GraphModel getModel() {
		return model;
	}
	
	/**
	 * Gets the current node layout manager for this graph.
	 * 
	 * @return - The current node layout manager.
	 */
	public NodeLayoutManager getLayoutManager() {
		return layoutManager;
	}

	/**
	 * Gets the tool tip that the specified node should display. The default implementation
	 * returns null for all nodes. Subclasses that wish to display tool tips on their nodes
	 * should override this method.
	 * 
	 * @param node - The node for which the tool tip should be returned.
	 * @return - The tool tip for the specified node.
	 */
	public String getTooltip(Object node) {
		return null;
	}
	
	/**
	 * Gets the tool tip that the specified edge should display. The default implementation
	 * returns null for all edges. Subclasses that wish to display tool tips on their edges
	 * should override this method.
	 * 
	 * @param edge - The edge for which the tool tip should be returned.
	 * @return - The tool tip for the specified edge.
	 */
	public String getTooltip(GraphEdge edge) {
		return null;
	}

	/**
	 * Gets the bounds for the given node. The returned rectangle should be relative
	 * to the node's center point. That is, the point (0, 0) within the returned
	 * rectangle will be considered the center point of the node for the purpose of
	 * drawing edges.
	 * 
	 * The returned bounding rectangle is also used to calculate the bounds for the
	 * graph as a whole, so the node in question shouldn't generally paint outside
	 * of this rectangle.
	 * 
	 * If the node is not currently displayed (due to a filtering operation by the
	 * view), then this method should return null.
	 * 
	 * @param node - The node to get the bounds for.
	 * @param selected - True if the node is currently selected, false otherwise.
	 * @return - The bounds for the specified node.
	 */
	public abstract Rectangle2D getNodeBounds(Object node, boolean selected);

	/**
	 * Determines whether the given rectangle intersects with a particular node.
	 * This method is called to determine whether a given click or selection region
	 * should affect this node.
	 * 
	 * Generally, if the <code>getNodeBounds</code> method returns null for a given
	 * node, this method should return false for that node.
	 * 
	 * @param node - The node to check for intersection with.
	 * @param rect - The rectangle to check for intersection.
	 * @param selected - True if the node is currently selected, false otherwise.
	 * @return - True if the node intersects with the supplied rectangle, false otherwise.
	 */
	public abstract boolean intersects(Object node, Rectangle2D rect, boolean selected);

	/**
	 * Determines whether the given rectangle intersects with a particular edge.
	 * This method is called to determine whether a given edge is on screen (with
	 * the view bounds as <code>rect</code>) and to determine whether a given click
	 * should be considered to hit an edge.<p>
	 * 
	 * Generally, if the edge is not visible this method should return false for that edge.
	 * 
	 * @param edge - The edge to check for intersection with.
	 * @param rect - The rectangle to check for intersection.
	 * @return - True if the edge intersects with the supplied rectangle, false otherwise.
	 */
	public abstract boolean intersects(GraphEdge edge, Rectangle2D rect);

	/**
	 * Paints the specified node using the graphics device <code>g</code>.
	 * 
	 * This method should paint the node with its center at (0, 0). The
	 * graphics device supplied to this method will already be translated
	 * and scaled as appropriate for the given node and the current view.
	 * 
	 * @param node - The node to paint.
	 * @param g - The graphics device with which to paint the the node.
	 * @param selected - True if the node is currently selected, false otherwise.
	 */
	public abstract void paintNode(Object node, Graphics2D g, boolean selected);

	/**
	 * Paints the specified edge using the graphics device <code>g</code>.
	 * 
	 * This method should paint the edge based on the positions of the nodes that
	 * the edge links together. The graphics device will already be translated
	 * and scaled as appropriate for the current view.
	 * 
	 * @param edge - The edge to paint.
	 * @param g - The graphics device with which to paint the edge.
	 */
	public abstract void paintEdge(GraphEdge edge, Graphics2D g);

	/**
	 * Paints the background for the graph. There are no transforms
	 * applied to the supplied graphics object beyond the graph
	 * components own transform. The pixels for the graphics object
	 * should be one to one with the graph component's pixels on the
	 * screen.
	 * 
	 * @param g - The graphics device with which to paint the background.
	 */
	public abstract void paintBackground(Graphics2D g);
}

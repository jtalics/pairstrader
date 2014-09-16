package com.jtalics.onyx.visual.graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import com.jtalics.onyx.messaging.GraphicMessage;
import com.jtalics.onyx.messaging.GraphicMessageType;

/**
 * This class defines the component on which the graph is drawn. This class is
 * only intended for use by {@link GraphView}.
 */
class InnerGraphView extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;

	/**
	 * The {@link GraphView} instance that this panel sits on.
	 */
	private final GraphView graph;
	/**
	 * A temporary list of nodes or edges for the current render operation.
	 */
	private final List<GraphEdge> tempEdgeList = new ArrayList<>();

	/**
	 * Creates a new graph view for the specified {@link GraphView} instance.
	 * 
	 * @param graph
	 *          - The {@link GraphView} instance on which this
	 *          {@link InnerGraphView} will sit.
	 */
	InnerGraphView(GraphView graph) {
		this.graph = graph;
		// Listen for our own mouse events.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.isAltDown()) {
			/*
			 * Mouse wheel movement occurred while the control key was down, so zoom
			 * in or out depending on the direction of the movement.
			 */
			final double factor;
			if (e.getWheelRotation() < 0) {
				factor = 1.25;
			}
			else {
				factor = 0.80;
			}
			this.graph.setZoom(this.graph.getZoom() * factor);
		}
		else {
			/*
			 * Mouse wheel movement occurred while the control key was up, so pan the
			 * window up/down or (if up/down isn't applicable because the entire graph
			 * fits vertically on the screen) left/right.
			 */
			final int sign;
			if (e.getWheelRotation() < 0) {
				sign = -1;
			}
			else if (e.getWheelRotation() > 0) {
				sign = 1;
			}
			else {
				sign = 0;
			}
			if (this.graph.vScroll.getVisibleAmount() < this.graph.vScroll.getMaximum() - this.graph.vScroll.getMinimum()) {

				/*
				 * Do up/down scroll.
				 */
				this.graph.vScroll.setValue(this.graph.vScroll.getValue() + sign * this.graph.vScroll.getUnitIncrement(sign));
			}
			else if (this.graph.hScroll.getVisibleAmount() < this.graph.hScroll.getMaximum() - this.graph.hScroll.getMinimum()) {

				/*
				 * Do left/right scroll
				 */
				this.graph.hScroll.setValue(this.graph.hScroll.getValue() + sign * this.graph.hScroll.getUnitIncrement(sign));
			}
			else { /* Neither scroll direction was applicable, so do nothing. */
				;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p = this.graph.translateRawCoordinates(e.getX(), e.getY());
		this.graph.scrCurX = e.getX();
		this.graph.scrCurY = e.getY();
		if (this.graph.dragging && !this.graph.selectedNodes.isEmpty()) {
			/*
			 * We're dragging nodes, so move each node along with the mouse.
			 */
			for (Object node : this.graph.selectedNodes) {
				Point2D pos = this.graph.getNodePos(node);
				pos.setLocation((pos.getX() + p.getX() - this.graph.lastX), (pos.getY() + p.getY() - this.graph.lastY));
			}
			if (this.graph.graphView.getAutoscrolls()) {
				panTowardsPoint(p, GraphView.AUTOSCROLL_FACTOR);
			}
			else { /* Auto-scrolling not enabled, so do nothing. */
				;
			}
		}
		else if (this.graph.selecting) {
			// /*
			// * We're dragging out a selection rectangle, so select/deselect nodes
			// * as necessary
			// */
			// this.graph.selectWidth = p.getX() - this.graph.startX;
			// this.graph.selectHeight = p.getY() - this.graph.startY;
			// Set<Object> oldTempSelection = this.graph.tempSelection;
			// this.graph.tempSelection = new LinkedHashSet<>();
			// for (Object node : this.graph.model.getNodes()) {
			// Point2D pos = this.graph.getNodePos(node);
			// Rectangle2D s = this.graph.getSelectionRect();
			// Rectangle2D rect = new Rectangle2D.Double(s.getX() - pos.getX(),
			// s.getY() - pos.getY(),
			// s.getWidth(), s.getHeight());
			// if (this.graph.intersects(node, rect, this.graph.isSelected(node))) {
			// this.graph.tempSelection.add(node);
			// } else { /* This node doesn't intersect the dragged rectangle, so
			// ignore it. */ ; }
			// }
			// diffSelections(oldTempSelection, this.graph.tempSelection);
		}
		else if (this.graph.panning) {
			/*
			 * We're panning, so move the view in sync with the mouse.
			 */
			this.graph.centerX -= (p.getX() - this.graph.startX);
			this.graph.centerY -= (p.getY() - this.graph.startY);
		}
		else { /*
						 * This drag operation doesn't require us to perform any special
						 * action.
						 */
			;
		}
		this.graph.graphView.repaint();
		this.graph.lastX = p.getX();
		this.graph.lastY = p.getY();
	}

	/**
	 * Fires the appropriate selection changed events by checking the difference
	 * between two temporary selection sets. The state of the main selection set
	 * and the <code>xorTempSelection</code> flag are taken into account to
	 * determine which events need to be fired off.
	 * 
	 * @param oldSet
	 *          - The old temporary selection set.
	 * @param newSet
	 *          - The new temporary selection set.
	 */
	private void diffSelections(Set<Object> oldSet, Set<Object> newSet) {
		/*
		 * Look for nodes that entered the temporary selection, and send messages
		 * for those nodes. If the selection is in XOR mode, these could be either
		 * additions, or deletions to/from the selection. Otherwise, they can only
		 * be additions.
		 */
		for (Object node : oldSet) {
			if ((this.graph.xorTempSelection || !this.graph.selectedNodes.contains(node)) && !newSet.contains(node)) {

				this.graph.fireSelectionChanged(node, this.graph.xorTempSelection && this.graph.selectedNodes.contains(node));
			}
			else { /*
							 * This node didn't enter the selection, so we don't need to send
							 * a message.
							 */
				;
			}
		}

		/*
		 * Look for nodes that exited the temporary selection, and send messages for
		 * those nodes. If the selection is in XOR mode, these could be either
		 * additions, or deletions to/from the selection. Otherwise, they can only
		 * be deletions.
		 */
		for (Object node : newSet) {
			if ((this.graph.xorTempSelection || !this.graph.selectedNodes.contains(node)) && !oldSet.contains(node)) {

				this.graph.fireSelectionChanged(node, !(this.graph.xorTempSelection && this.graph.selectedNodes.contains(node)));
			}
			else { /*
							 * This node didn't exit the selection, so we don't need to send a
							 * message.
							 */
				;
			}
		}
	}

	/**
	 * Pans the view towards the specified point. The <code>factor</code>
	 * parameter determines the distance to pan as a factor of the total panning
	 * distance necessary to include the point in the view.
	 * 
	 * Calling this method with <code>factor == 1.0</code> will pan just far
	 * enough so that the specified point is visible in the view.
	 * 
	 * If the specified point is already visible, then calling this method will do
	 * nothing.
	 * 
	 * @param p
	 *          - The point to include in the view.
	 */
	private void panTowardsPoint(Point2D p, double factor) {
		Rectangle2D viewRect = this.graph.getViewRect();
		if (p.getX() < viewRect.getX()) {
			this.graph.centerX -= (viewRect.getX() - p.getX()) * factor;
		}
		else if (p.getX() > viewRect.getX() + viewRect.getWidth()) {
			this.graph.centerX += (p.getX() - (viewRect.getX() + viewRect.getWidth())) * factor;
		}
		else { /* No panning necessary in the X direction. */
			;
		}
		if (p.getY() < viewRect.getY()) {
			this.graph.centerY -= (viewRect.getY() - p.getY()) * factor;
		}
		else if (p.getY() > viewRect.getY() + viewRect.getHeight()) {
			this.graph.centerY += (p.getY() - (viewRect.getY() + viewRect.getHeight())) * factor;
		}
		else { /* No panning necessary in the Y direction. */
			;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		graph.requestFocusInWindow();

		Point2D p = this.graph.translateRawCoordinates(e.getX(), e.getY());
		this.graph.scrStartX = e.getX();
		this.graph.scrStartY = e.getY();
		this.graph.scrCurX = this.graph.scrStartX;
		this.graph.scrCurY = this.graph.scrStartY;
		this.graph.lastX = p.getX();
		this.graph.lastY = p.getY();
		if (this.graph.interactionType == GraphInteractionType.SELECTION && !e.isShiftDown()) {
			Object targetedNode = this.graph.getNodeForPoint(p);
			handleSelectSingle(targetedNode, e.isControlDown());
			if (targetedNode == null) {
				this.graph.panning = false;
				this.graph.dragging = false;
				this.graph.selecting = true;
				this.graph.selectWidth = 0;
				this.graph.selectHeight = 0;
				this.graph.xorTempSelection = e.isControlDown();
				// this.graph.graphView.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
			else {
				this.graph.dragging = !e.isControlDown();
				this.graph.panning = false;
				this.graph.selecting = false;
			}
		}
		else {
			this.graph.dragging = false;
			this.graph.selecting = false;
			if (this.graph.interactionType == GraphInteractionType.PAN || e.isShiftDown()) {
				this.graph.panning = true;
				this.graph.graphView.setCursor(GraphView.HAND_CLOSED);
			}
			else {
				this.graph.panning = false;
			}
		}
		if (e.getButton() != MouseEvent.BUTTON1) {
			this.graph.dragging = false;
			this.graph.selecting = false;
			this.graph.panning = false;
			this.graph.graphView.setCursor(null);
		}
		else { /*
						 * This is the left mouse button, so we don't need to reset the
						 * mouse states.
						 */
			;
		}
		this.graph.startX = this.graph.lastX;
		this.graph.startY = this.graph.lastY;
		this.graph.graphView.repaint();
	}

	/**
	 * Handles the selection/deselection of this single node (resulting from a
	 * click on it, not by dragging a selection over it).
	 * 
	 * @param newlySelected
	 *          - The node that was clicked.
	 * @param ctrlDown
	 *          - True if the control key was pressed at the time the
	 *          selection/deselection event occurred.
	 */
	private void handleSelectSingle(Object newlySelected, boolean ctrlDown) {
		if (newlySelected == null) {
			GraphicMessage msg = new GraphicMessage(GraphicMessageType.APPLET_NODE_SELECTION);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SELECTED", "FALSE");
			msg.setData(map);
			// try {
			// if(webSocket != null) {
			// webSocket.sendMessage(msg);
			// } else {
			// ; // no web socket
			// }
			// } catch (IOException | InterruptedException | ExecutionException e1) {
			// e1.printStackTrace();
			// }
		}
		else {
			GraphicMessage msg = new GraphicMessage(GraphicMessageType.APPLET_NODE_SELECTION);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SELECTED", "TRUE");
			msg.setData(map);
			// try {
			// if(webSocket != null) {
			// webSocket.sendMessage(msg);
			// } else {
			// ; // no web socket
			// }
			// } catch (IOException | InterruptedException | ExecutionException e1) {
			// e1.printStackTrace();
			// }
		}
		this.graph.clearSelection();
		this.graph.selectedNodes.add(newlySelected);
		this.graph.fireSelectionChanged(newlySelected, true);
		// if (this.graph.selectedNodes.contains(newlySelected)) {
		// if (ctrlDown) {
		// this.graph.selectedNodes.remove(newlySelected);
		// this.graph.fireSelectionChanged(newlySelected, false);
		// } else { /* Control key wasn't down, so we shouldn't remove the node from
		// the selection. */ ; }
		// } else if (newlySelected == null) {
		// if (!ctrlDown) {
		// this.graph.clearSelection();
		// } else { /* This node didn't enter the selection, so we don't need to
		// send a message. */ ; }
		// } else {
		// if (!ctrlDown)
		// {
		// this.graph.clearSelection();
		// } else { /* This node didn't enter the selection, so we don't need to
		// send a message. */ ; }
		// this.graph.selectedNodes.add(newlySelected);
		// this.graph.fireSelectionChanged(newlySelected, true);
		// }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (this.graph.selecting) {
			this.graph.selecting = false;
			for (Object node : this.graph.tempSelection) {
				if (this.graph.xorTempSelection) {
					if (this.graph.selectedNodes.contains(node)) {
						this.graph.selectedNodes.remove(node);
					}
					else {
						this.graph.selectedNodes.add(node);
					}
				}
				else {
					this.graph.selectedNodes.add(node);
				}
			}
			this.graph.tempSelection.clear();
		}
		else { /*
						 * We weren't in selecting mode, so we don't need to apply the temp
						 * selection.
						 */
			;
		}
		if (this.graph.interactionType == GraphInteractionType.PAN) {
			this.graph.graphView.setCursor(GraphView.HAND_OPEN);
		}
		else {
			this.graph.graphView.setCursor(null);
		}
		this.graph.panning = false;
		this.graph.dragging = false;
		this.graph.graphView.repaint();
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point2D p = this.graph.translateRawCoordinates(event.getX(), event.getY());
		Object node = this.graph.getNodeForPoint(p);
		final String toolTip;
		if (node == null) {
			GraphEdge edge = this.graph.getEdgeForPoint(p);
			if (edge == null) {
				toolTip = null;
			}
			else {
				toolTip = this.graph.getTooltip(edge);
			}
		}
		else {
			toolTip = this.graph.getTooltip(node);
		}
		return toolTip;
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		Collection<?> nodes = this.graph.model.getNodes();
		this.graph.posMap.keySet().retainAll(nodes);

		if (graph.getLayoutOnUpdate() && graph.layoutStale) {
			graph.layoutStale = false;
			graph.layoutAll(true);
		}
		else { /* Graph isn't configured for auto re-layout. */
			;
		}

		/*
		 * Prepare the graphics object.
		 */
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		/*
		 * Paint the background.
		 */
		Graphics2D newGraphics = (Graphics2D) g.create();
		this.graph.paintBackground(newGraphics);
		newGraphics.dispose();

		/*
		 * Create a backup of the original transform, then transform the graphics
		 * object based on our current view (position and zoom) within the graph.
		 */
		AffineTransform originalTransform = g.getTransform();
		g.scale(this.graph.zoom, this.graph.zoom);
		g.translate(-this.graph.centerX + this.graph.graphView.getWidth() / (2 * this.graph.zoom), -this.graph.centerY + this.graph.graphView.getHeight() / (2 * this.graph.zoom));

		tempEdgeList.clear();
		Rectangle2D viewRect = graph.getViewRect();
		for (GraphEdge edge : this.graph.model.getEdges()) {
			if (graph.intersects(edge, viewRect)) {
				tempEdgeList.add(edge);
			}
			else { /* This edge isn't visible, so skip it. */
				;
			}
		}

		/*
		 * Adjust rendering hints based on the details of the rendering job.
		 */
		if ((graph.isGraphChanging() && tempEdgeList.size() > 80) || tempEdgeList.size() > 400) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		/*
		 * Paint the edges. We do this before the nodes because we don't want any
		 * edges to show up on top of the nodes.
		 */
		for (GraphEdge edge : tempEdgeList) {
			Graphics2D g2 = (Graphics2D) g.create();
			this.graph.paintEdge(edge, g2);
			g2.dispose();
		}
		tempEdgeList.clear();

		/*
		 * As we loop through the list of nodes, checking their bounds and painting
		 * them as necessary, we also want to keep track of the minimum bounding
		 * rectangle that contains all nodes that are currently visible (that is,
		 * nodes with a non-null bounding rectangle).
		 * 
		 * We can then use this minimum bounding rectangle to update the scroll bars
		 * based on whether there are any visible nodes off screen.
		 */
		resetGraphBounds();
		updateGraphBounds(this.graph.getViewRect(), 0, 0);
		for (Object node : nodes) {
			boolean isSelected = this.graph.isSelected(node);
			Point2D pos = this.graph.getNodePos(node);
			Rectangle2D bounds = this.graph.getNodeBounds(node, isSelected);
			updateGraphBounds(bounds, pos.getX(), pos.getY());
			if (bounds != null && g.hitClip((int) Math.floor(pos.getX() + bounds.getX() - 1), (int) Math.floor(pos.getY() + bounds.getY() - 1), (int) Math.ceil(bounds.getWidth() + 2), (int) Math.ceil(bounds.getHeight()) + 2)) {
				/*
				 * This node is visible and on screen, so draw it.
				 */
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(pos.getX(), pos.getY());
				this.graph.paintNode(node, g2, isSelected);
				g2.dispose();
			}
			else { /* Node was visible or off screen, so do nothing. */
				;
			}
		}

		/*
		 * Update the scroll bars based on the minimum bounding rectangle for all
		 * visible nodes as calculated above while drawing the nodes.
		 */
		updateScrollBars();

		if (this.graph.selecting) {
			// /*
			// * Draw the selection region. We need to go back to our original
			// * transform here since we're using this components native coordinate
			// * system to keep track of the selection region.
			// *
			// * (Note that we're also keeping track of the selection region in
			// * the graph's coordinate system, but if we use that to draw the
			// * selection region, the edge of the selection region can sometimes
			// * fluctuate back and forth by a pixel as we draw.)
			// */
			// g.setColor(Color.DARK_GRAY);
			// g.setTransform(originalTransform);
			// int x = Math.min(this.graph.scrStartX, this.graph.scrCurX);
			// int y = Math.min(this.graph.scrStartY, this.graph.scrCurY);
			// int w = Math.abs(this.graph.scrStartX - this.graph.scrCurX);
			// int h = Math.abs(this.graph.scrStartY - this.graph.scrCurY);
			// g.drawRect(x, y, w, h);
		}
		else { /*
						 * We're not in selecting mode, so we don't need to draw the
						 * selection region.
						 */
			;
		}
	}

	/**
	 * Resets the min/max X/Y values in preparation for recalculating them.
	 * 
	 * If either scroll bar is currently adjusting, then this method is ignored.
	 */
	private void resetGraphBounds() {
		if (!(this.graph.hScroll.getValueIsAdjusting() || this.graph.vScroll.getValueIsAdjusting())) {
			this.graph.minX = Double.POSITIVE_INFINITY;
			this.graph.maxX = Double.NEGATIVE_INFINITY;
			this.graph.minY = Double.POSITIVE_INFINITY;
			this.graph.maxY = Double.NEGATIVE_INFINITY;
		}
		else { /*
						 * We don't want to reset the bounds yet as the scroll bars are
						 * still being dragged.
						 */
			;
		}
	}

	/**
	 * Updates the min/max X/Y values to include the given rectangle offset by
	 * <code>xOffset</code> and <code>yOffset</code>.
	 * 
	 * If either scroll bar is currently adjusting, then this method is ignored.
	 * If the <code>rect</code> parameter is null, then this method is ignored.
	 * 
	 * @param rect
	 *          - The rectangle to include.
	 * @param xOffset
	 *          - The distance to offset the rectangle in the X direction before
	 *          including it in the current bounding rectangle.
	 * @param yOffset
	 *          - The distance to offset the rectangle in the Y direction before
	 *          including it in the current bounding rectangle.
	 */
	private void updateGraphBounds(Rectangle2D rect, double xOffset, double yOffset) {
		if (!(rect == null || this.graph.hScroll.getValueIsAdjusting() || this.graph.vScroll.getValueIsAdjusting())) {

			if (rect.getX() + xOffset < this.graph.minX) {
				this.graph.minX = rect.getX() + xOffset;
			}
			else { /* Rectangle doesn't extend the bounds to the left, so do nothing. */
				;
			}
			if (rect.getX() + xOffset + rect.getWidth() > this.graph.maxX) {
				this.graph.maxX = rect.getX() + xOffset + rect.getWidth();
			}
			else { /*
							 * Rectangle doesn't extend the bounds to the right, so do
							 * nothing.
							 */
				;
			}
			if (rect.getY() + yOffset < this.graph.minY) {
				this.graph.minY = rect.getY() + yOffset;
			}
			else { /* Rectangle doesn't extend the bounds to the top, so do nothing. */
				;
			}
			if (rect.getY() + yOffset + rect.getHeight() > this.graph.maxY) {
				this.graph.maxY = rect.getY() + yOffset + rect.getHeight();
			}
			else { /*
							 * Rectangle doesn't extend the bounds to the bottom, so do
							 * nothing.
							 */
				;
			}
		}
		else {
			/*
			 * We either didn't get a rectangle, or the scroll bars are being dragged,
			 * so do nothing.
			 */;
		}
	}

	/**
	 * Updates the scroll bars based on the current view and the graph's bounding
	 * rectangle.
	 * 
	 * If either scroll bar is currently adjusting, then this method is ignored.
	 */
	private void updateScrollBars() {
		if (!(this.graph.hScroll.getValueIsAdjusting() || this.graph.vScroll.getValueIsAdjusting())) {
			this.graph.autoAdjustingScrollBars = true;
			Rectangle2D viewRect = this.graph.getViewRect();
			double xCurrentRatio = (viewRect.getX() - this.graph.minX) / (this.graph.maxX - this.graph.minX);
			double yCurrentRatio = (viewRect.getY() - this.graph.minY) / (this.graph.maxY - this.graph.minY);

			double xVisibleRatio = viewRect.getWidth() / (this.graph.maxX - this.graph.minX);
			if (xVisibleRatio + GraphView.FP_DELTA > 1.0) {
				xVisibleRatio = 1.0;
			}
			else { /*
							 * X visible ratio isn't within FP_DELTA of 1.0, so leave it
							 * alone.
							 */
				;
			}

			double yVisibleRatio = viewRect.getHeight() / (this.graph.maxY - this.graph.minY);
			if (yVisibleRatio + GraphView.FP_DELTA > 1.0) {
				yVisibleRatio = 1.0;
			}
			else { /*
							 * Y visible ratio isn't within FP_DELTA of 1.0, so leave it
							 * alone.
							 */
				;
			}

			/*
			 * Update the current scroll position and the percent of the total scroll
			 * bar that the knob should take up.
			 * 
			 * Updating the visible amount first can cause unexpected behavior if the
			 * visible amount is less than the previous value. Updating the visible
			 * amount last can cause unexpected behavior if the visible amount is
			 * greater than previous value. To get around this, we update the value
			 * both before and after the visible amount.
			 */
			this.graph.hScroll.setValue((int) (xCurrentRatio * GraphView.SCROLL_MAX));
			this.graph.vScroll.setValue((int) (yCurrentRatio * GraphView.SCROLL_MAX));
			this.graph.hScroll.setVisibleAmount((int) (xVisibleRatio * GraphView.SCROLL_MAX));
			this.graph.vScroll.setVisibleAmount((int) (yVisibleRatio * GraphView.SCROLL_MAX));
			this.graph.hScroll.setValue((int) (xCurrentRatio * GraphView.SCROLL_MAX));
			this.graph.vScroll.setValue((int) (yCurrentRatio * GraphView.SCROLL_MAX));

			// Update increments.
			this.graph.hScroll.setUnitIncrement(this.graph.hScroll.getVisibleAmount() / 20);
			this.graph.vScroll.setUnitIncrement(this.graph.vScroll.getVisibleAmount() / 20);
			this.graph.hScroll.setBlockIncrement(this.graph.hScroll.getVisibleAmount());
			this.graph.vScroll.setBlockIncrement(this.graph.vScroll.getVisibleAmount());
			this.graph.autoAdjustingScrollBars = false;
		}
		else { /* The scroll bars are being dragged, so do nothing. */
			;
		}
	}

	/*
	 * MouseListener methods that we don't need to handle.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Point2D p = this.graph.translateRawCoordinates(e.getX(), e.getY());
			Object targetedNode = this.graph.getNodeForPoint(p);
			if (targetedNode instanceof CpdaveGraphNode) {
				CpdaveGraphNode node = (CpdaveGraphNode) targetedNode;
				if (node.isCollapsed()) {
					node.setCollapsed(false);
				}
				else {
					node.setCollapsed(true);
				}
				this.revalidate();
				this.validate();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
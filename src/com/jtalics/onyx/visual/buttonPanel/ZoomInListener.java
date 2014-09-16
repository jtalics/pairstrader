package com.jtalics.onyx.visual.buttonPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.jtalics.onyx.MainPanel;

/**
 * The listener interface for receiving zoomIn events.
 * The class that is interested in processing a zoomIn
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addZoomInListener<code> method. When
 * the zoomIn event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ZoomInEvent
 */
public class ZoomInListener implements MouseListener {

	/* The Main Panel */
	private MainPanel mainPanel = null;
	/* The Button Panel */
	private ButtonPanel buttonPanel = null;
	
	
	/**
	 * Instantiates a new zoom to fit listener.
	 *
	 * @param mainPanel the main panel
	 * @param buttonPanel the button panel
	 */
	public ZoomInListener(MainPanel mainPanel, ButtonPanel buttonPanel) {
		this.mainPanel = mainPanel;
		this.buttonPanel = buttonPanel;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		buttonPanel.startTimer(false);
		buttonPanel.setZoomInAction(true);
		mainPanel.getWorkflowZoom().setManualZoom(true);
		mainPanel.getWorkflowZoom().zoom(1);
        mainPanel.invalidate();
        mainPanel.validate();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		mainPanel.getWorkflowZoom().setManualZoom(true);
		buttonPanel.startTimer(true);
		buttonPanel.setZoomInAction(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		mainPanel.getWorkflowZoom().setManualZoom(false);
		buttonPanel.startTimer(false);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}
}

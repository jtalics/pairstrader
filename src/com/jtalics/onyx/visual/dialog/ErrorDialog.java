package com.jtalics.onyx.visual.dialog;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * The Class ErrorDialog.
 * Used to display any Errors
 * in the Workflow Applet
 */
public class ErrorDialog extends JDialog {
	
	
	/* Default Serial Version UID */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new error dialog.
	 *
	 * @param msg the msg
	 */
	public ErrorDialog(String msg) {
		JTextPane pane = new JTextPane();
		pane.setText(msg);
		this.getContentPane().add(new JScrollPane(pane));
		setVisible(true);
	}

}

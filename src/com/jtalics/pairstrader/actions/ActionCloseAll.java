package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.trades.PositionTableRow;

public class ActionCloseAll extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionCloseAll(MainFrame mainFrame) {
		super("Close All");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (mainFrame.positionPanel.model.getRows().size() > 0) {
			String message = "Really close all open pairs?";
			if (JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(mainFrame, message, "About", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				panic();
			}			
		}
	}

	private void panic() {
		for (PositionTableRow row : mainFrame.positionPanel.model.getRows()) {
			mainFrame.positionPanel.close(row);
		}
	}
}

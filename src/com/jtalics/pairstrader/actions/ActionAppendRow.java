package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.stocks.SecurityTableModel;

public class ActionAppendRow extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionAppendRow(MainFrame mainFrame) {
		super("Append row");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.securityPanel.model.appendRow();
	}
}

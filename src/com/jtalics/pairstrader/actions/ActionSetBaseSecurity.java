package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.stocks.SecurityTableModel;

public class ActionSetBaseSecurity extends AbstractAction {

	private final MainFrame mainFrame;
	private final SecurityTableModel model;
	private final int rowIndex;

	public ActionSetBaseSecurity(MainFrame mainFrame, SecurityTableModel model, int rowIndex) {
		super("Set as base");
		this.mainFrame = mainFrame;
		this.model = model;
		this.rowIndex = rowIndex;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: Unsubscribe
		model.setBaseSecurity(rowIndex);
		mainFrame.pairsPanel.model.fireTableDataChanged();
	}
}

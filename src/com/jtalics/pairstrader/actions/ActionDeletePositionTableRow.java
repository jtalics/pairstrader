package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.trades.PositionTableModel;
import com.jtalics.pairstrader.trades.PositionTableRow;

public class ActionDeletePositionTableRow extends AbstractAction {

	private final MainFrame mainFrame;
	private final PositionTableModel model;
	private final int rowIndex;

	public ActionDeletePositionTableRow(MainFrame mainFrame, PositionTableModel model, int rowIndex) {
		super("Delete");
		this.mainFrame = mainFrame;
		this.model = model;
		this.rowIndex = rowIndex;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Unsubscribe and delete row
		PositionTableRow row = model.getRow(rowIndex);
		if (row.baseTickerId != Integer.MIN_VALUE) { // already unsubscribed?
			mainFrame.cancelMktData(row.baseTickerId);
		}
		if (row.mateTickerId != null && row.mateTickerId  != Integer.MIN_VALUE) { // already unsubscribed?
			mainFrame.cancelMktData(row.mateTickerId);
		}
		model.removeRowAt(rowIndex);
	}
}

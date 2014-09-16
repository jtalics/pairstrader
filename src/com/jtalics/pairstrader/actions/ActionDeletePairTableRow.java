package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.pairs.PairTableModel;

public class ActionDeletePairTableRow extends AbstractAction {

	private final MainFrame mainFrame;
	private final PairTableModel model;
	private final int rowIndex;

	public ActionDeletePairTableRow(MainFrame mainFrame, PairTableModel model, int rowIndex) {
		super("Delete");
		this.mainFrame = mainFrame;
		this.model = model;
		this.rowIndex = rowIndex;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.removeRowAt(rowIndex);
	}
}

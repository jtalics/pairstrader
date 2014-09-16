package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.pairs.PairTableModel;
import com.jtalics.pairstrader.stocks.SecurityTableRowPair;

import TestJavaClient.OrderDlg;

public class ActionCreatePair extends AbstractAction {

	private final MainFrame mainFrame;
	private final PairTableModel model;
	private final SecurityTableRowPair pair;

	public ActionCreatePair(MainFrame mainFrame, PairTableModel model, SecurityTableRowPair pair) {
		super("Create "+pair.toString());
		this.mainFrame = mainFrame;
		this.model = model;
		this.pair = pair;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

//		int baseTickerId = mainFrame.nextValidTickerId++;
//		int mateTickerId = mainFrame.nextValidTickerId++;
//		mainFrame.reqMktData(baseTickerId, pair.getBaseRow().contract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
//		mainFrame.reqMktData(mateTickerId, pair.getMateRow().contract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);

		model.appendRow(pair);
	}
}

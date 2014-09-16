package com.jtalics.pairstrader.pairs;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.ib.contracts.StkContract;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.PriceWrapper;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.stocks.SecurityTableRow;
import com.jtalics.pairstrader.stocks.SecurityTableRowPair;

public class PairTableModel extends AbstractTableModel {

	private final List<PairTableRow>  rows = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Pair";
		case 1:
			return "bQty";
		case 2:
			return "mQty";
		case 3:
			return "Bid";
		case 4:
			return "Ask";
		case 5:
			return "Buy";
		case 6:
			return "Sell";
		default:
			return "?";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return String.class;
		case 1:
		case 2:
			return Integer.class;
		case 3:
		case 4:
			return Double.class;
		case 5:
		case 6:
			return TableButton.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0 || columnIndex == 2) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PairTableRow row = rows.get(rowIndex);
		switch(columnIndex) {
		case 0:
			return new PairRenderer.PairWrapper(row.pair);
		case 1:
			return row.baseQuantity;
		case 2:
			return row.calcMateQuantity();
		case 3:
			//return new PriceWrapper(row.getBid(), row.getBid() - row.getPrevBid(), 0, false);
			return row.getBid();
		case 4:
			//return new PriceWrapper(row.getAsk(), row.getAsk() - row.prevAsk(), 0, false);
			return row.getAsk();
		case 5:
		case 6:	
			return true; // TODO: when should we disable, if ever?
		default:
			return "?";
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			rows.get(rowIndex).baseQuantity = (Integer)value;
			fireTableDataChanged();
		}
	}

	public boolean appendRow(SecurityTableRowPair pair, Integer baseQty) {
		PairTableRow row = new PairTableRow(pair, baseQty);
		rows.add(row);
		fireTableDataChanged();
		return true;		
	}
	
	public boolean appendRow(SecurityTableRowPair pair) {
		Integer d = PreferencesDialog.getDefaultBaseQuantity();
		return appendRow(pair, d);
	}

	public void removeRowAt(int rowIndex) {
		rows.remove(rowIndex);
		fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));	
	}

	public PairTableRow getRow(int rowIndex) {
		return rows.get(rowIndex);
	}

	public void clear() {
		rows.clear();
	}

	public List<PairTableRow> getRows() {
		return rows;
	}
}
	

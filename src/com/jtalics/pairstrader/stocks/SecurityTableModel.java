package com.jtalics.pairstrader.stocks;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.ib.client.Contract;
import com.ib.client.TickType;
import com.ib.contracts.StkContract;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.MarketDataListener;
import com.jtalics.pairstrader.Outputable;
import com.jtalics.pairstrader.PriceWrapper;
import com.jtalics.pairstrader.ServerErrorListener;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;
import com.jtalics.pairstrader.events.ManagedAccountsEvent;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;
import com.jtalics.pairstrader.trades.PositionTableModel.PositionTableModelEvent.Kind;
import com.jtalics.pairstrader.trades.PositionTableRow;

public class SecurityTableModel extends AbstractTableModel implements MarketDataListener, ServerListener, ServerErrorListener {

	static public class SecurityTableModelEvent extends TableModelEvent {

		public enum Kind {
			SubscribeRow, UnsubscribeRow, FireSyntheticAskEvent, FireSyntheticLastEvent, FireSyntheticBidEvent, TickerMessage
		}
		
		public final Kind kind;
		public final Object details;
		
		public SecurityTableModelEvent(TableModel source, Kind kind, Object details) {
			super(source);
			this.kind = kind;
			this.details = details;
		}
	}
	
	private final List<SecurityTableRow> rows = new ArrayList<>();
	private final Map<Integer, SecurityTableRow> tickerIdToRow = new HashMap<>();
	private final Map<SecurityTableRow, Integer> rowToTickerId = new HashMap<>();
	int baseRowIndex = -1;
	private Outputable out;
	private boolean debug=false;
	private final Map<Contract, SecurityTableRow> contractToRow = new HashMap<>();

	public SecurityTableModel(Outputable out) {
		super();
		this.out = out;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Symbol";
		case 1:
			return "Bid";
		case 2:
			return "Last";
		case 3:
			return "Ask";
		default:
			return "?";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
		case 2:
		case 3:
			return Double.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return true;
		case 1:
		case 2:
		case 3:
			// TODO: if (MainFrame.ConnectedTo)
			return true;
		default:
			throw new RuntimeException("bad col index="+columnIndex);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		SecurityTableRow row = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return new SecurityRenderer.ContractWrapper(row.contract, rowIndex == baseRowIndex);
		case 1:
			return new PriceWrapper(row.bid, row.bid - row.prevBid, row.errorCode, rowIndex == baseRowIndex);
		case 2:
			return new PriceWrapper(row.last, row.last - row.prevLast, row.errorCode, rowIndex == baseRowIndex);
		case 3:
			return new PriceWrapper(row.ask, row.ask - row.prevAsk, row.errorCode, rowIndex == baseRowIndex);
		default:
			return "?";
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SecurityTableRow oldRow = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			Contract contract = new StkContract((String) value);
			SecurityTableRow newRow = new SecurityTableRow(contract);
			if (!newRow.contract.m_symbol.equals(oldRow.contract.m_symbol)) {
				rows.set(rowIndex, newRow);
				contractToRow.put(contract, newRow);
				fireTableDataChanged();
				fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.UnsubscribeRow,oldRow));
				fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.SubscribeRow,newRow));
			}
			break;
		case 1:
			oldRow.prevBid=oldRow.bid;
			oldRow.bid = (Double)value;

			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.SubscribeRow,oldRow));
			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.FireSyntheticBidEvent,oldRow));
			break;
		case 2:
			oldRow.prevLast=oldRow.last;
			oldRow.last = (Double)value;
//			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.UnsubscribeRow,row));
			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.SubscribeRow,oldRow));
			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.FireSyntheticLastEvent,oldRow));
			fireTableDataChanged();
			break;
		case 3:
			oldRow.prevAsk=oldRow.ask;
			oldRow.ask = (Double)value;
//			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.UnsubscribeRow,row));
			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.SubscribeRow,oldRow));
			fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.FireSyntheticAskEvent,oldRow));
			fireTableDataChanged();
			break;
		}
	}

	@Override
	public void onMdPriceTickEvent(MdPriceTickEvent ev) {

		// Determine which symbol the id corresponds to and update the price
		SecurityTableRow row = tickerIdToRow.get(ev.tickerId);
		if (row == null) return; // this MD is not for us!
		switch(ev.field) {
		case TickType.BID:
			row.prevBid = row.bid;
			if (ev.price != null) {
				row.bid = ev.price;
			}
			else {
				row.bid = Double.NaN;
			}
			break;
		case TickType.ASK:
			row.prevAsk = row.ask;
			if (ev.price != null) {
				row.ask = ev.price;
			}
			else {
				row.ask=Double.NaN;
			}
			break;
		case TickType.LAST:
			row.prevLast = row.last;
			if (ev.price != null) {
				row.last = ev.price;
			}
			else {
				row.last = Double.NaN;
			}
			break;
		case TickType.HIGH:
		case TickType.LOW:
		case TickType.CLOSE:
			// ignore for now
			break;
		default:
			new Exception("Unhandled price : " + ev.field).printStackTrace();
		}
		row.errorCode = 0;
		fireTableDataChanged();
		fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.TickerMessage,row));
		//if (debug) out.println(mde.data.id + " | " + mde.data.field + " | "+mde.data.price);
	}

	@Override
	public void onServerErrorEvent(ErrorEvent twsErrorEvent) {
		if (debug) System.out.println("SECURITYTABLEMODEL Received " + twsErrorEvent);
		int id = twsErrorEvent.id;
		SecurityTableRow row = tickerIdToRow.get(id);
		if (row != null) {
			row.last = Double.NaN;
			row.errorCode = twsErrorEvent.errorCode;
			fireTableDataChanged();
		}
		else {
			if (debug) System.out.println("SECURITYTABLEMODEL id = " + id);
		}
	}

	public void appendRow() {
		rows.add(new SecurityTableRow(new StkContract("?")));
		fireTableDataChanged();
	}

	public int getBaseRowIndex() {
		return baseRowIndex;
	}

	public void removeRowAt(int rowIndex) {

		SecurityTableRow row = rows.remove(rowIndex);
		
		if (baseRowIndex == rowIndex) {
			baseRowIndex = -1;
		}
		else if (baseRowIndex > rowIndex) {
			baseRowIndex--;
		}
		fireTableChanged(new SecurityTableModelEvent(this, SecurityTableModelEvent.Kind.UnsubscribeRow, row));
		contractToRow.remove(row.contract);
		fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));	
	}

	public void setBaseSecurity(int rowIndex) {
		baseRowIndex = rowIndex;
		fireTableDataChanged();
	}

	@Override
	public void onServerEvent(ServerEvent twsEvent) {
		if (twsEvent instanceof ManagedAccountsEvent) {
			if (((ManagedAccountsEvent) twsEvent).accountsList != null) {
				for (SecurityTableRow row : rows) {
					fireTableChanged(new SecurityTableModelEvent(this, SecurityTableModelEvent.Kind.SubscribeRow, row));
					// connect
				}
			}
			else {
				for (SecurityTableRow row : rows) {
					row.bid=Double.NaN;
					row.prevBid=Double.NaN;
					row.last=Double.NaN;
					row.prevLast=Double.NaN;
					row.ask=Double.NaN;
					row.prevAsk=Double.NaN;
					fireTableChanged(new SecurityTableModelEvent(this, SecurityTableModelEvent.Kind.UnsubscribeRow, row));
					rowToTickerId.clear();
					tickerIdToRow.clear();
				}
			}
		}
	}

	public void removeTickerId(Integer tickerId) {
		if (debug) out.println("SECURITYTABLEMODEL: removing Ticker Id" + tickerId);
		SecurityTableRow row = tickerIdToRow.get(tickerId);
		if (row == null) {
			new Exception("SECURITYTABLEMODEL tickerIdToRow does not contain key: " +tickerId).printStackTrace();;
		}
		rowToTickerId.remove(tickerIdToRow.get(tickerId));
		tickerIdToRow.remove(tickerId);
	}

	public Integer getTickerId(SecurityTableRow row) {
		return rowToTickerId.get(row);
	}

	public void addTickerId(Integer id, SecurityTableRow row) {
		if (debug) System.out.println("SECURITYTABLEMODEL: adding Ticker Id" + id);
		tickerIdToRow.put(id, row);
		rowToTickerId.put(row, id);
	}

	public boolean isBaseRow(SecurityTableRow row) {
		return row == getBaseRow();
	}
	
	public SecurityTableRow getBaseRow() {
		return rows.get(getBaseRowIndex());
	}

	public Set<Integer> getTickerIds() {
		return tickerIdToRow.keySet();
	}

	public SecurityTableRow getRow(int baseRowIndx) {
		return rows.get(baseRowIndx);
	}

	public void addRow(SecurityTableRow row) {
		rows.add(row);
		contractToRow.put(row.contract,row);
		fireTableChanged(new SecurityTableModelEvent(this,SecurityTableModelEvent.Kind.SubscribeRow,row));
		fireTableDataChanged();
	}

	public SecurityTableRow getRowForContract(Contract contract) {
		return contractToRow.get(contract);
	}
	
	public SecurityTableRow findRow(String symbol) {

		// TODO: we should be able to use contractToRow here, but IB's equals/hashcode broken
		for (SecurityTableRow row : rows) {
			if (row.contract.m_symbol.equals(symbol)) {return row;}
		}
		return null;
	}
	
	public SecurityTableRowPair makePair(SecurityTableRow mate) {
		return new SecurityTableRowPair(this, mate);
	}

	public void clear() {
		rows.clear();
	}

	public List<SecurityTableRow> getRows() {
		return rows;
	}

}

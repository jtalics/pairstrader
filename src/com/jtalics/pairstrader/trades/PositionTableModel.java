package com.jtalics.pairstrader.trades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.ib.client.Order;
import com.ib.client.TickType;
import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MarketDataListener;
import com.jtalics.pairstrader.Outputable;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.PriceWrapper;
import com.jtalics.pairstrader.ServerErrorListener;
import com.jtalics.pairstrader.events.ExecDetailsEvent;
import com.jtalics.pairstrader.events.ManagedAccountsEvent;
import com.jtalics.pairstrader.events.OpenOrderEvent;
import com.jtalics.pairstrader.events.OrderStatusEvent;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.trades.CloseStateRenderer.RowClosingState;

public class PositionTableModel extends AbstractTableModel implements ServerListener, ServerErrorListener, MarketDataListener {
	
	static public class PositionTableModelEvent extends TableModelEvent {

		public enum Kind {
			PositionNeedsClosed, UnsubscribeTicker, Connect, Disconnect
		}
		
		public final Kind kind;
		public final Object details;
		
		public PositionTableModelEvent(TableModel source, Kind kind, Object details) {
			super(source);
			this.kind = kind;
			this.details = details;
		}
	}
	 
	private final List<PositionTableRow>  rows = new ArrayList<>();
	private final Map<Integer, PositionTableRow> orderIdToRow = new HashMap<>();
	private final Map<Integer, PositionTableRow> tickerIdToRow = new HashMap<>();
	private final Outputable out;
	private final boolean debug = false;
	
	public PositionTableModel(Outputable out) {
		this.out = out;
	}
	
	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		if (PreferencesDialog.getAutoCloseMethod() == AutoCloseMethod.Stops) {return 8;} 
		return 6;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch(columnIndex) {
		case 0:
			return "Pair";
		case 1:
			return "Quantities";
		case 2:
			return "Bid";
		case 3:
			return "Last";
		case 4:
			return "Ask";
		case 5:
			return "P&L";
		case 6:
			return "sLoss";
		case 7:
			return "sGain";
		default:
			return "?";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch(columnIndex) {
		case 0:
		case 1:
			return OwnablePairRenderer.Wrapper.class;
		case 2:
		case 3:
		case 4:
			return Double.class;
		case 5:
			return null;
		case 6:
		case 7:
			return Double.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return false;
		case 6:
		case 7:
			PositionTableRow row = rows.get(rowIndex);
			boolean enabled = !(row.pair.baseActQty == 0.0 && row.pair.mateActQty == 0.0);
			return enabled;
		default:
			return false;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PositionTableRow row = rows.get(rowIndex);
		switch(columnIndex) {
		case 0:
		case 1:
			return new OwnablePairRenderer.Wrapper(row);
		case 2:
			Double bid = row.getBid();
			return bid;
		case 3:
			Double last = row.getLast();
			return last;
		case 4:
			Double ask = row.getAsk();
			return ask;
		case 5:
			RowClosingState state = null;
			if (row.baseClosingOrder != null || row.mateClosingOrder != null) {
				state = RowClosingState.Closing;
				if (closed(row)) {
					// System.out.println("CLOSED!");					
					state = RowClosingState.Closed;					
				}
			}
			else {
				state = RowClosingState.Close;
			}
			return state;
		case 6:
			return new PriceWrapper(row.stopLoss, 0.0, 0, false);
		case 7:
			return new PriceWrapper(row.stopGain, 0.0, 0, false);
		default:
			return "?";
		}
	}

	private static boolean closed(PositionTableRow row) {

		if (row.pair.baseActQty == 0 && row.pair.mateActQty == 0) {
			return true;			
		}
		if (row.baseOpeningOrder==null
				|| row.baseOpeningOrderState.status==null
				|| (!row.baseOpeningOrderState.status.equals("Filled"))
				&& !row.baseOpeningOrderState.status.equals("Inactive")) { 
			return false;
		}
		if (row.baseClosingOrder==null
				|| row.baseClosingOrderState.status==null
				|| (!row.baseClosingOrderState.status.equals("Filled"))
				&& !row.baseClosingOrderState.status.equals("Inactive")) {
			return false;
		}
		if (row.mateOpeningOrder==null
				|| row.mateOpeningOrderState.status==null
				|| (!row.mateOpeningOrderState.status.equals("Filled"))
				&& !row.mateOpeningOrderState.status.equals("Inactive")) {
			return false;
		}
		if (row.mateClosingOrder==null
				|| row.mateClosingOrderState.status==null
				|| (!row.mateClosingOrderState.status.equals("Filled"))
				&& !row.mateClosingOrderState.status.equals("Inactive")) {
			return false;
		}

//System.out.println("row.baseOpeningOrderState="+row.baseOpeningOrderState);
//System.out.println("row.baseClosingOrderState="+row.baseClosingOrderState);
//System.out.println("row.mateOpeningOrderState="+row.mateOpeningOrderState);
//System.out.println("row.mateClosingOrderState="+row.mateClosingOrderState);
//System.out.println();
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		switch(columnIndex) {
		case 6:
			rows.get(rowIndex).stopLoss=(Double)value;
			break;
		case 7:
			rows.get(rowIndex).stopGain=(Double)value;
			break;
		default:
		}
	}
	
	@Override
	public void onServerEvent(ServerEvent twsEvent) {

		if (debug) out.println(getClass().getSimpleName()+" received TWS event of type " + twsEvent.getClass().getSimpleName());

		if (twsEvent instanceof OpenOrderEvent) {
			OpenOrderEvent ooe = (OpenOrderEvent)twsEvent;
			if (debug) out.println("Open order event received: "+ ooe.toString());
			fireTableDataChanged();
		}
		else if (twsEvent instanceof OrderStatusEvent) {
			OrderStatusEvent ose = (OrderStatusEvent)twsEvent;
			int orderId = ose.orderId;
			if (debug) out.println("Order status event received:"+ose.toString());
			PositionTableRow row = orderIdToRow.get(orderId);
			if (row == null) return; 
			// Could be somebody else's order (or an order on a deleted row)
			row.setState(orderId, ose);
			handleFills(row,orderId);
			fireTableDataChanged();
		}
		else if (twsEvent instanceof ExecDetailsEvent) {
			ExecDetailsEvent ede = (ExecDetailsEvent)twsEvent;
			if (debug) out.println("Exec Details event received: " + ede.toString());
			int orderId = ede.execution.m_orderId;
			PositionTableRow row = orderIdToRow.get(orderId);
		}
		if (twsEvent instanceof ManagedAccountsEvent) {
			if (((ManagedAccountsEvent) twsEvent).accountsList != null) {
				for (PositionTableRow row : rows) {
					fireTableChanged(new PositionTableModelEvent(this, PositionTableModelEvent.Kind.Connect, row));
				}
			}
			else {
				// Disconnect event received
				orderIdToRow.clear();
				tickerIdToRow.clear();
				for (PositionTableRow row : rows) {
					row.setBaseBid(Double.NaN);
					row.setBaseLast(Double.NaN);
					row.setBaseAsk(Double.NaN);
					row.setMateBid(Double.NaN);
					row.setMateLast(Double.NaN);
					row.setMateAsk(Double.NaN);
					fireTableDataChanged();
					fireTableChanged(new PositionTableModelEvent(this, PositionTableModelEvent.Kind.Disconnect, row));
				}
			}
		}
		else {
			if (debug) out.println("Position Table RECEIVED EVENT: " + twsEvent.getClass().getSimpleName());
		}
	}

	private void handleFills(PositionTableRow row, int orderId) {
		// Checks to see if an individual order is filled, and unsubscribes if so.
		// Checks to see if a base or mate position is filled, unsubscribes from ticker if so. 
		// Checks to see if complete pair position is filled, and sets last price to filled price.
		if (orderId == row.baseOpeningOrder.m_orderId) {
			handleBaseOpeningOrderFill(row);
			handlePairFill(row);
		}
		else if (row.baseClosingOrder != null && orderId == row.baseClosingOrder.m_orderId) {
			handleBaseClosingOrderFill(row);
			handlePairFill(row);
		}
		else if (orderId == row.mateOpeningOrder.m_orderId) {
			handleMateOpeningOrderFill(row);
			handlePairFill(row);
		}
		else if (row.mateClosingOrder != null && orderId == row.mateClosingOrder.m_orderId) {
			handleMateClosingOrderFill(row);
			handlePairFill(row);		
		}
		else {
			new Exception("Order id not found: " + orderId).printStackTrace();
		}
	}

	private void handlePairFill(PositionTableRow row) {
		// TODO - set bid,last,ask?
	}

	private void handleBaseOpeningOrderFill(PositionTableRow row) {
		if (row.baseOpeningOrderState.status.equals("Filled")) {
			//removeOrderId(row.baseOpeningOrder.m_orderId);	// Stop listening to server order events
			if (debug) out.print("POSITIONTABLEMODEL: baseOpeningOrder filled --- ");
			if (row.mateClosingOrder == null) {
				if (debug) out.println(" NO such baseClosingOrder ---");
			}
			else {
				if (row.baseClosingOrderState.status.equals("Filled")) {
					if (debug) out.println(" baseClosingOrder filled --- ");
					unsubscribeTicker(row.baseTickerId);
				}
				else if (debug) out.println(" baseClosingOrder not filled. ");
			}
		}
	}

	private void handleBaseClosingOrderFill(PositionTableRow row) {
		if (row.baseClosingOrderState.status.equals("Filled")) {
			if (debug) out.print("POSITIONTABLEMODEL: baseClosingOrder filled --- ");
			//removeOrderId(row.baseClosingOrder.m_orderId);	// Stop listening to server order events
			if (row.baseOpeningOrderState.status.equals("Filled")) {
				if (debug) out.println(" baseOpeningOrder filled --- ");
				unsubscribeTicker(row.baseTickerId);
			}
			else if (debug) {
					out.println(" baseOpeningOrder not filled. ");
			}
		}		
	}

	private void handleMateOpeningOrderFill(PositionTableRow row) {
		if (row.mateOpeningOrderState.status.equals("Filled")) {
			if (debug) out.print("POSITIONTABLEMODEL: mateOpeningOrderState filled --- ");
			//removeOrderId(row.mateOpeningOrder.m_orderId);	// Stop listening to server order events
			if (row.mateClosingOrder == null) {
				if (debug) out.println(" NO such mateClosingOrder ---");
			}
			else {
				if (row.mateClosingOrderState.status.equals("Filled")) {
					if (debug) out.println(" mateClosingOrder filled --- ");
					unsubscribeTicker(row.mateTickerId);
			  }
			  else if (debug) out.println(" mateClosingOrder not filled. ");
			}
		}
	}

	private void handleMateClosingOrderFill(PositionTableRow row) {
		if (row.mateClosingOrderState.status.equals("Filled")) {
			if (debug) out.print("POSITIONTABLEMODEL: mateClosingOrder filled --- ");
			//removeOrderId(row.mateClosingOrder.m_orderId); // Stop listening to server order events
			if (row.mateOpeningOrder == null) {
				if (debug) out.println(" NO such mateOpeningOrder ---");				
			}
			else {
				if (row.mateOpeningOrderState.status.equals("Filled")) {
					if (debug)	out.println(" mateClosingOrder filled --- ");
					unsubscribeTicker(row.mateTickerId);
				}
				else if (debug) out.println(" mateClosingOrder not filled. ");
			}
		}
	}

	private void unsubscribeTicker(int tickerId) {
		if (debug) out.println("POSITIONTABLEMODEL: Canceling & unsubscribing tickerId = "+tickerId);
		fireTableChanged(	new PositionTableModelEvent(this,PositionTableModelEvent.Kind.UnsubscribeTicker,tickerId));
		removeTickerId(tickerId);
	}

	@Override
	public void onServerErrorEvent(ErrorEvent ee) {
		//if (debug) out.println("POSITIONTABLEMODEL: heard TWS error:" + ee.errorMsg);
		switch (ee.errorCode) {
		case 201: // order rejected
		case 321: // invalid API request
			int rejectedOrderId = ee.id;
			
		default:
			new Exception(ee.id+" | "+ee.errorCode+" | "+ee.errorMsg).printStackTrace();
		}
	}

	@Override
	public void onMdPriceTickEvent(MdPriceTickEvent ev) {
		PositionTableRow row = tickerIdToRow.get(ev.tickerId);
		if (row == null) return; // this MD is not for us!

		if (row.baseTickerId == ev.tickerId) {
			switch (ev.field) {
			case TickType.BID:
				if (ev.price != null) {
					row.setBaseBid(ev.price);
				}
				else {
					row.setBaseBid(Double.NaN);

				}
				break;
			case TickType.LAST:
				if (ev.price != null) {
					row.setBaseLast(ev.price);
				}
				else {
					row.setBaseLast(Double.NaN);

				}
				break;
			case TickType.ASK:
				if (ev.price != null) {
					row.setBaseAsk(ev.price);
				}
				else {
					row.setBaseAsk(Double.NaN);

				}
				break;
			case TickType.HIGH:
			case TickType.LOW:
			case TickType.CLOSE:
				// ignore for now
				break;
			default:
				new Exception("Unhandled price field: " + ev.field).printStackTrace();
			}
			fireTableDataChanged();
		}
		else if (row.mateTickerId != null && row.mateTickerId == ev.tickerId) {
			switch(ev.field) {
			case TickType.BID:
				if (ev.price != null) {
					row.setMateBid(ev.price);
				}
				else {
					row.setMateBid(Double.NaN);
				}
				break;
			case TickType.LAST:
				if (ev.price != null) {
					row.setMateLast(ev.price);
				}
				else {
					row.setMateLast(Double.NaN);
				}
				break;
			case TickType.ASK:
				if (ev.price != null) {
					row.setMateAsk(ev.price);
				}
				else {
					row.setMateAsk(Double.NaN);
				}
				break;
			case TickType.HIGH:
			case TickType.LOW:
			case TickType.CLOSE:
				// ignore for now
				break;
			default:
				new Exception("Unhandled tick price field: " + ev.field).printStackTrace();
			}
			fireTableDataChanged();			
		}
//		else {
//			new Exception("Unhandled ticker id: " + ev.tickerId).printStackTrace();
//		}
		maybeAutoclose(row);
	}

	private void maybeAutoclose(PositionTableRow row) {

		switch (PreferencesDialog.getAutoCloseMethod()) {
		case None:
			break;
		case Stops:
			double last = row.getLast();
			if (!Double.isNaN(last)) { 
				if (!Double.isNaN(row.stopLoss) && last < -row.stopLoss) {
					row.stopLoss = row.stopGain = Double.NaN;
					this.fireTableChanged(
  						new PositionTableModelEvent(this,PositionTableModelEvent.Kind.PositionNeedsClosed,row));
	  			Main.playStopLoss();
		  	}
			
			  if (!Double.isNaN(row.stopGain) && last > row.stopGain) {
					row.stopLoss = row.stopGain = Double.NaN;
  				this.fireTableChanged(
  						new PositionTableModelEvent(this,PositionTableModelEvent.Kind.PositionNeedsClosed,row));
			  	Main.playStopGain();
			  }
			}
			break;
		case UserDefined:
			break;
		}
	}

	public boolean appendOwnablePair(OwnablePair op, Order baseOrder, int baseTickerId, Order mateOrder, Integer mateTickerId, double baseBid, double baseLast, double baseAsk, double mateBid, double mateLast, double mateAsk) {
		if (orderIdToRow.containsKey(baseOrder.m_orderId) || (mateOrder != null && orderIdToRow.containsKey(mateOrder.m_orderId))) {return false;}	
		PositionTableRow row = new PositionTableRow(op,baseOrder, baseTickerId, mateOrder, mateTickerId, baseBid,baseLast, baseAsk,mateBid,mateLast,mateAsk);
		rows.add(row);
		addOrderId(baseOrder.m_orderId, row);
		if (mateOrder != null) {
			addOrderId(mateOrder.m_orderId, row);
		}
		addTickerId(baseTickerId, row);
		if (mateTickerId != null) {
			addTickerId(mateTickerId, row);
		}
		fireTableDataChanged();	
		return true;
	}
	
	public PositionTableRow getRow(int rowIndex) {
		return rows.get(rowIndex);
	}

	public void removeRowAt(int rowIndex) {

		PositionTableRow row = rows.get(rowIndex);
		if (row.baseTickerId != Integer.MIN_VALUE) { // already unsubscribed?
			removeTickerId(row.baseTickerId);
		}
		if (row.mateTickerId != null && row.mateTickerId != Integer.MIN_VALUE) { // already unsubscribed?
			removeTickerId(row.mateTickerId);
		}
		removeOrderId(row.baseOpeningOrder.m_orderId);
		if (row.mateOpeningOrder != null) {
			removeOrderId(row.mateOpeningOrder.m_orderId);
		}
		if (row.baseClosingOrder != null) {
			removeOrderId(row.baseClosingOrder.m_orderId);
		}
		if (row.mateClosingOrder != null) {
			removeOrderId(row.mateClosingOrder.m_orderId);
		}
		rows.remove(rowIndex);
		fireTableChanged(new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));	
	}

	public void addOrderId(int orderId, PositionTableRow row) {
		// Start listening to server order events
		if (orderIdToRow.containsKey(orderId)) {
			new Exception("POSITIONTABLE orderIdToRow already contains key: " +orderId).printStackTrace();;
		}
		orderIdToRow.put(orderId, row);
		if (debug) out.println("POSITIONTABLEMODEL: added orderId = " + orderId);
		if (debug) dumpOrderIdToRow();
	}

	public void removeOrderId(int orderId) {
	  // Stop listening to server order events
		PositionTableRow row = orderIdToRow.get(orderId);
		if (row != null) { // unfilled orders remain
			orderIdToRow.remove(orderId);
			if (debug) out.println("POSITIONTABLEMODEL: removed orderId = " + orderId);
			if (debug) dumpOrderIdToRow();
		}
	}

	private void dumpOrderIdToRow() {
		out.print("POSITIONTABLEMODEL: orderIdToRow: ");
		for (Map.Entry<Integer, PositionTableRow> entry: orderIdToRow.entrySet()) {
			out.print("("+entry.getKey()+","+rows.indexOf(entry.getValue())+")");
		}
		out.println();
	}

	public void addTickerId(int tickerId, PositionTableRow row) {
		if (tickerIdToRow.containsKey(tickerId)) {
			new Exception("POSITIONTABLE tickerIdToRow already contains key: " + tickerId).printStackTrace();
		}
		if (debug) out.println("POSITIONTABLEMODEL added tickerId = " + tickerId);
		tickerIdToRow.put(tickerId, row);
		if (debug) dumpTickerIdToRow();
	}

	private void removeTickerId(int tickerId) {
		// Stop listening to market data
		PositionTableRow row = tickerIdToRow.get(tickerId);
		if (row == null) {
			new Exception("POSITIONTABLE tickerIdToRow does not contain key: " + tickerId).printStackTrace();;
		}
		else {
			if (tickerId == row.baseTickerId) {
				row.baseTickerId = Integer.MIN_VALUE;
			}
			else if (row.mateTickerId != null && tickerId == row.mateTickerId) {
				row.mateTickerId = Integer.MIN_VALUE;				
			}
			tickerIdToRow.remove(tickerId);
			if (debug) out.println("POSITIONTABLE removed tickerId = " + tickerId);
		}
		if (debug) dumpTickerIdToRow();
	}

	private void dumpTickerIdToRow() {
		out.print("POSITIONTABLE tickerIdToRow: ");
		for (Map.Entry<Integer, PositionTableRow> entry: tickerIdToRow.entrySet()) {
			out.print("("+entry.getKey()+","+rows.indexOf(entry.getValue())+")");
		}
		out.println();	
	}

	public List<PositionTableRow> getRows() {
		return rows;
	}

	public PositionTableRow getRowForOrderId(Integer orderId) {
		return orderIdToRow.get(orderId);
	}
}

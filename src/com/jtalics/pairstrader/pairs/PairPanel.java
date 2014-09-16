package com.jtalics.pairstrader.pairs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;

import TestJavaClient.OrderDlg;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.ib.client.Order;
import com.ib.contracts.StkContract;
import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.OrderDialog;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.actions.ActionDeletePairTableRow;
import com.jtalics.pairstrader.events.ValidIdEvent;
import com.jtalics.pairstrader.pairs.TableButton.TableButtonListener;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.stocks.SecurityTableRow;
import com.jtalics.pairstrader.stocks.SecurityTableRowPair;
import com.jtalics.pairstrader.trades.OwnablePair;

public class PairPanel extends JPanel implements ServerListener, TableColumnModelListener, Fontible {

	public static Preferences pref=PreferencesDialog.pref.node("VIRT_TABLE");
	
	public final MainFrame mainFrame;
	public final PairTableModel model = new PairTableModel() {
    @Override
		public void fireTableChanged(TableModelEvent e) {
    	// By default, selection is lost upon fireTableDataChanged
			int[] selectedRows = table.getSelectedRows();
    	super.fireTableChanged(e);
    	 if (e.getFirstRow()==0 && e.getLastRow() == Integer.MAX_VALUE && e.getColumn() == TableModelEvent.ALL_COLUMNS && e.getType() == TableModelEvent.UPDATE ) {
    		 // fireTableDataChanged()
    		 for (Integer i : selectedRows) {
    			 table.addRowSelectionInterval(i,i);
    		 }
    	 }
    }
	};
	public final JTable table = new JTable(model);
	public final JPopupMenu popup = new JPopupMenu();
	private final List<Fontible> fontibles = new ArrayList<>();

	public PairPanel(final MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		JScrollPane scrollPane = new JScrollPane(table);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		popup.setBorder(new BevelBorder(BevelBorder.RAISED));

		int nCol = table.getColumnCount();
		Preferences prefWidths  = pref.node("WIDTHS");
		for (int i=0; i<nCol; i++) {
			String name = table.getColumnName(i);
			TableColumn col = table.getColumnModel().getColumn(i);
			int w=prefWidths.getInt(name,col.getWidth());
			col.setPreferredWidth(w);
		}
		table.setToolTipText("This is the Pairs table");
		table.getColumnModel().addColumnModelListener(this);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			private void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					Point p = e.getPoint();
					int rowIndex = table.rowAtPoint(p);
					popup.removeAll();
					popup.add(new JMenuItem(new ActionDeletePairTableRow(PairPanel.this.mainFrame, model, rowIndex)));

					popup.show(table, e.getX(), e.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		PairRenderer pr = new PairRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(pr);
		fontibles.add(pr);
		
		//table.getColumnModel().getColumn(3).setCellRenderer(new PriceRenderer(false));
		//table.getColumnModel().getColumn(4).setCellRenderer(new PriceRenderer(true));

		TableButton buyButtonRenderer = new TableButton("Buy");
		fontibles.add(buyButtonRenderer);
		TableButton buyButtonEditor = new TableButton("Buy");
		buyButtonEditor.addTableButtonListener(getBuySellButtonListener(true));
		table.getColumnModel().getColumn(5).setCellRenderer(buyButtonRenderer);
		table.getColumnModel().getColumn(5).setCellEditor(buyButtonEditor);

		TableButton sellButtonRenderer = new TableButton("Sell");
		fontibles.add(sellButtonRenderer);
		TableButton sellButtonEditor = new TableButton("Sell");
		sellButtonEditor.addTableButtonListener(getBuySellButtonListener(false));
		table.getColumnModel().getColumn(6).setCellRenderer(sellButtonRenderer);
		table.getColumnModel().getColumn(6).setCellEditor(sellButtonEditor);
		mainFrame.addTwsListener(this);
	}

	private TableButtonListener getBuySellButtonListener(final boolean orderAction) {

		TableButtonListener tbl = new TableButtonListener() {
			@Override
			public void tableButtonClicked(int rowIndex, int columnIndex) {

				if (!mainFrame.isConnected()) {
					JOptionPane.showMessageDialog(PairPanel.this, "Please connect first.");
					return;
				}
				placeOrder(rowIndex, orderAction);
			}

		};
		return tbl;
	}
	
	private synchronized void placeOrder(int rowIndex, boolean pairAction /*true = BUY, false = SELL*/) {

		PairTableRow row = model.getRow(rowIndex);
		OwnablePair ownablePair;
		SecurityTableRow baseRow = row.pair.getBaseRow();
		SecurityTableRow mateRow = row.pair.getMateRow();
		double basePrice = Double.NaN;
		double matePrice = Double.NaN;
		int baseQuantity = row.baseQuantity; // should never be non-positive

		Integer mateQuantity = row.calcMateQuantity();

		if (pairAction) { // BUY PAIR: Sell into ask for base, buy into bid for mate
			basePrice = baseRow.ask;
			matePrice = mateRow.bid;
		}
		else { // Buy base, Sell mate
			basePrice = baseRow.ask;
			matePrice = mateRow.bid;
			baseQuantity = -baseQuantity;
			if (mateQuantity != null) mateQuantity = -mateQuantity;
		}
		try {
			ownablePair = new OwnablePair(baseRow.contract, basePrice, baseQuantity, mateRow.contract, matePrice, mateQuantity);
		}
		catch (CloneNotSupportedException e) {
			// TODO should never be thrown - double check that it won't
			e.printStackTrace();
			return;
		}

		Order baseOrder = new Order();
		baseOrder.m_orderId = mainFrame.nextValidOrderId++;
		baseOrder.m_action = ownablePair.baseTargQty < 0 ? "SELL" : "BUY";
		baseOrder.m_totalQuantity = Math.abs((int) ownablePair.baseTargQty);
		baseOrder.m_orderType = "MKT";
		baseOrder.m_lmtPrice = 0.0;
		baseOrder.m_auxPrice = 0.0;

		int baseTickerId=mainFrame.nextValidTickerId++;
		boolean b = PreferencesDialog.getShowDialog();
		if (b) {
			OrderDialog baseOD = new OrderDialog(mainFrame, baseOrder, ownablePair.baseContract);
			baseOD.setTitle("Specify BASE");
			Main.centerOnScreen(baseOD);
			baseOD.show();
			if (!baseOD.m_rc)
				return;
			baseOrder.m_orderId = baseOD.orderId;
		}

		Integer mateTickerId = null;
		Order mateOrder=null;
		if (ownablePair.mateTargQty != null && ownablePair.mateTargQty != 0) {
			mateOrder = new Order();
			mateOrder.m_orderId = mainFrame.nextValidOrderId++;
			mateOrder.m_action = ownablePair.mateTargQty < 0 ? "SELL" : "BUY";
			mateOrder.m_totalQuantity = Math.abs((int) ownablePair.mateTargQty);
			mateOrder.m_orderType = "MKT";
			mateOrder.m_lmtPrice = 0.0;
			mateOrder.m_auxPrice = 0.0;
			if (b) {
				OrderDialog mateOD = new OrderDialog(mainFrame, mateOrder, ownablePair.mateContract);

				mateOD.setTitle("Specify MATE");
				Main.centerOnScreen(mateOD);
				mateOD.show();
				if (!mateOD.m_rc)
					return;
				mateOrder.m_orderId = mateOD.orderId;
			}
			mateTickerId = mainFrame.nextValidTickerId++;
		}
		
		SecurityTableRow base = row.pair.getBaseRow();
		SecurityTableRow mate = row.pair.getMateRow();
		
		// Put a new row in the position table
		if (!mainFrame.positionPanel.model.appendOwnablePair(ownablePair, baseOrder, baseTickerId, mateOrder, mateTickerId, base.bid,base.last,base.ask,mate.bid,mate.last,mate.ask)) {
			JOptionPane.showMessageDialog(PairPanel.this, "Can't order specified pair: bad order id(s)?");
			return;
		}

		mainFrame.reqMktData(baseTickerId, base.contract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
		mainFrame.placeOrder(baseOrder.m_orderId, base.contract, baseOrder);

		if (ownablePair.mateTargQty != null && ownablePair.mateTargQty != 0  && mateTickerId != null && mateOrder != null) {
			mainFrame.reqMktData(mateTickerId, mate.contract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
			mainFrame.placeOrder(mateOrder.m_orderId, mate.contract, mateOrder);
		}
	}

	@Override
	public void onServerEvent(ServerEvent twsEvent) {
		if (twsEvent instanceof ValidIdEvent) {
			ValidIdEvent vie = (ValidIdEvent) twsEvent;
			mainFrame.nextValidOrderId = vie.nextValidOrderId;
		}
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		Preferences prefWidths  = pref.node("WIDTHS");
		int nCol = table.getColumnModel().getColumnCount();
		for (int i=0; i<nCol; i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			int w=col.getWidth();
			prefWidths.putInt(table.getColumnName(i),w);
		}
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
	}
	
	@Override
	public void setAppFont(Font font) {
		table.setRowHeight(font.getSize()+PreferencesDialog.getRowPadding());
		table.setFont(font);
		for (Fontible f : fontibles) {
			f.setAppFont(font);
		}
	}

	public void exportAllRows(FileWriter fileWriter) throws IOException {
		try (CSVWriter writer = new CSVWriter(fileWriter, ',')) {
			for (PairTableRow row : model.getRows()) {
				String[] entries = new String[] {row.pair.getMateRow().contract.m_symbol, Integer.toString(row.baseQuantity)};
				writer.writeNext(entries);
			}
		}
	}

	public void importCsv(FileReader fileReader) throws IOException {
		model.clear(); // TODO: confirm
		try (CSVReader reader = new CSVReader(fileReader)) {
	    List<String[]> entries = reader.readAll();		
			for (String[] entry : entries) {
				if (entry.length != 2) {
					JOptionPane.showMessageDialog(this, "file format not correct", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				SecurityTableRow secRow = mainFrame.securityPanel.model.findRow(entry[0]);
				SecurityTableRowPair pair = mainFrame.securityPanel.model.makePair(secRow);
				model.appendRow(pair,Integer.parseInt(entry[1]));
			}
			model.fireTableDataChanged();
		}
	}
}

package com.jtalics.pairstrader.trades;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import TestJavaClient.OrderDlg;

import com.ib.client.Order;
import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.MainFrame.ConnectedTo;
import com.jtalics.pairstrader.OrderDialog;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.PriceEditor;
import com.jtalics.pairstrader.PriceRenderer;
import com.jtalics.pairstrader.actions.ActionDeletePositionTableRow;
import com.jtalics.pairstrader.pairs.PairPanel;
import com.jtalics.pairstrader.trades.PositionTableModel.PositionTableModelEvent;

public class PositionPanel extends JPanel implements TableColumnModelListener, Fontible {

	public static Preferences pref=PreferencesDialog.pref.node("POS_TABLE");
	private final List<Fontible> fontibles = new ArrayList<>();

	public final MainFrame mainFrame;
	public final PositionTableModel model;
	public final JTable table;
	public final JPopupMenu popup = new JPopupMenu();
	public final CloseStateRenderer closeButtonRenderer = new CloseStateRenderer();
	public final boolean debug = false;
	private final OwnablePairRenderer namesRenderer = new OwnablePairRenderer(OwnablePairRenderer.DisplayMode.Names);
	private final OwnablePairRenderer quantitiesRenderer = new OwnablePairRenderer(OwnablePairRenderer.DisplayMode.Quantities);
	private final PriceRenderer stopLossRenderer = new PriceRenderer(false);
	private final PriceEditor stopLossEditor = new PriceEditor();
	private final PriceRenderer stopGainRenderer = new PriceRenderer(false);
	private final PriceEditor stopGainEditor = new PriceEditor();
	private final LinkedBlockingQueue<Runnable> closeQueue = new LinkedBlockingQueue<>();

	public PositionPanel(MainFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		model = new PositionTableModel(mainFrame) {
	    @Override
			public void fireTableChanged(TableModelEvent e) {
				// By default, selection is lost upon fireTableDataChanged
				int[] selectedRows = table.getSelectedRows();
				super.fireTableChanged(e);
				if (e.getFirstRow() == 0 && e.getLastRow() == Integer.MAX_VALUE && e.getColumn() == TableModelEvent.ALL_COLUMNS && e.getType() == TableModelEvent.UPDATE) {
					for (Integer i : selectedRows) {
						table.addRowSelectionInterval(i, i);
					}
				}
			}
		};

		table = new JTable(model) {
			
			@Override
			public void tableChanged(TableModelEvent e) { // model changed

				super.tableChanged(e);// to gain control of order of listeners
				if (TableModelEvent.HEADER_ROW == e.getFirstRow()) {
					if (table != null) {setCellRenderers();} // don't call during init
				}
				else if (e instanceof PositionTableModelEvent) {
					final PositionTableModelEvent ptme = (PositionTableModelEvent)e;
					switch (ptme.kind) {
					case PositionNeedsClosed:
						closeQueue.add(new Runnable() {
							@Override
							public void run() {
								close((PositionTableRow)ptme.details);
							}
						});
						break;
					case UnsubscribeTicker:
						PositionPanel.this.mainFrame.cancelMktData((Integer)ptme.details);
						break;
					case Connect:
						// Connect event received
						int baseTickerId = PositionPanel.this.mainFrame.nextValidTickerId++;
						int mateTickerId = PositionPanel.this.mainFrame.nextValidTickerId++;
						PositionTableRow row = (PositionTableRow)ptme.details;
						row.baseTickerId = baseTickerId;
						row.mateTickerId = mateTickerId;
						model.addTickerId(baseTickerId, row);
						model.addTickerId(mateTickerId, row);
						PositionPanel.this.mainFrame.reqMktData(baseTickerId, row.pair.baseContract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
						PositionPanel.this.mainFrame.reqMktData(mateTickerId, row.pair.mateContract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
						break;
					case Disconnect:
						new Exception("TODO: disconnect").printStackTrace();
						break;
					}
				}
			}


		};
		JScrollPane scrollPane = new JScrollPane(table);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		popup.setBorder(new BevelBorder(BevelBorder.RAISED));
		fontibles.add(namesRenderer);
		fontibles.add(quantitiesRenderer);
		fontibles.add(closeButtonRenderer);
		fontibles.add(stopLossRenderer);
		fontibles.add(stopLossEditor);
		fontibles.add(stopGainRenderer);
		fontibles.add(stopGainEditor);

		setCellRenderers();
		table.setToolTipText("This is the Trades table");		
		int nCol = table.getColumnCount();
		Preferences prefWidths  = pref.node("WIDTHS");
		for (int i=0; i<nCol; i++) {
			String name = table.getColumnName(i);
			TableColumn col = table.getColumnModel().getColumn(i);
			int w=prefWidths.getInt(name,col.getWidth());
			col.setPreferredWidth(w);
		}
		table.getColumnModel().addColumnModelListener(this);

		table.addMouseListener(buildMouseListener());

		mainFrame.addServerErrorListener(model);
		mainFrame.addMarketDataListener(model);
		mainFrame.addServerListener(model);
		
		new Thread("CLOSE QUEUE") {
			@Override
			public void run() {
				while (true) {
					try {
						closeQueue.take().run();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public synchronized void close(PositionTableRow row) {

		if (!mainFrame.isConnected()) {
			JOptionPane.showMessageDialog(this, "Please connect first.");
			return;
		}

		// If there are any (partially) unfilled orders remaining, then cancel them
		if (debug) mainFrame.println("CLOSING POSITION");
		if (row.baseOpeningOrderState.status != null
				&&!row.baseOpeningOrderState.status.equals("Inactive") 
				&& row.baseOpeningOrderState.remaining > 0) {
			mainFrame.cancelOrder(row.baseOpeningOrder.m_orderId);
		}
		if (row.mateOpeningOrder != null
				&& row.mateOpeningOrderState.status != null
				&& !row.mateOpeningOrderState.status.equals("Inactive") 
				&& row.mateOpeningOrderState.remaining > 0) {
			mainFrame.cancelOrder(row.mateOpeningOrder.m_orderId);
		}

		boolean showDialog = PreferencesDialog.getShowDialog();
		
		if (row.pair.baseActQty != 0) {
			closeBase(row, showDialog);
		}

		// 
		if (row.mateOpeningOrder != null && row.pair.mateActQty != 0.0) {
			closeMate(row, showDialog);
		}
		model.fireTableDataChanged();
	}
	
	private void closeBase(PositionTableRow row, boolean showDialog) {
		row.pair.baseTargQty = 0;
		// Sell or cover everything we have
		Order closeBaseOrder = new Order();
		if (row.pair.baseActQty > 0) {
			closeBaseOrder.m_action = "SELL";
		}
		else if (row.pair.baseActQty < 0) {
			closeBaseOrder.m_action = "BUY"; // buy to cover
		}

		closeBaseOrder.m_totalQuantity = Math.abs(row.pair.baseActQty);
		closeBaseOrder.m_orderId = mainFrame.nextValidOrderId++;
		closeBaseOrder.m_orderType = "MKT";
		closeBaseOrder.m_lmtPrice = 0.0;
		closeBaseOrder.m_auxPrice = 0.0;
		row.baseClosingOrder = closeBaseOrder;

		if (showDialog) {
			OrderDialog baseOD = new OrderDialog(mainFrame, closeBaseOrder, row.pair.baseContract);

			baseOD.setTitle("Specify BASE");
			Main.centerOnScreen(baseOD);
			baseOD.show();
			if (!baseOD.m_rc)
				return;
			closeBaseOrder.m_orderId = baseOD.orderId;

		}
		row.baseTickerId = mainFrame.nextValidTickerId++;
		model.addOrderId(row.baseClosingOrder.m_orderId, row);
		model.addTickerId(row.baseTickerId, row);
		// TODO: use OwnablePair, not SecurityTableRow to get base.contract next line
		mainFrame.reqMktData(row.baseTickerId, row.pair.baseContract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
		mainFrame.placeOrder(row.baseClosingOrder.m_orderId, row.pair.baseContract, row.baseClosingOrder);
	}

	private void closeMate(PositionTableRow row, boolean showDialog) {
		row.pair.mateTargQty = 0;
		Order closeMateOrder = new Order();
		// not thread safe
		if (row.pair.mateActQty > 0) {
			closeMateOrder.m_action = "SELL";
		}
		else if (row.pair.mateActQty < 0) {
			closeMateOrder.m_action = "BUY"; // cover our short
		}
		closeMateOrder.m_totalQuantity = Math.abs(row.pair.mateActQty);
		closeMateOrder.m_orderId = mainFrame.nextValidOrderId++;
		closeMateOrder.m_orderType = "MKT";
		closeMateOrder.m_lmtPrice = 0.0;
		closeMateOrder.m_auxPrice = 0.0;
		row.mateClosingOrder = closeMateOrder;
		if (showDialog) {
			OrderDialog mateOD = new OrderDialog(mainFrame, closeMateOrder, row.pair.mateContract);

			mateOD.setTitle("Specify MATE");
			Main.centerOnScreen(mateOD);
			mateOD.show();
			if (!mateOD.m_rc)
				return;
			closeMateOrder.m_orderId = mateOD.orderId;
		}
		row.mateTickerId = mainFrame.nextValidTickerId++;
		model.addOrderId(row.mateClosingOrder.m_orderId, row);
		model.addTickerId(row.mateTickerId, row);
		mainFrame.reqMktData(row.mateTickerId, row.pair.mateContract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
		mainFrame.placeOrder(closeMateOrder.m_orderId, row.pair.mateContract, row.mateClosingOrder);
	}

	public void setCellRenderers() { // renderers are cleared when table structure changes

		table.getColumnModel().getColumn(0).setCellRenderer(namesRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(quantitiesRenderer );
		table.getColumnModel().getColumn(5).setCellRenderer(closeButtonRenderer);

		if (PreferencesDialog.getAutoCloseMethod() == AutoCloseMethod.Stops) {
			table.getColumnModel().getColumn(6).setCellRenderer(stopLossRenderer);
			table.getColumnModel().getColumn(6).setCellEditor(stopLossEditor);
			table.getColumnModel().getColumn(7).setCellRenderer(stopGainRenderer);
			table.getColumnModel().getColumn(7).setCellEditor(stopGainEditor);
		}
		//table.getColumnModel().getColumn(5).setCellEditor(closeButtonEditor);
		//table.setDefaultRenderer(TableButton.class, closeButtonRenderer);
		//table.setDefaultEditor(TableButton.class, closeButtonEditor);
	}

	private MouseListener buildMouseListener() {
		return new MouseListener() {
			@Override
			public void mousePressed(MouseEvent ev) {
				checkPopup(ev);
			}

			@Override
			public void mouseClicked(MouseEvent ev) {
				handleMouseClick(ev);
			}

			private void handleMouseClick(MouseEvent ev) {
				 int rowIndex = table.rowAtPoint(ev.getPoint());			
				 int colIndex = table.columnAtPoint(ev.getPoint());
				 switch(colIndex) {
				 case 0:
				 case 1:
					 if (ev.getClickCount()==2) {
					   JOptionPane.showMessageDialog(table, new JLabel(namesRenderer.getToolTipText()), "Pair ordering information", JOptionPane.PLAIN_MESSAGE);
					 }
					 break;
				 case 5:
					 if (ev.getClickCount()==1) {
						 close(model.getRow(rowIndex));
					 }
					 break;
				 }
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			private void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					Point p = e.getPoint();
					popup.removeAll();
					JMenuItem item = new JMenuItem(new ActionDeletePositionTableRow(mainFrame, model, table.rowAtPoint(p)));
					item.setHorizontalTextPosition(JMenuItem.RIGHT);
					popup.add(item);

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
		};
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
		table.setFont(font);
		table.setRowHeight(font.getSize()+PreferencesDialog.getRowPadding());

		for (Fontible f : fontibles) {
			f.setAppFont(font);
		}
	}
}

package com.jtalics.pairstrader.stocks;

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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import TestJavaClient.OrderDlg;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.ib.client.TickType;
import com.ib.contracts.StkContract;
import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.PriceEditor;
import com.jtalics.pairstrader.PriceRenderer;
import com.jtalics.pairstrader.actions.ActionAppendRow;
import com.jtalics.pairstrader.actions.ActionCreatePair;
import com.jtalics.pairstrader.actions.ActionDeleteSecurityTableRow;
import com.jtalics.pairstrader.actions.ActionSetBaseSecurity;
import com.jtalics.pairstrader.stocks.SecurityTableModel.SecurityTableModelEvent;

public class SecurityPanel extends JPanel implements TableModelListener, TableColumnModelListener, Fontible {

	public static Preferences pref=PreferencesDialog.pref.node("SEC_TABLE");

	private final MainFrame mainFrame;
	public final SecurityTableModel model;
	public final JTable table;
	private final JPopupMenu popup = new JPopupMenu();
	protected boolean gettingValidId = false;
	private final List<Fontible> fontibles = new ArrayList<>();

	public SecurityPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		model = new SecurityTableModel(mainFrame) {
			@Override
			public void fireTableChanged(TableModelEvent e) {
				// By default, selection is lost upon fireTableDataChanged - let's restore it
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
	    public void addRowSelectionInterval(int index0, int index1) {
        selectionModel.addSelectionInterval(index0, index1);
			}

		};
		int nCol = table.getColumnCount();
		Preferences prefWidths  = pref.node("WIDTHS");
		for (int i=0; i<nCol; i++) {
			String name = table.getColumnName(i);
			TableColumn col = table.getColumnModel().getColumn(i);
			int w=prefWidths.getInt(name,col.getWidth());
			col.setPreferredWidth(w);
		}
		table.setToolTipText("This is the Stocks table");
		table.getColumnModel().addColumnModelListener(this);
		JScrollPane scrollPane = new JScrollPane(table);
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		popup.setBorder(new BevelBorder(BevelBorder.RAISED));

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
					// TODO: BUG - press on row, release off any row at end of table gives OOB error
					popup.removeAll();
					Point p = e.getPoint();
					int mateRowIndex = table.rowAtPoint(p);
					int baseRowIndex = model.getBaseRowIndex();
					if (baseRowIndex != -1) {
						SecurityTableRow mate = model.getRow(mateRowIndex);
						SecurityTableRowPair pair = SecurityPanel.this.model.makePair(mate);
						JMenuItem item = new JMenuItem(new ActionCreatePair(SecurityPanel.this.mainFrame, SecurityPanel.this.mainFrame.pairsPanel.model, pair));
						item.setHorizontalTextPosition(JMenuItem.RIGHT);
						popup.add(item);
					}
					popup.add(new JMenuItem(new ActionSetBaseSecurity(SecurityPanel.this.mainFrame, model, mateRowIndex)));
					popup.add(new JMenuItem(new ActionAppendRow(SecurityPanel.this.mainFrame)));
					popup.add(new JMenuItem(new ActionDeleteSecurityTableRow(SecurityPanel.this.mainFrame, model, mateRowIndex)));

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

		TableStringEditor tse = new TableStringEditor();
		table.getColumnModel().getColumn(0).setCellEditor(tse);
		fontibles.add(tse);
		SecurityRenderer sr = new SecurityRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(sr);
		fontibles.add(sr);
		
		PriceRenderer pr = new PriceRenderer(false);
		PriceEditor pe = new PriceEditor();
		table.getColumnModel().getColumn(1).setCellRenderer(pr);
		table.getColumnModel().getColumn(1).setCellEditor(pe);
		fontibles.add(pr);
		fontibles.add(pe);
		
		pr=new PriceRenderer(true);
		pe=new PriceEditor();
		table.getColumnModel().getColumn(2).setCellRenderer(pr);
		table.getColumnModel().getColumn(2).setCellEditor(pe);
		fontibles.add(pr);
		fontibles.add(pe);

		pr=new PriceRenderer(false);
		pe=new PriceEditor();
		table.getColumnModel().getColumn(3).setCellRenderer(pr);
		table.getColumnModel().getColumn(3).setCellEditor(pe);
		fontibles.add(pr);
		fontibles.add(pe);

		model.addTableModelListener(this);
		mainFrame.addServerErrorListener(model);
		mainFrame.addServerListener(model);
		mainFrame.addMarketDataListener(model);

		//populateWithTestData();
	}

	public void cancelAllMktData() {
		for (Integer tickerId : model.getTickerIds()) {
			mainFrame.cancelMktData(tickerId);
		}
	}

	@Override
	public void tableChanged(TableModelEvent ev) {
		// We manage the subscriptions
		if (ev instanceof SecurityTableModelEvent) {
			SecurityTableModelEvent event = (SecurityTableModelEvent) ev;
			SecurityTableRow row = (SecurityTableRow) event.details;
			switch (event.kind) {
			case UnsubscribeRow:
				Integer tickerId = model.getTickerId(row);
				if (tickerId != null) {
					mainFrame.cancelMktData(tickerId);
					model.removeTickerId(tickerId);
				}
				break;
			case SubscribeRow:
				tickerId = model.getTickerId(row);
				if (tickerId == null) {
					tickerId = mainFrame.nextValidTickerId++;
					model.addTickerId(tickerId, row);
				}
				mainFrame.reqMktData(tickerId, row.contract, OrderDlg.ALL_GENERIC_TICK_TAGS, false);
				break;
			case FireSyntheticBidEvent:
				mainFrame.fireTickPrice(row.contract, TickType.BID, row.bid);
				break;
			case FireSyntheticAskEvent:
				mainFrame.fireTickPrice(row.contract, TickType.ASK, row.ask);
				break;
			case FireSyntheticLastEvent:
				mainFrame.fireTickPrice(row.contract, TickType.LAST, row.last);
				break;
			case TickerMessage:
				mainFrame.pairsPanel.model.fireTableDataChanged();
				break;
			}
		}
	}
	
	public void populateWithTestData() {
		
		SecurityTableRow row = new SecurityTableRow(new StkContract("C"));
		model.addRow(row);
		//model.fireTableChanged(new SecurityTableModelEvent(model,SecurityTableModelEvent.Kind.SubscribeRow,row));
		
//		row = new SecurityTableRow(new StkContract("IBM"), 0.0);
//		model.addRow(row);
//
		row = new SecurityTableRow(new StkContract("GOOG"));
		model.addRow(row);
		//model.fireTableChanged(new SecurityTableModelEvent(model,SecurityTableModelEvent.Kind.SubscribeRow,row));

		model.setBaseSecurity(0);
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
			for (SecurityTableRow row : model.getRows()) {
				String[] entries = new String[] {row.contract.m_symbol, Double.toString(row.bid), Double.toString(row.last), Double.toString(row.ask), Boolean.toString(model.isBaseRow(row))};
				writer.writeNext(entries);
			}
		}
	}

	public void importCsv(FileReader fileReader) throws IOException {
		model.clear(); // TODO: confirm
		try (CSVReader reader = new CSVReader(fileReader)) {
	    List<String[]> entries = reader.readAll();		
			for (String[] entry : entries) {
				if (entry.length != 5) {
					JOptionPane.showMessageDialog(this, "file format not correct", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				model.addRow(new SecurityTableRow(new StkContract(entry[0]),Double.parseDouble(entry[1]),Double.parseDouble(entry[2]),Double.parseDouble(entry[3])));
				if (Boolean.parseBoolean(entry[4])) {
					model.setBaseSecurity(model.getRowCount()-1);
				}
			}
			model.fireTableDataChanged();
		}
	}


}

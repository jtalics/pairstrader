package com.jtalics.pairstrader.pairs;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.stocks.SecurityTableRowPair;

class PairRenderer extends DefaultTableCellRenderer implements Fontible {

	private final Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 0);
	private Font appFont;
	
	static public class PairWrapper {

		public final SecurityTableRowPair pair; 
		
		public PairWrapper(SecurityTableRowPair pair) {
			this.pair = pair;
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {

		PairWrapper pw = (PairWrapper) value;

		setOpaque(true);
		setText(pw.pair.toString());

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
		else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setBorder(padding);
		return this;
	}

	@Override
	public void setAppFont(Font font) {
		this.appFont  = font;
		setFont(font);
	}
}

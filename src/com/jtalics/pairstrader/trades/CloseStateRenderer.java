package com.jtalics.pairstrader.trades;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;

import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.MainFrame;

public class CloseStateRenderer extends DefaultTableCellRenderer implements Fontible {

	static public enum RowClosingState {
		Close, Closing, Closed
	}

	private final JToggleButton button = new JToggleButton();

	public CloseStateRenderer() {
    button.setMargin(new Insets(1,1,1,1));
    setHorizontalAlignment(RIGHT);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
		CloseStateRenderer.RowClosingState state = (CloseStateRenderer.RowClosingState) table.getModel().getValueAt(rowIndex, columnIndex);
		button.setText(state.name());
		switch (state) {
		case Close:
			button.setSelected(false);
			return button;
		case Closed:
			PositionTableRow row = ((PositionTableModel)table.getModel()).getRow(rowIndex);
			String s=MainFrame.df.format(row.net);
			setText(s);
			return this;
		case Closing:
			button.setSelected(true);
			return button;
		default: 
			throw new RuntimeException();
		}
	}

	@Override
	public void setAppFont(Font font) {
		button.setFont(font);
		setFont(font);
	}
}
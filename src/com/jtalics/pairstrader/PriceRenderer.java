package com.jtalics.pairstrader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import com.jtalics.pairstrader.util.Util;

public class PriceRenderer extends DefaultTableCellRenderer implements Fontible {

	private boolean bold;
	private Font appFont = PreferencesDialog.getAppFont();

	public PriceRenderer(boolean bold) {
		this.bold = bold;
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {
		PriceWrapper pw = (PriceWrapper) value;
		//System.out.println("row=" + rowIndex + " col=" + columnIndex + " pw.price=" + pw.toString());
		if (pw.drawBorder) {
			setBorder(BorderFactory.createLineBorder(Color.BLUE, Util.BaseBorderThickness));
		}
		else {
			setBorder(null);
		}

		if (pw.errorCode != 0) {
			switch (pw.errorCode) {
			default:
				setText("?" + pw.errorCode + "?");
				return this;
			}
		}

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
		else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		if (bold) {
			setText("<html><b>" + MainFrame.df.format(pw.price).toString() + "</b></html>");
		}
		else {
			setText(MainFrame.df.format(pw.price).toString());
		}
		if (pw.delta < 0) {
			setForeground(Color.RED);
		}
		else if (pw.delta > 0) {
			setForeground(Color.GREEN.darker());
		}
		

		return this;
	}

	@Override
	public void setAppFont(Font font) {
		this.appFont = font;
		setFont(font);
	}
}

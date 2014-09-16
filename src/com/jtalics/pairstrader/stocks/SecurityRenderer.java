package com.jtalics.pairstrader.stocks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import com.ib.client.Contract;
import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.util.Util;

public class SecurityRenderer extends DefaultTableCellRenderer implements Fontible {

	private final Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 0);
	private Font appFont = PreferencesDialog.getAppFont();
	
	static public class ContractWrapper {

		public final Contract contract;
		public final boolean drawBorder;
		
		public ContractWrapper(Contract contract, boolean drawBorder) {
			this.contract = contract;
			this.drawBorder = drawBorder;
		}
	}

	public SecurityRenderer() {
		super();
		setFont(appFont);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {

		setOpaque(true);
		
		ContractWrapper cw = (ContractWrapper)value;
		setText(cw.contract.m_symbol);
		Border border;
		
		if (cw.drawBorder) {
			border = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, Util.BaseBorderThickness), padding);
		}
		else {
			border = padding;
		}

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
		else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setBorder(border);
		return this;
	}

	@Override
	public void setAppFont(Font font) {
		this.appFont  = font;
		setFont(font);
	}

}

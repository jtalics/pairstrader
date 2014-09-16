package com.jtalics.pairstrader.stocks;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import com.jtalics.pairstrader.Fontible;

public class TableStringEditor extends AbstractCellEditor implements TableCellEditor, Fontible {

  JTextField editor = new JTextField();
	
	@Override
	public Object getCellEditorValue() {
		return editor.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editor.setText(((SecurityRenderer.ContractWrapper)value).contract.m_symbol);
		return editor;
	}
	
	@Override
	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) {
			int clickCount;
			clickCount = 2;
			return ((MouseEvent) evt).getClickCount() >= clickCount;
		}
		return true;
	}

	@Override
	public void setAppFont(Font font) {
		editor.setFont(font);
	}
}

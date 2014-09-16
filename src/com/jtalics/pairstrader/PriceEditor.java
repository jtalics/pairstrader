package com.jtalics.pairstrader;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class PriceEditor extends AbstractCellEditor implements TableCellEditor, Fontible {

	JTextField editor = new JTextField();
	

	@Override
	public Object getCellEditorValue() {
		return Double.parseDouble(editor.getText());
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editor.setText(Double.toString((((PriceWrapper)value).price)));
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

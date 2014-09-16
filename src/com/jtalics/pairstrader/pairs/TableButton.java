package com.jtalics.pairstrader.pairs;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.jtalics.pairstrader.Fontible;

class TableButton extends JButton implements TableCellRenderer, TableCellEditor, Fontible {

	static public interface TableButtonListener extends EventListener {
	  public void tableButtonClicked( int row, int col );
	}
	
	private int selectedRow;
  private int selectedColumn;
  List<TableButtonListener> listener;
	private Font appFont;
  
  public TableButton(String text) {
    super(text); 
    setMargin(new Insets(1,1,1,1));
    listener = new ArrayList<>();
    addActionListener(new ActionListener() { 
      @Override
    	public void actionPerformed( ActionEvent e ) { 
        for(TableButtonListener l : listener) { 
          l.tableButtonClicked(selectedRow, selectedColumn);
        }
      }
    });
  }

  public void addTableButtonListener( TableButtonListener l ) {
    listener.add(l);
  }

  public void removeTableButtonListener( TableButtonListener l ) { 
    listener.remove(l);
  }

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
		//Boolean enabled = (Boolean) table.getModel().getValueAt(rowIndex, colIndex);
		//System.out.println("RENDERER" + getText() + enabled+rowIndex);
		// setSelected(false);
		//if (!enabled) setText("diabled");//return new JLabel("closed");
		//else setText("Enabled");
		//editable=false;
		setFont(appFont);
		return this;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int colIndex) {
		//Boolean b = (Boolean) table.getModel().getValueAt(rowIndex, colIndex);
		//System.out.println("EDITOR" + getText() + b+rowIndex);
		//if (!b) setText("closed");
		//editable=false;
		// setSelected(false);
		selectedRow = rowIndex;
		selectedColumn = colIndex;
		setFont(appFont);
		return this;
	}

  @Override
  public void addCellEditorListener(CellEditorListener arg0) {      
  } 

  @Override
  public void cancelCellEditing() {
  } 

  @Override
  public Object getCellEditorValue() {
    return this;
  }

  @Override
  public boolean isCellEditable(EventObject ev) {
    return true;
  }

  @Override
  public void removeCellEditorListener(CellEditorListener arg0) {
  }

  @Override
  public boolean shouldSelectCell(EventObject arg0) {
    return true;
  }

  @Override
  public boolean stopCellEditing() {
    return true;
  }
  
	@Override
	public void setAppFont(Font font) {
		this.appFont  = font;
		setFont(font);
	}

}
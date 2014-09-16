package com.jtalics.pairstrader.stocks;

public class SecurityTableRowPair {

	private final SecurityTableModel model;
	private final SecurityTableRow mateRow;

	public SecurityTableRowPair(SecurityTableModel model, SecurityTableRow mateRow) {
		this.model = model;
		this.mateRow = mateRow;
	}
	
	public SecurityTableRow getBaseRow() {
		return model.getBaseRow();
	}
	
	@Override
	public String toString() {
		return "(" + model.getBaseRow().contract.m_symbol+"," + mateRow.contract.m_symbol+")";
	}

	public SecurityTableRow getMateRow() {
		return mateRow;
	}
}

package com.jtalics.pairstrader.trades;

import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.ib.controller.OrderStatus;
import com.jtalics.pairstrader.Fontible;
import com.jtalics.pairstrader.MainFrame;

public class OwnablePairRenderer extends DefaultTableCellRenderer implements Fontible {

	static public class Wrapper {

		public final PositionTableRow row;

		public Wrapper(PositionTableRow row) {
			this.row=row;
		}
	}

	public enum DisplayMode {
		Names, Quantities, NamesAndQuantities
	}

	public final DisplayMode mode;
	private Font appFont;

	public OwnablePairRenderer(DisplayMode mode) {
		this.mode = mode;
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex) {

		Wrapper opw = (Wrapper) value;

		setOpaque(true);
		switch (mode) {
		case Names:
			if (isSelected) {
				setText("("+opw.row.pair.baseContract.m_symbol+","+opw.row.pair.mateContract.m_symbol+")");
			}
			else {
				String baseColor = determineColor(opw.row.baseOpeningOrderState.status, opw.row.baseClosingOrderState.status);
				String mateColor = determineColor(opw.row.mateOpeningOrderState.status, opw.row.mateClosingOrderState.status);
				setText("<html><b>(<font color=" + baseColor + ">" + opw.row.pair.baseContract.m_symbol + "</font>,<font color=" + mateColor + ">" + opw.row.pair.mateContract.m_symbol + "</font>)</b></html>");
			}
			break;
		case NamesAndQuantities:
			setText(opw.row.pair.toString()); // TODO: fix toString()
			break;
		case Quantities:
			setText(opw.row.pair.getQuantitiesString());
			break;
		}

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
		else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		
		setToolTipText(buildToolTipText(opw));
		return this;
	}

	private static String buildToolTipText(Wrapper opw) {
		
		StringBuilder sb=new StringBuilder("<html><table cellpadding=1 cellspacing=1>");
		sb.append(buildheaderRow());
		sb.append(buildSymbolRow(opw));
		
		sb.append(buildOpeningHeaderRow());
		sb.append(buildOpeningStatusRow(opw));
		sb.append(buildOpeningTypeRow(opw));
		sb.append(buildOpeningActionRow(opw));
		sb.append(buildOpeningQuantityRow(opw));
		
		sb.append(buildClosingHeaderRow());
		sb.append(buildClosingStatusRow(opw));
		sb.append(buildClosingTypeRow(opw));
		sb.append(buildClosingActionRow(opw));
		sb.append(buildClosingQuantityRow(opw));

		sb.append("</table></html>"); 
		String s=sb.toString();
		return s;
	}

	private static Object buildClosingTypeRow(Wrapper opw) {

		String baseOrderType="";
		if (opw.row.baseClosingOrder != null) {
			baseOrderType=opw.row.baseClosingOrder.m_orderType;
		}
		String mateOrderType="";
		if (opw.row.mateClosingOrder != null) {
			mateOrderType=opw.row.mateClosingOrder.m_orderType;
		}
		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td><b>Type<b></td>");
		sb.append("<td>"+baseOrderType+"</td>");
		sb.append("<td>"+mateOrderType+"</td>");
		sb.append("</tr>");

		String baseLmtPrc="";
		if (opw.row.baseClosingOrder != null && opw.row.baseClosingOrder.m_orderType.equals("LMT")) {
			baseLmtPrc=MainFrame.df.format(opw.row.baseClosingOrder.m_lmtPrice);
		}
			
		String mateLmtPrc="";
		if (opw.row.mateClosingOrder != null && opw.row.mateClosingOrder.m_orderType.equals("LMT")) {
			mateLmtPrc=MainFrame.df.format(opw.row.mateClosingOrder.m_lmtPrice);
		}
		
		if (!baseLmtPrc.isEmpty() || !mateLmtPrc.isEmpty()) {
			sb.append("<tr>");
			sb.append("<td><b>LmtPrc<b></td>");
			sb.append("<td>"+baseLmtPrc+"</td>");
			sb.append("<td>"+mateLmtPrc+"</td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}

	public static String buildOpeningTypeRow(Wrapper opw){

		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td><b>Type<b></td>");
		sb.append("<td>"+opw.row.baseOpeningOrder.m_orderType+"</td>");
		if (opw.row.mateOpeningOrder != null) sb.append("<td>"+opw.row.mateOpeningOrder.m_orderType+"</td>");
		sb.append("</tr>");

		String baseLmtPrc="";
		if (opw.row.baseOpeningOrder.m_orderType.equals("LMT")) {
			baseLmtPrc=MainFrame.df.format(opw.row.baseOpeningOrder.m_lmtPrice);
		}
			
		String mateLmtPrc="";
		if (opw.row.mateOpeningOrder != null && opw.row.mateOpeningOrder.m_orderType.equals("LMT")) {
			mateLmtPrc=MainFrame.df.format(opw.row.mateOpeningOrder.m_lmtPrice);
		}
		
		if (!baseLmtPrc.isEmpty() || !mateLmtPrc.isEmpty()) {
			sb.append("<tr>");
			sb.append("<td><b>LmtPrc<b></td>");
			sb.append("<td>"+baseLmtPrc+"</td>");
			sb.append("<td>"+mateLmtPrc+"</td>");
			sb.append("</tr>");
		}
		return sb.toString();
	}
	
	private static Object buildClosingQuantityRow(Wrapper opw) {

		String baseQty = "";
		if (opw.row.baseClosingOrder != null) {
			baseQty = Integer.toString(opw.row.baseClosingOrder.m_totalQuantity);
		}

		String mateQty = "";
		if (opw.row.mateClosingOrder != null) {
			mateQty = Integer.toString(opw.row.mateClosingOrder.m_totalQuantity);
		}

		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td><b>Qty<b></td>");
		sb.append("<td>" + baseQty + "</td>");
		sb.append("<td>" + mateQty + "</td>");
		sb.append("</tr>");

		return sb.toString();
	}

	private static Object buildOpeningQuantityRow(Wrapper opw) {

		String baseQty = Integer.toString(opw.row.baseOpeningOrder.m_totalQuantity);

		StringBuilder sb = new StringBuilder("<tr>");
			sb.append("<td><b>Qty<b></td>");
			sb.append("<td>" + baseQty + "</td>");
			if (opw.row.mateOpeningOrder != null) {
				String mateQty = Integer.toString(opw.row.mateOpeningOrder.m_totalQuantity);
				sb.append("<td>" + mateQty + "</td>");
			}
			sb.append("</tr>");
		return sb.toString();
	}

	private static String buildClosingStatusRow(Wrapper opw) {

		String mateClosingOrderStateStatus="";
		if (opw.row.mateClosingOrderState.status != null) {
			mateClosingOrderStateStatus=opw.row.mateClosingOrderState.status;
		}
		String baseClosingOrderStateStatus="";
		if (opw.row.baseClosingOrderState.status != null) {
			baseClosingOrderStateStatus=opw.row.baseClosingOrderState.status;
		}
		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td><b>Status<b></td>");
		sb.append("<td>"+baseClosingOrderStateStatus+"</td>");
		sb.append("<td>"+mateClosingOrderStateStatus+"</td>");
		sb.append("</tr>");
		return sb.toString();
	}

	private static String buildClosingActionRow(Wrapper opw) {
		
		String baseAction="";
		if (opw.row.baseClosingOrder != null) {
			baseAction=opw.row.baseClosingOrder.m_action;
		}
			
		String mateAction="";
		if (opw.row.mateClosingOrder != null) {
			mateAction=opw.row.mateClosingOrder.m_action;
		}
		
		StringBuilder sb =new StringBuilder("<tr>");
		sb.append("<td><b>Action<b></td>");
		sb.append("<td>"+baseAction+"</td>");
		sb.append("<td>"+mateAction+"</td>");
		sb.append("</tr>");

		return sb.toString();
	}

	private static String buildClosingHeaderRow() {

		return "<tr>"
		+ "<td align=center colspan=3><b>--Closing order --<b></td>"
		+ "</tr>";
	}

	private static String buildOpeningStatusRow(Wrapper opw) {
		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td><b>Status<b></td>");
		sb.append("<td>"+opw.row.baseOpeningOrderState.status+"</td>");
		if (opw.row.mateOpeningOrder != null) sb.append("<td>"+opw.row.mateOpeningOrderState.status+"</td>");
		sb.append("</tr>");
		return sb.toString();
	}

	private static String buildOpeningActionRow(Wrapper opw) {
		StringBuilder sb =new StringBuilder("<tr>");
		sb.append("<td><b>Action<b></td>");
		sb.append("<td>"+opw.row.baseOpeningOrder.m_action+"</td>");
		if (opw.row.mateOpeningOrder != null) sb.append("<td>"+opw.row.mateOpeningOrder.m_action+"</td>");
		sb.append("</tr>");

		return sb.toString();
	}

	private static String buildOpeningHeaderRow() {
		return "<tr>"
				+ "<td align=center colspan=3><b>--Opening order --<b></td>"
				+ "</tr>";
	}

	private static String buildSymbolRow(Wrapper opw) {
		return "<tr>"
				+ "<td><b>Symbol<b></td>"
				+ "<td>"+opw.row.pair.baseContract.m_symbol+"</td>"
				+ "<td>"+opw.row.pair.mateContract.m_symbol+"</td>"
				+ "</tr>";
	}

	private static String buildheaderRow() {
		return "<tr>"
				+ "<td></td>"
				+ "<td><b>BASE</b></td>"
				+ "<td><b>MATE</b></td>"
				+ "</tr>";
	}

	private String determineColor(String openingStatus, String closingStatus) {

		// Here is the color precedence, hi to lo: Red, Orange, Blue, Green
		// The highest color wins on competition between opening and closing
		// This will cause the user to use the tooltip to learn more about problems.
		String color[] = new String[] {"red","orange","blue","green"};
		
		int openingColor=Integer.MAX_VALUE; // lowest priority
		
		if (openingStatus != null) switch (OrderStatus.valueOf(openingStatus)) {
		case Inactive:
			openingColor = 0;
			break;
		case PendingSubmit:
			openingColor = 1;
			break;
		case Submitted:
			openingColor = 2;
			break;
		case Cancelled:
		case Filled:
			openingColor = 3;
			break;
		case PreSubmitted:
		case ApiCancelled:
		case ApiPending:
		case PendingCancel:
		case Unknown:
		default:
			openingColor=0;
			new Exception("TODO: "+openingStatus).printStackTrace();;
		}

		int closingColor=Integer.MAX_VALUE;		
		if (closingStatus != null) switch (OrderStatus.valueOf(closingStatus)) {
		case Inactive:
			closingColor = 0;
			break;
		case PendingSubmit:
			closingColor = 1;
			break;
		case Submitted:
			closingColor = 2;
			break;
		case Cancelled:
		case Filled:
			closingColor = 3;
			break;
		case PreSubmitted:
		case ApiCancelled:
		case ApiPending:
		case PendingCancel:
		case Unknown:
		default:
			closingColor=0;
			new Exception("TODO: "+closingStatus).printStackTrace();;
		}
		//System.out.println(openingColor+":"+closingColor);
		if (openingColor==Integer.MAX_VALUE && closingColor==Integer.MAX_VALUE) {
			return "black";
		}
		return color[Math.min(openingColor, closingColor)];
	}
	
	@Override
	public void setAppFont(Font font) {
		this.appFont  = font;
		setFont(font);
	}

}

package com.jtalics.pairstrader.server;

import com.ib.client.Order;
import com.ib.client.OrderState;
import com.jtalics.pairstrader.MainFrame;

public final class SellOrder implements Comparable<SellOrder> {

	public final Order order;
	public final OrderState orderState;
	public int filled;
	public int remaining;
	public double avgFillPrice;
	public int permId;
	public int parentId;
	public double lastFillPrice;
	public int clientId;
	public String whyHeld;
	
	public SellOrder(Order order, OrderState orderState) {
		this.order = order;
		this.orderState = orderState;
		filled=0;
		remaining=order.m_totalQuantity;
		avgFillPrice=0.0;
		permId=Integer.MIN_VALUE;
		parentId=Integer.MIN_VALUE;
		lastFillPrice=Double.NaN;
		clientId=Integer.MIN_VALUE;
		whyHeld=null; // "Can't find security to short"
	}

	@Override
	public int compareTo(SellOrder so) {
		if (this.order.m_orderType.equals("MKT")) {
			if (so.order.m_orderType.equals("LMT")) {
				return Integer.MIN_VALUE; // put on top of book
			}
			else if (so.order.m_orderType.equals("MKT"))  {
				return 0; // TODO: check timestamp so FIFO and fair
			}
			else {
				new Exception().printStackTrace();
			}
		}
		if (this.order.m_lmtPrice == so.order.m_lmtPrice) {
			return 0; // TODO: check timestamp so FIFO and fair
		}
		return this.order.m_lmtPrice < so.order.m_lmtPrice ? -1 : 1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (order.m_orderType.equals("LMT")) {
			sb.append("SELL:"+remaining+"/"+order.m_totalQuantity+"@"+MainFrame.df.format(order.m_lmtPrice));
		}
		else if (order.m_orderType.equals("MKT")) {
			sb.append("SELL:"+remaining+"/"+order.m_totalQuantity+"@MKT");			
		}
		else {
			new Exception("unhandled order type").printStackTrace();
		}
		return sb.toString();
	}
}
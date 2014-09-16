package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class OrderStatusEvent extends ServerListener.ServerEvent {

	public final int orderId;
	public final String status;
	public final int filled;
	public final int remaining;
	public final double avgFillPrice;
	public final int permId;
	public final int parentId;
	public final double lastFillPrice;
	public final int clientId;
	public final String whyHeld;		

	public OrderStatusEvent(Object source, int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		super(source);
		this.orderId = orderId;
		this.status = status;
		this.filled = filled;
		this.remaining=remaining;
		this.avgFillPrice = avgFillPrice;
		this.permId = permId;
		this.parentId = parentId;
		this.lastFillPrice = lastFillPrice;
		this.clientId = clientId;
		this.whyHeld = whyHeld;		
	}
	
	@Override
	public String toString() {
				return "[orderId=" + orderId
				+ ";avgFillPrice=" + avgFillPrice
				+ ";clientId=" + clientId
				+ ";filled=" + filled
				+ ";lastFillPrice=" + lastFillPrice
				+ ";parentId=" + parentId
				+ ";permId=" + permId
				+ ";remaining=" + remaining
				+ ";status=" + status
				+ ";whyHeld=" + whyHeld
				+"]";	
	}
}

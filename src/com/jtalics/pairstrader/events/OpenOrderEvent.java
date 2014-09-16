package com.jtalics.pairstrader.events;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class OpenOrderEvent extends ServerListener.ServerEvent {

	public final int orderId;
	public final Contract contract;
	public final Order order;
	public final OrderState orderState;
	
	public OpenOrderEvent(Object source, int orderId, Contract contract, Order order, OrderState orderState) {
		super(source);
		this.orderId = orderId;
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
	}
	
	public String toString() {
		String s =  
				"[orderId="+orderId
				+";contract="+contract.toString();
		
				if (order == null) {
					s+=";order=null";
				}
				else {
					s+=";order="+order.toString();
				}

				s+=";orderState="+orderState.toString()
				+"]";
				
				return s;
	}
}

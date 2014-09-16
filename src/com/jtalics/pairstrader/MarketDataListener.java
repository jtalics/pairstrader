package com.jtalics.pairstrader;

import java.util.EventObject;

public interface MarketDataListener {

public class MdPriceTickEvent extends EventObject {

	public final int tickerId;
	public final int field;
	public final Double price;
	public final int canAutoExecute;

	public MdPriceTickEvent(Object source, int tickerId, int field, Double price, int canAutoExecute) { // Interactive Broker Market data
		super(source);
		this.tickerId = tickerId;
		this.field = field; // see class TickType
		this.price = price;
		this.canAutoExecute = canAutoExecute;
	}

}

	void onMdPriceTickEvent(MdPriceTickEvent ee);
}

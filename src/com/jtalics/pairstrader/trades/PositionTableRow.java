package com.jtalics.pairstrader.trades;

import com.ib.client.Order;
import com.jtalics.pairstrader.PreferencesDialog;
import com.jtalics.pairstrader.events.OrderStatusEvent;
import com.jtalics.pairstrader.util.Util;

public class PositionTableRow {

	static public class OrderState {
		public String status;
		public int filled;
		public int remaining;
		public double avgFillPrice;
		public int permId;
		public int parentId;
		public double lastFillPrice;
		public int clientId;
		public String whyHeld;

		public void setState(OrderStatusEvent ose) {
			this.status = ose.status;
			this.filled = ose.filled;
			this.remaining = ose.remaining;
			this.avgFillPrice = ose.avgFillPrice;
			this.permId = ose.permId;
			this.parentId = ose.parentId;
			this.lastFillPrice = ose.lastFillPrice;
			this.clientId = ose.clientId;
			this.whyHeld = ose.whyHeld;
		}

		@Override
		public String toString() {
			String s = "[status=" + status + "; filled=" + filled + "; remaining=" + remaining + "; avgFillPrice=" + avgFillPrice + "; permId=" + permId + "; parentId=" + parentId + "; lastFillPrice=" + lastFillPrice + "; clientId=" + clientId + "; whyHeld=" + whyHeld + "]";
			return s;
		}
	}

	public final OwnablePair pair;
	
	public final Order baseOpeningOrder; // final keyword is important!
	public final OrderState baseOpeningOrderState = new OrderState();
	public Order baseClosingOrder; // null until closed
	public final OrderState baseClosingOrderState = new OrderState();
	public int baseTickerId;
	private double baseBid=Double.NaN;
	private double baseLast=Double.NaN;
	private double baseAsk=Double.NaN;

	public final Order mateOpeningOrder; // final keyword is important! can be null if mateQty can't be calc'd or is zero
	public final OrderState mateOpeningOrderState = new OrderState();
	public Order mateClosingOrder=null; // null until closed
	public final OrderState mateClosingOrderState = new OrderState();
	public Integer mateTickerId; // can be null if mateQty can't be calc'd or is zero 
	private double mateBid=Double.NaN;
	private double mateLast=Double.NaN;
	private double mateAsk=Double.NaN;

	double net=0.0;
	
	public double stopLoss = PreferencesDialog.getDefaultStopsLose();
	public double stopGain = PreferencesDialog.getDefaultStopsGain();
		
	public PositionTableRow(OwnablePair pair, Order baseOpeningOrder, int baseTickerId, Order mateOpeningOrder, Integer mateTickerId,
			double baseBid, double baseLast, double baseAsk, double mateBid, double mateLast, double mateAsk) {
		this.pair = pair;
		this.baseOpeningOrder = baseOpeningOrder;
		this.baseTickerId = baseTickerId;
		this.mateOpeningOrder = mateOpeningOrder;
		this.mateTickerId = mateTickerId;
		this.baseOpeningOrderState.filled=0;
		this.baseClosingOrderState.filled=0;
		this.mateOpeningOrderState.filled=0;
		this.mateClosingOrderState.filled=0;
		this.baseBid = baseBid; 
		this.baseLast = baseLast; 
		this.baseAsk = baseAsk;
		this.mateBid = mateBid; 
		this.mateLast = mateLast; 
		this.mateAsk = mateAsk;
	}

	public void setState(int orderId, OrderStatusEvent ose) {

		if (orderId == baseOpeningOrder.m_orderId) {
			baseOpeningOrderState.setState(ose);
		}
		else if (baseClosingOrder != null && orderId == baseClosingOrder.m_orderId) {
			baseClosingOrderState.setState(ose);
		}
		else if (orderId == mateOpeningOrder.m_orderId) {
			mateOpeningOrderState.setState(ose);
		}
		else if (mateOpeningOrder != null && orderId == mateClosingOrder.m_orderId) {
			mateClosingOrderState.setState(ose);
		}
		else {
			new Exception("Order id not found: " + orderId).printStackTrace();
		}

		// Sum orders to get the actual prices
		pair.baseActQty = 0;
		if (baseOpeningOrder.m_action.equals("SELL")) {
			pair.baseActQty -= baseOpeningOrderState.filled;
		}
		else if (baseOpeningOrder.m_action.equals("BUY")) {
			pair.baseActQty += baseOpeningOrderState.filled;
		}
		else {
			new Exception("Unhandled action: " + baseOpeningOrder.m_action).printStackTrace();
		}

		if (baseClosingOrder != null) {
			if (baseClosingOrder.m_action.equals("SELL")) {
				pair.baseActQty -= baseClosingOrderState.filled;
			}
			else if (baseClosingOrder.m_action.equals("BUY")) {
				pair.baseActQty += baseClosingOrderState.filled;
			}
			else {
				new Exception("Unhandled action: " + baseClosingOrder.m_action).printStackTrace();
			}
		}

		pair.mateActQty = 0;
		if (mateOpeningOrder != null) {
			if (mateOpeningOrder.m_action.equals("SELL")) {
				pair.mateActQty -= mateOpeningOrderState.filled;
			}
			else if (mateOpeningOrder.m_action.equals("BUY")) {
				pair.mateActQty += mateOpeningOrderState.filled;
			}
			else {
				new Exception("Unhandled action: " + mateOpeningOrder.m_action).printStackTrace();
			}
		}

		if (mateClosingOrder != null) {
			if (mateClosingOrder.m_action.equals("SELL")) {
				pair.mateActQty -= mateClosingOrderState.filled;
			}
			else if (mateClosingOrder.m_action.equals("BUY")) {
				pair.mateActQty += mateClosingOrderState.filled;
			}
			else {
				new Exception("Unhandled action: " + mateClosingOrder.m_action).printStackTrace();
			}
		}
		calcNet();
	}

	public double getBid() {
		// To calculate BID: A buyer wants to buy the pair. But how much? 
		// The bid is the price the buyer will pay for the pair.
		// The buyer will pay no more that
		// what buyer would pay to build the pair individually from the open market.
		// Buyer needs a short base and a long mate to build the pair.
		// To get a short base, buyer needs to sell the base at its ask to another buyer.
		// To get a long mate, buyer needs to buy the mate at its bid from another seller.
		double basePrice = baseAsk;
		double matePrice = mateBid;
		double baseActQty = -(Math.abs(pair.baseActQty));
		double mateActQty = Math.abs(pair.mateActQty);
		double retval = baseActQty * basePrice + mateActQty * matePrice;
		return Util.round(retval,2);
	}

	public Double getLast() {
		double baseActQty = -(Math.abs(pair.baseActQty));
		double mateActQty = Math.abs(pair.mateActQty);
		double retval = baseActQty * baseLast + mateActQty * mateLast;
		return Util.round(retval,2);
	}

	public double getAsk() {
		// To calculate ASK: A seller wants to sell the pair.  But how much?
		// The ask is the price the buyer will take for the pair.
		// The seller will take no more that what the seller  
		// would take to build the pair individually from legs on the open market.
		// Seller needs a long base and a short mate to build the pair.
		// To get a long base, seller needs to buy the base at its bid from another seller.
		// To get a short mate, seller needs to sell the mate at its ask from another buyer.
		double basePrice = baseBid;
		double matePrice = mateAsk;
		double baseActQty = -(Math.abs(pair.baseActQty));
		double mateActQty = Math.abs(pair.mateActQty);
		double retval=baseActQty * basePrice + mateActQty * matePrice;
		return Util.round(retval,2);
	}

	public void calcNet() {
		
		double b0=0.0,b1=0.0,m0=0.0,m1=0.0;
		if (baseOpeningOrder!=null) b0=calcOrderNet(baseOpeningOrderState,baseOpeningOrder.m_action);
		if (baseClosingOrder!=null) b1=calcOrderNet(baseClosingOrderState,baseClosingOrder.m_action);
		if (mateOpeningOrder != null) m0=calcOrderNet(mateOpeningOrderState,mateOpeningOrder.m_action);
		if (mateClosingOrder != null) m1=calcOrderNet(mateClosingOrderState,mateClosingOrder.m_action);

//		System.out.println("b0="+b0+" b1="+b1+" m0="+m0+" m1="+m1);
		net = Util.round(b0+b1+m0+m1,2);
	}

	private static double calcOrderNet(OrderState orderState, String action) {
		double net = 0.0;
//System.out.println(orderState.avgFillPrice+":"+orderState.filled);
		net = orderState.avgFillPrice * orderState.filled;
		if (action.equals("BUY")) {
			net = -net;
		}
		return net;
	}

	public void setBaseBid(double baseBid) {
		this.baseBid = baseBid;
	}

	public void setBaseLast(double baseLast) {
		this.baseLast = baseLast;
	}

	public void setBaseAsk(double baseAsk) {
		this.baseAsk = baseAsk;
	}

	public void setMateBid(double mateBid) {
		this.mateBid = mateBid;
	}

	public void setMateLast(double mateLast) {
		this.mateLast = mateLast;
	}

	public void setMateAsk(double mateAsk) {
		this.mateAsk = mateAsk;
	}
}
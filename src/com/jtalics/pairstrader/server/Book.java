/* TODO
Stop or stop-loss order becomes a market order when a
given price is reached by the market on the downside.
This enables an investor to minimize their losses in a
market reversal, but does not guarantee them the given
price.
Market-if-Touched order (MIT) becomes a market order
when a given price is reached by the market on the upside.
This enables an investor to take profits when they are
available, but does not guarantee them the given price.
*/
package com.jtalics.pairstrader.server;

import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.TickType;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.Outputable;
import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;
import com.jtalics.pairstrader.events.OpenOrderEvent;
import com.jtalics.pairstrader.events.OrderStatusEvent;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class Book {
	
	public final PriorityQueue<BuyOrder> buySide = new PriorityQueue<>();
	public final PriorityQueue<SellOrder> sellSide = new PriorityQueue<>();
	public final Contract contract;
	public Double bid=null;
	public Double last=null;
	public Double ask=null;

	private final BlockingQueue<ServerEvent> twsQueue;
	private final BlockingQueue<MdPriceTickEvent> mdQueue;
	private final BlockingQueue<ErrorEvent> errQueue;
	private final Outputable out;

	public Book(BlockingQueue<ServerEvent> twsQueue, BlockingQueue<MdPriceTickEvent> mdQueue, BlockingQueue<ErrorEvent> errQueue, Contract contract, Outputable out) {
		this.twsQueue = twsQueue;
		this.mdQueue = mdQueue;
		this.errQueue = errQueue;
		this.contract = contract;
		this.out = out;
		if (debug>Integer.MAX_VALUE) out.println(a()+"Initializing, hashCode="+hashCode());
	}
	
	int DEBUG_BLA=3;
	int DEBUG_FILL=2;
	int DEBUG_MATCH=1;
	int DEBUG_ADD=0;
	int debug = -1;//1+DEBUG_FILL;
	
	public void placeOrder(Order order) {
		placeOrder(order, true);
	}
	
	public void placeOrder(Order order, boolean doMatch) {
		if (invalidOrder(order)) {
			new Exception("invalid order: "+order.toString()).printStackTrace();
		}
		

		synchronized (this) {
			OrderState orderState = new OrderState();
			orderState.m_status="Submitted";
			switch (order.m_action) {
			case "BUY":
				BuyOrder buyOrder = new BuyOrder(order,orderState);
				buySide.add(buyOrder);
				try {
					if (debug>DEBUG_ADD) {
						out.println(a()+"***Added to BUY side: orderId="+order.m_orderId+","+buyOrder);
						out.println(a() + "Depths (buy,sell)=(" + buySide.size() + "," + sellSide.size() + ")");
					}
					twsQueue.put(new OpenOrderEvent(this, order.m_orderId, contract, order, orderState));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "SELL":
				SellOrder sellOrder = new SellOrder(order,orderState);
				sellSide.add(sellOrder);
				try {
					if (debug>DEBUG_ADD) {
						out.println(a()+"***Added to SELL side: order id="+order.m_orderId+","+sellOrder);
						out.println(a() + "Depths (buy,sell)=(" + buySide.size() + "," + sellSide.size() + ")");
					}
					twsQueue.put(new OpenOrderEvent(this, order.m_orderId, contract, order, orderState));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			default:
				String s=a()+"Unhandled action: " + order.m_action;
				try {
					errQueue.put(new ErrorEvent(this, 0, 0, s));
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				new Exception().printStackTrace();
			}
		}
		if (doMatch) {
			if (debug>Integer.MAX_VALUE) dump();
			// example where we wouldn't do a match is loading a book.
			match();
		}
	}

	private static boolean invalidOrder(Order order) {
		if (order.m_totalQuantity <= 0) {
			return true;
		}
		// TODO: add more checks
		return false;
	}


	void match() {
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				synchronized (this) {
					boolean fillHappened;
					if (debug>DEBUG_MATCH) {
						out.println(a() + "Start matching! ----------------");
					}
					do {
						fillHappened=false;
						if (debug>DEBUG_MATCH) {
							out.println(a() + "Begin match iter, depths (buy,sell)=(" 
									+ buySide.size() + "," + sellSide.size() + ")");
						}
						if (buySide.size() <= 0 || sellSide.size() <= 0) {
							if (debug>DEBUG_MATCH) {
								out.println(a() + "Cannot match when one or both sides are empty.");
							}
						}
						else {
							BuyOrder topBuy = buySide.peek();
							SellOrder topSell = sellSide.peek();
							if (debug>DEBUG_MATCH) {
								out.println(a() + "Matching: (buy,sell)=("+topBuy.toString()+","+topSell.toString()+")");
							}
							switch (topBuy.order.m_orderType) {
							case "MKT":
								if (topSell.order.m_orderType.equals("MKT")) {
									if (debug>DEBUG_MATCH) {
										out.println(a() + "Found match: (buy,sell)=(MKT,MKT)");
									}
									fillHappened=fill(last);
								}
								else if (topSell.order.m_orderType.equals("LMT")) {
									if (debug>DEBUG_MATCH) {
										out.println(a() + "Found match: (buy,sell)=(MKT,LMT)");
									}
									fillHappened=fill(topSell.order.m_lmtPrice);
								}
								else {
									unhandledAction(topSell.order.m_action);
								}
								break;
							case "LMT":
								if (topSell.order.m_orderType.equals("MKT")) {
									if (debug>DEBUG_MATCH) {
										out.println(a() + "Found match: (buy,sell)=(LMT,MKT)");
									}
									fillHappened=fill(topBuy.order.m_lmtPrice);
								}
								else if (topSell.order.m_orderType.equals("LMT")) {
									if (topSell.order.m_lmtPrice <= topBuy.order.m_lmtPrice) {
										if (debug>DEBUG_MATCH) {
											out.println(a() + "Found match: (buy,sell)=(LMT>=LMT)");
										}
										fillHappened=fill(topBuy.order.m_lmtPrice);
									}
									else {
										if (debug>DEBUG_MATCH) {
											out.println(a() + "No match: topBuy LMT < topSell LMT.");
										}
									}
								}
								else {
									unhandledAction(topSell.order.m_action);
								}
								break;
							default:
								unhandledAction(topBuy.order.m_action);
							}
						}
						if (debug>DEBUG_MATCH) {
							out.println(a() + "End match iter, depths (buy,sell)=(" + buySide.size() + "," + sellSide.size() + ")");
						}
					} while (fillHappened);
					if (debug>DEBUG_MATCH)
						out.println(a() + "Stop matching! -----------------");
				}
			}
		};

		boolean multi = false;
		if (multi) {
			Thread thread = new Thread(runnable);
			thread.setName("MATCH AND FILL");
		}
		else {
			runnable.run();
		}
	}

	void fireBookDataChanged() {
		try {
			MdPriceTickEvent e=new MdPriceTickEvent(contract, -1, TickType.BID, bid, 0);
			mdQueue.put(e);
			mdQueue.put(new MdPriceTickEvent(contract, -1, TickType.LAST, last, 0));
			mdQueue.put(new MdPriceTickEvent(contract, -1, TickType.ASK, ask, 0));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void unhandledAction(String action) {
		
		String s = a() + "Unhandled action: " + action;
		try {
			errQueue.put(new ErrorEvent(this, 0, 0, s));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Exception(s).printStackTrace();				
	}
	
	private boolean fill(Double price) {

		boolean fillHappened=false;
		BuyOrder topBuy = buySide.peek();
		SellOrder topSell = sellSide.peek();
		if (topBuy.remaining<=0) {
			new Exception("topBuy.remaining<=0").printStackTrace();
		}
		else if (topSell.remaining<=0) {
			new Exception("topSell.remaining<=0").printStackTrace();
		}
		else if (topBuy.filled<0) {
			new Exception("topBuy.filled<0").printStackTrace();
		}
		else if (topSell.filled<0) {
			new Exception("topSell.filled<0").printStackTrace();
		}
		
		// The top buy and sell agree on price.
		// Determine number of shares transferred from seller to buyer.
		topSell.lastFillPrice=topBuy.lastFillPrice=price;
		if (topBuy.remaining <= topSell.remaining) {
			if (debug>DEBUG_FILL) {
				out.println(a()+"Seller bigger or same as buyer, transfer=" + topBuy.remaining);
			}
			topBuy.avgFillPrice = ((topBuy.avgFillPrice*topBuy.filled)+(price*topBuy.remaining))/(topBuy.filled+topBuy.remaining);
//			if (Double.isNaN(topBuy.avgFillPrice)) {
//new Exception().printStackTrace();
//			}
			topSell.avgFillPrice = ((topSell.avgFillPrice*topSell.filled)+(price*topBuy.remaining))/(topSell.filled+topBuy.remaining);
//			if (Double.isNaN(topSell.avgFillPrice)) {
//new Exception().printStackTrace();
//			}
			topBuy.filled += topBuy.remaining;
			topSell.filled += topBuy.remaining;
			topSell.remaining = topSell.remaining - topBuy.remaining;
			topBuy.remaining = 0;
			topBuy.orderState.m_status = "Filled";
			if (debug>DEBUG_FILL) {
				out.println(a()+"Filled BUY orderId=" + topBuy.order.m_orderId + ","+ topBuy.toString());
			}
			buySide.poll(); // discard filled order - TODO send to auditing
			fillHappened=true;
			
			if (topSell.remaining==0)	{
				topSell.orderState.m_status = "Filled";
				sellSide.poll(); // discard filled order - TODO send to auditing
				fillHappened=true;
				if (debug>DEBUG_FILL) {
					out.println(a()+"Filled SELL orderId=" + ","+ topSell.order.m_orderId + topSell.toString());
				}
			}
			else if (topSell.remaining < 0) {
				new Exception("topSell.remaining < 0").printStackTrace();
			}
			else {
				topSell.orderState.m_status = "Submitted";
				if (debug>DEBUG_FILL) {
					out.println(a()+"Partially filled SELL orderId=" + topSell.order.m_orderId + ","+ topSell.toString());
				}
			}
		}
		else { // topBuy.remaining > topSell.remaining
			if (debug>DEBUG_FILL) {
				out.println(a()+"Seller smaller than buyer, transfer=" + topSell.remaining);
			}
			topSell.avgFillPrice = ((topSell.avgFillPrice*topSell.filled)+(price*topSell.remaining))/(topSell.filled+topSell.remaining);
			topBuy.avgFillPrice = ((topBuy.avgFillPrice*topBuy.filled)+(price*topSell.remaining))/(topBuy.filled+topSell.remaining);
			topBuy.filled += topSell.remaining;
			topSell.filled += topSell.remaining;
			topBuy.remaining = topBuy.remaining - topSell.remaining;
			topSell.remaining = 0;
			topSell.orderState.m_status = "Filled";
			if (debug>DEBUG_FILL) {
				out.println(a()+"Filled SELL orderId=" + topSell.order.m_orderId +","+ topSell.toString());
			}
			sellSide.poll(); // discard filled order - TODO route to auditing
			fillHappened = true;

			if (topBuy.remaining <= 0) {
				new Exception("topBuy.remaining <= 0").printStackTrace();
			}
			else {
				topBuy.orderState.m_status = "Submitted";
				if (debug>DEBUG_FILL) {
					out.println(a()+"Partially filled BUY orderId=" + topBuy.order.m_orderId + "," + topBuy.toString());
				}
			}
		}				
		
		try {
			twsQueue.put(new OrderStatusEvent(Book.this, 
					topBuy.order.m_orderId, 
					topBuy.orderState.m_status,
					topBuy.filled,
					topBuy.remaining,
					topBuy.avgFillPrice,
					topBuy.permId,
					topBuy.parentId,
					topBuy.lastFillPrice,
					topBuy.clientId,
					topBuy.whyHeld
					));
			twsQueue.put(new OrderStatusEvent(Book.this, 
					topSell.order.m_orderId, 
					topSell.orderState.m_status,
					topSell.filled,
					topSell.remaining,
					topSell.avgFillPrice,
					topSell.permId,
					topSell.parentId,
					topSell.lastFillPrice,
					topSell.clientId,
					topSell.whyHeld
					));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Update this book's bid, last, ask
		if (debug>DEBUG_BLA) {
			String s = a()+"Old (bid,last,ask)=("+bid+","+last+","+ask+")";
			out.println(s);
		}
		Book.this.last = price;

		// Set the new bid
		
		topBuy = buySide.peek();
		
		bid=null;
		if (topBuy != null)	switch (topBuy.order.m_orderType) {
		case "LMT": 
			bid=topBuy.order.m_lmtPrice;
			break;
		case "MKT":
			bid=null;
			break;
		default:
			String s=a()+"Unhandled action on top of book BUY order: "+topBuy.order.m_orderType;
			try {
				errQueue.put(new ErrorEvent(this, 0, 0, s));
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Exception(s).printStackTrace();
		}

		topSell = sellSide.peek();
		ask=null;
		if (topSell != null) switch (topSell.order.m_orderType) {
		case "LMT": 
			ask=topSell.order.m_lmtPrice;
			break;
		case "MKT":
			ask=null;
			break;
		default:
			String s=a()+"Unhandled action on top of book SELL order: "+topSell.order.m_orderType;
			try {
				errQueue.put(new ErrorEvent(this, 0, 0, s));
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			new Exception(s).printStackTrace();

		}

		if (debug>DEBUG_BLA) {
			String s = a()+"New (bid,last,ask)=("+bid+","+last+","+ask+")";
			out.println(s);
		}

		fireBookDataChanged();
		return fillHappened;
	}

	private final String a() {
		return "BOOK("+contract.m_symbol/*+","+this.hashCode()*/+"): ";
	}
	
	public void dump() {

		synchronized (this) {
			out.println(a() + "DUMPING - BUYSIDE - BUYSIDE - BUYSIDE - BUYSIDE - BUYSIDE ");
			PriorityQueue<BuyOrder> copyBuySide = new PriorityQueue<>(buySide);
			for (BuyOrder o = copyBuySide.poll(); o != null; o = copyBuySide.poll()) {
				out.println(o.order.toString());
			}
			out.println(a() + "DUMPING - SELLSIDE - SELLSIDE - SELLSIDE - SELLSIDE - SELLSIDE ");
			PriorityQueue<SellOrder> copySellSide = new PriorityQueue<>(sellSide);
			for (SellOrder o = copySellSide.poll(); o != null; o = copySellSide.poll()) {
				out.println(o.order.toString());
			}
		}
	}

}
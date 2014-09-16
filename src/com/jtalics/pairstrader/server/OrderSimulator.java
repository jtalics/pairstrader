package com.jtalics.pairstrader.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.TickType;
import com.ib.contracts.StkContract;
import com.jtalics.pairstrader.Outputable;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class OrderSimulator extends Thread {
	// Monitors the books and places orders opposite ones it observes,
	// and puts orders in empty books.
	public final Map<Contract, Book> contractToBook;
	public final Map<Book,Double> bookToMeanPrice=new HashMap<>();
	private Outputable out;
	static boolean debug = false;
	
	public OrderSimulator(Map<Contract,Book> contractToBook, Outputable out) {
		super("ORDER MONKEY");
		this.contractToBook = contractToBook;
		this.out = out;
	}
	int nextValidOrderId=10000000; // Monkey's order Id's start at ten million
	boolean quit = false;

	public void quit() {
		quit=true;
	}
	
	@Override
	public void run() {
		if (debug) out.println("MONKEY: starting");
		while (!quit) {
			for (Book book : contractToBook.values()) {
				Double meanPrice = bookToMeanPrice.get(book);
				if (meanPrice == null) {
					meanPrice = random.nextDouble()*100+10;
					bookToMeanPrice.put(book, meanPrice);
				}
				if (debug) out.println("MONKEY: Mutating "+book.contract.m_symbol);
				// Buy
				Order order = new Order();
				order.m_action="BUY";
				order.m_orderType="LMT";
				order.m_totalQuantity=100;
				order.m_orderId=nextValidOrderId++;
				order.m_lmtPrice = getNextPrice(meanPrice);
				book.placeOrder(order);						
				// Sell
				order = new Order();
				order.m_action="SELL";
				order.m_orderType="LMT";
				order.m_totalQuantity=100;
				order.m_orderId=nextValidOrderId++;
				order.m_lmtPrice = getNextPrice(meanPrice);
				book.placeOrder(order);						
			}
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (debug) out.println("MONKEY: quiting");

	}

	private void unStickBook(final Book book) {
		BuyOrder topBuy = book.buySide.peek();
		SellOrder topSell = book.sellSide.peek();
		if (topBuy == null) {
			if (debug) out.println("MONKEY: empty buyside");
			Order order = new Order();
			order.m_action="BUY";
			order.m_orderType="LMT";
			order.m_lmtPrice=1000.0;
			order.m_totalQuantity=100;
			order.m_orderId=nextValidOrderId++;
			book.placeOrder(order);
		}
		else if (topBuy.order.m_orderType.equals("MKT") ) {
			if (debug) out.println("MONKEY: topbuy is MKT");
			Order order = new Order();
			order.m_action="SELL";
			order.m_orderType="MKT";
			order.m_totalQuantity=100;
			order.m_orderId=nextValidOrderId++;
			book.placeOrder(order);
		}
		else if (topBuy.order.m_orderType.equals("LMT")) {
			if (debug) out.println("MONKEY: topbuy is LMT");
			Order order = new Order();
			order.m_action="SELL";
			order.m_orderType="LMT";
			order.m_totalQuantity=100;
			order.m_orderId=nextValidOrderId++;
			order.m_lmtPrice = book.buySide.peek().order.m_lmtPrice;
			book.placeOrder(order);						
		}
		else if (topSell == null) {
			if (debug) out.println("MONKEY: empty sellside");
			Order order = new Order();
			order.m_action="SELL";
			order.m_orderType="LMT";
			order.m_lmtPrice=1001.0;
			order.m_orderId=nextValidOrderId++;
			order.m_totalQuantity=100;
			book.placeOrder(order);
		}
		else if (topSell.order.m_orderType.equals("MKT") ) {
			if (debug) out.println("MONKEY: topsell is MKT");
			Order order = new Order();
			order.m_action="BUY";
			order.m_orderType="MKT";
			order.m_totalQuantity=100;
			order.m_orderId=nextValidOrderId++;
			book.placeOrder(order);						
		}
		else if (topSell.order.m_orderType.equals("LMT")) {
			if (debug) out.println("MONKEY: topsell is LMT");
			Order order = new Order();
			order.m_action="BUY";
			order.m_orderType="LMT";
			order.m_lmtPrice = book.sellSide.peek().order.m_lmtPrice;
			order.m_orderId=nextValidOrderId++;
			book.placeOrder(order);												
		}
		else {
			new Exception("Unhandled order type").printStackTrace();
		}

	}
	
	public void populateBook(Book book) {
		Double meanPrice = bookToMeanPrice.get(book);
		if (meanPrice == null) {
			meanPrice = random.nextDouble()*100+10;
			bookToMeanPrice.put(book, meanPrice);
		}
		if (debug) out.println("MONKEY: Creating market for Book="+book.contract.m_symbol);
		// place limit orders on each side
		int depth=20;
		for (int i=0; i<depth; i++) {

			Order buyOrder = new Order();
			buyOrder.m_action="BUY";
			buyOrder.m_totalQuantity=random.nextInt(1000)+1;
			buyOrder.m_orderType="LMT";
			buyOrder.m_lmtPrice=getNextPrice(meanPrice);
			buyOrder.m_orderId=nextValidOrderId++;
			
			book.placeOrder(buyOrder,false);
			
			Order sellOrder = new Order();
			sellOrder.m_action="SELL";
			sellOrder.m_totalQuantity=random.nextInt(1000)+1;
			sellOrder.m_orderType="LMT";
			sellOrder.m_lmtPrice=getNextPrice(meanPrice);
			sellOrder.m_orderId=nextValidOrderId++;

			book.placeOrder(sellOrder, false); // hold off on matching
		}	
		
		book.match();
		if (debug) book.dump();
	}
	
	static Random random = new Random(0); // use same seed

	public static double getNextPrice(double meanPrice) {
		return meanPrice+1.0*normal();
	}
	
	public static double normal() {
		double u1 = random.nextDouble(); 
		double u2 = random.nextDouble();
		// Box-Muller method of calculation - mu=0 and sigma=1
		double retval = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);
		if (retval < -100.0 || retval > 100.0) {retval = normal();}
		return retval;
	}

	public static void main(String[] args) {
		System.out.println("THE MONKEY LIVES!");
		final Outputable out = new Outputable.Default();

		final BlockingQueue<ServerEvent> twsQueue = new LinkedBlockingQueue<>();
		final BlockingQueue<MdPriceTickEvent> mdQueue = new LinkedBlockingQueue<>();
		final BlockingQueue<ErrorEvent> errQueue = new LinkedBlockingQueue<>();

		new Thread("MONKEY MARKET DATA PUMP") {
			@Override
			public void run() {
				while(true) {	
					try {
						MdPriceTickEvent mdEvent = mdQueue.take();
						if (debug) switch(mdEvent.field) {
						case TickType.BID:
							out.println("Bid: " + mdEvent.price);
							break;
						case TickType.ASK:
							out.println("Ask: " + mdEvent.price);
							break;
						case TickType.LAST:
							out.println("Last: " + mdEvent.price);
							break;
						}
						//System.out.println("EVENT: "+mdEvent.getClass().getSimpleName()+":"+mdEvent.toString());
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		new Thread("MONKEY MESSAGE PUMP") {
			@Override
			public void run() {
				while(true) {	
					try {
						ServerEvent twsEvent = twsQueue.take();
						//System.out.println("EVENT: "+twsEvent.getClass().getSimpleName()+":"+twsEvent.toString());
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		new Thread("MONKEY ERROR PUMP") {
			@Override
			public void run() {
				while(true) {	
					try {
						ErrorEvent errEvent = errQueue.take();
						//System.out.println("EVENT: "+errEvent.getClass().getSimpleName()+":"+errEvent.toString());
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		Contract contract = new StkContract("C");
		Book book = new Book(twsQueue, mdQueue, errQueue, contract, out);
		final Map<Contract, Book> contractToBook = new ConcurrentHashMap<>();
		contractToBook.put(contract, book);
		OrderSimulator orderMonkey = new OrderSimulator(contractToBook, out);
		book.last=10.0;// lamp post drunk wanders around
		//orderMonkey.populateBook(book);
		orderMonkey.start();
		//out.println("N O R M A L   C O M P L E T I O N");
	}
}
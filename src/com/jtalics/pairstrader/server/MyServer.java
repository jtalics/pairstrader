package com.jtalics.pairstrader.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;
import com.jtalics.pairstrader.events.ManagedAccountsEvent;
import com.jtalics.pairstrader.events.ValidIdEvent;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class MyServer implements TWSAPISimulator{

	// Market data generated from order books filling are put on the mdQueue
	LinkedBlockingQueue<ServerEvent> twsQueue = new LinkedBlockingQueue<>();
	// The ticker queue distributes market data from the mdQueue to each listener
	LinkedBlockingQueue<ServerEvent> orderQueue = new LinkedBlockingQueue<>();
	// Market data generated from order books filling are put on the mdQueue
	LinkedBlockingQueue<MdPriceTickEvent> mdQueue = new LinkedBlockingQueue<>();
	// The ticker queue distributes market data from the mdQueue to each listener
	LinkedBlockingQueue<MdPriceTickEvent> tickerQueue = new LinkedBlockingQueue<>();
	// Errors generated from server
	LinkedBlockingQueue<ErrorEvent> errQueue = new LinkedBlockingQueue<>();
	// The ticker queue distributes market data from the mdQueue to each listener
	// LinkedBlockingQueue<MdPriceTickEvent> tickerQueue = new LinkedBlockingQueue<>();
	/**
	 * Each contract has a trading book
	 */
	Map<Contract,Book> contractToBook = new ConcurrentHashMap<>();
	public final OrderSimulator orderMonkey; 
	
	public final MainFrame mainFrame;
	private final Map<Contract,List<Integer>> contractToTickerIds = new ConcurrentHashMap<>();
	/**
	 * TickerIdToContract is used to cancel market data
	 */
	private final Map<Integer,Contract> tickerIdToContract = new HashMap<>();
	private boolean connected = false;
	private boolean debug=false;
	private boolean monkeyAround=true;
	private boolean populateMarket = true;
	
	public MyServer(final MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.orderMonkey = new OrderSimulator(contractToBook,mainFrame);
		
		new Thread("MARKET DATA PUSH") {
			@Override
			public void run() {
				while(true) {	
					try {
						MdPriceTickEvent mdEvent = mdQueue.take();
						Contract contract = (Contract)mdEvent.getSource();
						List<Integer> tickerIds = contractToTickerIds.get(contract);
						if (tickerIds != null) {
							synchronized (tickerIds) {
//System.out.println("firing on "+Thread.currentThread().getName());

								for (Integer tickerId : tickerIds) {
									try {
										mainFrame.fireMarketData(mainFrame, tickerId, mdEvent.field, mdEvent.price, mdEvent.canAutoExecute);
									}
									catch (Throwable t) {
										System.err.println("mdEvent=" + mdEvent);
										t.printStackTrace();
									}
								}
							} // if null then ignore, somebody unsubscribed
						}
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		new Thread("MYSERVER MESSAGES PUMP") {
			@Override
			public void run() {
				while(true) {	
					try {
						ServerEvent serverEvent = twsQueue.take();
						mainFrame.fireServerEvent(serverEvent);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		new Thread("MYSERVER ERROR PUMP") {
			@Override
			public void run() {
				while(true) {	
					try {
						ErrorEvent errEvent = errQueue.take();
						mainFrame.fireErrorEvent(errEvent);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();

		if (monkeyAround) {
			mainFrame.println("Starting order monkey.");
			orderMonkey.start();
		}
	}
	
	@Override
	public void cancelOrder(int orderId) {
		if (debug) mainFrame.println("MYSERVER: cancelOrder()");
	}

	@Override
	public void reqContractDetails(int reqId, Contract contract) {
		if (debug) mainFrame.println("MYSERVER: reqContractDetails");
	}

	@Override
	public void cancelMktData(int tickerId) {
		
		if (debug) mainFrame.println("MYSERVER: cancelMktData()");

		Contract contract = tickerIdToContract.get(tickerId);
		if (contract == null) {
			// maybe it was already cancelled?
			new Exception().printStackTrace();
			return;
		}

		List<Integer> tickerIds = contractToTickerIds.get(contract);
		if (tickerIds == null) {
			new Exception("tickerIds == null").printStackTrace();
			return;
		}
		synchronized (tickerIds) {
			// System.out.println("removing on "+Thread.currentThread().getName());
			boolean found = tickerIds.remove((Integer) tickerId);
			if (!found) {
				new Exception("").printStackTrace();
			}
			if (tickerIds.size() <= 0) {
				contractToTickerIds.remove(contract);
				Book book = contractToBook.get(contract);
				if (book == null) {
					new Exception("book == null").printStackTrace();
					return;
				}
				contractToBook.remove(book);
			}
		}
	}

	@Override
	public void reqMktData(int tickerId, Contract contract, String genericTicks, boolean snapshotMktData) {

		// Set up a random book for pretty much any contract
		if (debug) mainFrame.println("MYSERVER: reqMktData() for "+contract.m_symbol+", tickerId = "+tickerId);
		
		tickerIdToContract.put(tickerId, contract);
		Book book = contractToBook.get(contract);
		// Make a new book for the symbol if first time
		if (book == null) {
			book = new Book(twsQueue, mdQueue, errQueue, contract, mainFrame);
			contractToBook.put(contract,book);
		}
		
		List<Integer> tickerIds = contractToTickerIds.get(contract);
		if (tickerIds == null) {
			tickerIds = new ArrayList<Integer>();
			tickerIds.add(tickerId);
			contractToTickerIds.put(contract,tickerIds);
			if (populateMarket) populateMarket(contract);
		}
		else {
			synchronized(tickerIds) { // somebody else might be using tickerIds
//System.out.println("adding(1) on "+Thread.currentThread().getName());
				tickerIds.add(tickerId); // TODO: check duplicate
			}
			contractToTickerIds.put(contract,tickerIds);
		}
	}

	@Override
	public void reqIds(int idCount) {
		if (debug) mainFrame.println("MYSERVER: reqIds()");
		// arbitrarily start at about 100
		mainFrame.fireServerEvent(new ValidIdEvent(mainFrame, 100));
	}

	@Override
	public void disconnect() {
		if (debug) mainFrame.println("MYSERVER: disconnect()");
		orderMonkey.quit();
		connected  = false;
	}

	@Override
	public void placeOrder(int orderId, Contract contract, Order order) {
		if (debug) {
			mainFrame.println("MYSERVER: placeOrder(), orderId=" + orderId);
		}
		
		Book book = contractToBook.get(contract);

		if (book == null) {
			book = new Book(twsQueue, mdQueue, errQueue, contract, mainFrame);
			contractToBook.put(contract, book);
		}
		book.placeOrder(order);
	}

	@Override
	public boolean isConnected() {
		if (debug) mainFrame.println("MYSERVER: isConnected()");
		return connected;
	}

	@Override
	public void reqPositions() {
		if (debug) mainFrame.println("MYSERVER: rqPositions()");

	}

	@Override
	public void eConnect(String ipAddr, int port, int clientId) {
		if (debug) mainFrame.println("MYSERVER: eConnect()");
		connected = true;
		mainFrame.fireServerEvent(new ManagedAccountsEvent(mainFrame, "myacctslist"));
	}

	public void populateMarket(Contract contract) {
		orderMonkey.populateBook(contractToBook.get(contract));
	}
	
	@Override
	public void finalize() {
		System.out.println(this.getClass().getSimpleName()+":"+this.hashCode()+" finalized");
	}
}

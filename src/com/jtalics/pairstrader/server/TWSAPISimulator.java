package com.jtalics.pairstrader.server;

import com.ib.client.Contract;
import com.ib.client.Order;

public interface TWSAPISimulator {

	void cancelOrder(int orderId);

	void reqContractDetails(int reqId, Contract contract);

	void cancelMktData(int tickerId);

	void reqMktData(int tickerId, Contract contract, String genericTicks, boolean snapshotMktData);

	void reqIds(int idCount);

	void disconnect();

	void placeOrder(int orderId, Contract contract, Order order);

	boolean isConnected();

	void reqPositions();

	void eConnect(String ipAddr, int port, int clientId);
}

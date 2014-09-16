package com.jtalics.pairstrader.events;

import com.ib.client.Contract;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class PositionEvent extends ServerListener.ServerEvent {

	public final String account;
	public final Contract contract;
	public final int pos;
	public final double avgCost;

	public PositionEvent(MainFrame mainFrame, String account, Contract contract, int pos, double avgCost) {
		super(mainFrame);
		this.account = account;
		this.contract = contract;
		this.pos=pos;
		this.avgCost = avgCost;		
	}
}

package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ContractDetailsEndEvent extends ServerListener.ServerEvent {

	public final int reqId;
	
	public ContractDetailsEndEvent(MainFrame mainFrame, int reqId) {

		super(mainFrame);
		this.reqId=reqId;
	}

}

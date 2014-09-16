package com.jtalics.pairstrader.events;

import com.ib.client.ContractDetails;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ContractDetailsEvent extends ServerListener.ServerEvent {

	public final int reqId;
	public final ContractDetails contractDetails;

	public ContractDetailsEvent(MainFrame mainFrame, int reqId, ContractDetails contractDetails) {
		super(mainFrame);
		this.reqId = reqId;
		this.contractDetails = contractDetails;
	}

}

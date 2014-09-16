package com.jtalics.pairstrader.events;

import com.ib.client.ContractDetails;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class BondContractDetailsEvent extends ServerListener.ServerEvent {

	public BondContractDetailsEvent(MainFrame mainFrame, int reqId, ContractDetails contractDetails) {
		super(mainFrame);
	}

}

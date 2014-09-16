package com.jtalics.pairstrader.events;

import com.ib.client.Contract;
import com.ib.client.Execution;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ExecDetailsEvent extends ServerListener.ServerEvent {

	public final int reqId;
	public final Contract contract;
	public final Execution execution;

	public ExecDetailsEvent(MainFrame mainFrame, int reqId, Contract contract, Execution execution) {
		super(mainFrame);
		this.reqId = reqId;
		this.contract = contract;
		this.execution = execution;		
	}

	@Override
	public String toString() {
		return "[reqId="+reqId
				+";contract="+contract.toString()
				+";execution="+execution.toString()
				+"]";
	}
	
}

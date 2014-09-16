package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class AccountSummaryEndEvent extends ServerListener.ServerEvent {

	public AccountSummaryEndEvent(MainFrame mainFrame, int reqId) {
		super(mainFrame);

	}

}

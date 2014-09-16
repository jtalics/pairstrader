package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class AccountSummaryEvent extends ServerListener.ServerEvent {

	public AccountSummaryEvent(MainFrame mainFrame, int reqId, String account, String tag, String value, String currency) {
		super(mainFrame);

	}

}

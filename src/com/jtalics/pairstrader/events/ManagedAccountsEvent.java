package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ManagedAccountsEvent extends ServerListener.ServerEvent {

	public final String accountsList;
	
	public ManagedAccountsEvent(MainFrame mainFrame, String accountsList) {
		super(mainFrame);
		this.accountsList = accountsList;
	}
}

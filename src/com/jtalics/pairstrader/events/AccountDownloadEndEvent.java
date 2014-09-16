package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class AccountDownloadEndEvent extends ServerListener.ServerEvent {

	public AccountDownloadEndEvent(MainFrame mainFrame, String accountName) {
		super(mainFrame);
	}

}

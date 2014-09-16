package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class OpenOrderEndEvent extends ServerListener.ServerEvent {

	public OpenOrderEndEvent(MainFrame mainFrame) {
		super(mainFrame);
	}

}

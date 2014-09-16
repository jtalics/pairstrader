package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ValidIdEvent extends ServerListener.ServerEvent {

	public final int nextValidOrderId;
	
	public ValidIdEvent(MainFrame mainFrame, int nextValidOrderId) {
		super(mainFrame);
		this.nextValidOrderId = nextValidOrderId;
	}
}

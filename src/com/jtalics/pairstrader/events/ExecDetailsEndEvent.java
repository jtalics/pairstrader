package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class ExecDetailsEndEvent extends ServerListener.ServerEvent {

	public ExecDetailsEndEvent(MainFrame mainFrame, int reqId) {
		super(mainFrame);
	}

}

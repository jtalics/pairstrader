package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class CurrentTimeEvent extends ServerListener.ServerEvent {

	public CurrentTimeEvent(MainFrame mainFrame, long time) {
		super(mainFrame);
	}

}

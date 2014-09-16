package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class PositionEndEvent extends ServerListener.ServerEvent {

	public PositionEndEvent(MainFrame mainFrame) {
		super(mainFrame);

	}

}

package com.jtalics.pairstrader.events;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class DisplayXMLEvent extends ServerListener.ServerEvent {

	public DisplayXMLEvent(MainFrame mainFrame, String title, String xml) {
		super(mainFrame);

	}

}

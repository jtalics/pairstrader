package com.jtalics.pairstrader.events;

import com.ib.client.UnderComp;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class DeltaNeutralValidationEvent extends ServerListener.ServerEvent {

	public DeltaNeutralValidationEvent(MainFrame mainFrame, int reqId, UnderComp underComp) {
		super(mainFrame);

	}

}

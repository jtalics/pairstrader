package com.jtalics.pairstrader.events;

import com.ib.client.CommissionReport;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerListener.ServerEvent;

public class CommissionReportEvent extends ServerListener.ServerEvent {

	public final CommissionReport commissionReport;

	public CommissionReportEvent(MainFrame mainFrame, CommissionReport commissionReport) {
		super(mainFrame);
		this.commissionReport = commissionReport;
	}

}

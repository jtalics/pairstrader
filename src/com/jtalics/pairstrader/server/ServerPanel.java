package com.jtalics.pairstrader.server;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.Outputable;
import com.jtalics.pairstrader.SimOutputPanel;
import com.jtalics.pairstrader.TwsOutputPanel;
import com.jtalics.pairstrader.MainFrame.ConnectedTo;

public class ServerPanel extends JPanel implements Outputable {

	public final TwsOutputPanel twsOutputPanel;
	public final SimOutputPanel simOutputPanel;
	
	public final MainFrame mainFrame;
	private ConnectedTo connectedTo = ConnectedTo.Standalone;
	
	public ServerPanel(MainFrame mainFrame) {
		
		super(new BorderLayout());
		this.mainFrame = mainFrame;
		twsOutputPanel = new TwsOutputPanel(mainFrame.m_errors, mainFrame.m_TWS, mainFrame.m_tickers);
		simOutputPanel = new SimOutputPanel();
		updatePanel(mainFrame.connectedTo);
	}
	
	public void updatePanel(final ConnectedTo connectedTo) {
		this.connectedTo = connectedTo;
		removeAll();
		switch(connectedTo) {
		case Standalone:
			add(new JLabel("<not connected>"));
			break;
		case Simulator:
			add(new JScrollPane(simOutputPanel));
			break;
		case TWS:
			add(twsOutputPanel);
			break;
		default:
			break;
		}

		revalidate();
		repaint();
	}

	@Override
	public void println(String ln) {
		print(ln+"\n");
	}

	@Override
	public void print(String string) {
		//System.out.println(string);
		switch (connectedTo) {
		case Standalone:
			System.out.println(string);
			break;
		case Simulator:
			simOutputPanel.println(string);
			break;
		case TWS:
			TwsOutputPanel.println(string);
			break;
		default:
			break;
		}
	}

	@Override
	public void println() {
		print("\n");
	}

	@Override
	public void clear() {
		switch (connectedTo) {
		case Standalone:
			// ignore
			break;
		case Simulator:
			simOutputPanel.clear();
			break;
		case TWS:
			twsOutputPanel.clear();
			break;
		default:
			break;
		}
	}
}

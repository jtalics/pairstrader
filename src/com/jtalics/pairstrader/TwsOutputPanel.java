package com.jtalics.pairstrader;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import TestJavaClient.IBTextPanel;

public class TwsOutputPanel extends JPanel {
	public static Preferences prefMainFrame=PreferencesDialog.pref.node("MAIN_FRAME");

	public TwsOutputPanel(IBTextPanel m_errors, IBTextPanel m_TWS, IBTextPanel m_tickers) {
		final Preferences prefDividerLoc = prefMainFrame.node("DIVIDER_LOC");

		setLayout(new BorderLayout());
		final JSplitPane sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp1.setOneTouchExpandable(true);
		final String KEY1="twsOutputSplitPane_1";
		sp1.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String propertyName = pce.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
          int current = sp1.getDividerLocation();
    			prefDividerLoc.putInt(KEY1,current);
				}				
			}
		});
		add(sp1,BorderLayout.CENTER);
		sp1.setLeftComponent(m_errors);
		final JSplitPane sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp2.setOneTouchExpandable(true);
		final String KEY2="twsOutputSplitPane_2";
		sp2.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String propertyName = pce.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
          int current = sp2.getDividerLocation();
    			prefDividerLoc.putInt(KEY2,current);
				}				
			}
		});
		sp1.setRightComponent(sp2);
		sp1.setDividerLocation(prefDividerLoc.getInt(KEY1,133));

		sp2.setLeftComponent(m_TWS);
		sp2.setRightComponent(m_tickers);
		sp2.setDividerLocation(prefDividerLoc.getInt(KEY2,133));
	}

	public static void println(String ln) {
		System.out.println(ln);
	}

	public void clear() {
		// TODO
	}
}

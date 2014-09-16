package com.jtalics.pairstrader;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.help.CSH;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import TestJavaClient.IBTextPanel;

import com.ib.client.EClientSocket;
import com.jtalics.pairstrader.MainFrame.ConnectedTo;
import com.jtalics.pairstrader.actions.ActionAbout;
import com.jtalics.pairstrader.actions.ActionAppendRow;
import com.jtalics.pairstrader.actions.ActionCloseAll;
import com.jtalics.pairstrader.actions.ActionEditPreferences;
import com.jtalics.pairstrader.actions.ActionExit;
import com.jtalics.pairstrader.actions.ActionPairsExport;
import com.jtalics.pairstrader.actions.ActionPairsImport;
import com.jtalics.pairstrader.actions.ActionSecExport;
import com.jtalics.pairstrader.actions.ActionHelpSystem;
import com.jtalics.pairstrader.actions.ActionSecImport;
import com.jtalics.pairstrader.actions.ActionOnyx;
import com.jtalics.pairstrader.actions.ActionResetPref;
import com.jtalics.pairstrader.actions.ActionValidate;
import com.jtalics.pairstrader.trades.AutoCloseMethod;

public class MenuBar extends JMenuBar {
	public MenuBar(final MainFrame mainFrame, EClientSocket m_client, IBTextPanel m_TWS) {

		JMenu fileMenu = new JMenu("File");
		add(fileMenu);

		JMenu connectToMenu = new JMenu("ConnectTo");
		fileMenu.add(connectToMenu);
		ButtonGroup buttonGroup = new ButtonGroup();
		for (MainFrame.ConnectedTo connectTo : MainFrame.ConnectedTo.values()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(connectTo.name());
			item.setSelected(connectTo == mainFrame.connectedTo);
			buttonGroup.add(item);
			item.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						mainFrame.disconnectFrom(mainFrame.connectedTo);
						MainFrame.ConnectedTo connectedTo = MainFrame.ConnectedTo.valueOf((((JRadioButtonMenuItem)e.getItem()).getText()));
						mainFrame.setConnectTo(connectedTo);
						// TODO: disconnect if changed?
						
//						if (mainFrame.positionPanel != null && mainFrame.positionPanel.model != null) { 
//							mainFrame.positionPanel.model.fireTableStructureChanged();
//							mainFrame.positionPanel.setCellRenderers();
//						}
					}
				}});
			item.setSelected(PreferencesDialog.getAutoCloseMethod().equals(connectTo));
			connectToMenu.add(item);
		}

		JMenu filePreferencesMenu = new JMenu("Preferences");
		fileMenu.add(filePreferencesMenu);
		filePreferencesMenu.add(new JMenuItem(new ActionEditPreferences(mainFrame)));
		filePreferencesMenu.add(new JMenuItem(new ActionResetPref(mainFrame)));
		filePreferencesMenu.add(new JMenuItem("Export"));

		fileMenu.add(new JSeparator());
		fileMenu.add(new JMenuItem(new ActionExit(mainFrame)));

		JMenu securitiesMenu = new JMenu("Stocks");
		add(securitiesMenu);
		JMenuItem securitiesAppendRowMenuItem = new JMenuItem(new ActionAppendRow(mainFrame));
		securitiesMenu.add(securitiesAppendRowMenuItem);
		securitiesMenu.add(new JSeparator());
		JMenuItem securitiesImportMenuItem = new JMenuItem(new ActionSecImport(mainFrame));
		securitiesMenu.add(securitiesImportMenuItem);
		JMenuItem securitiesExportMenuItem = new JMenuItem(new ActionSecExport(mainFrame));
		securitiesMenu.add(securitiesExportMenuItem);

		JMenu pairsMenu = new JMenu("Pairs");
		add(pairsMenu);
		JMenuItem importMenuItem = new JMenuItem(new ActionPairsImport(mainFrame));
		pairsMenu.add(importMenuItem);
		JMenuItem exportMenuItem = new JMenuItem(new ActionPairsExport(mainFrame));
		pairsMenu.add(exportMenuItem);

		JMenu positionsMenu = new JMenu("Trades");
		add(positionsMenu);
		JMenu autoCloseMenu = new JMenu("AutoClose");
		positionsMenu.add(autoCloseMenu);
		buttonGroup = new ButtonGroup();
		for (AutoCloseMethod method : AutoCloseMethod.values()) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(method.name());
			buttonGroup.add(item);
			item.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == e.SELECTED) {
						AutoCloseMethod method = AutoCloseMethod.valueOf((((JRadioButtonMenuItem)e.getItem()).getText()));
						PreferencesDialog.setAutoCloseMethod(method);
						if (mainFrame.positionPanel != null && mainFrame.positionPanel.model != null) { 
							mainFrame.positionPanel.model.fireTableStructureChanged();
							//mainFrame.positionPanel.setCellRenderers();
						}
					}
				}});
			item.setSelected(PreferencesDialog.getAutoCloseMethod().equals(method));
			autoCloseMenu.add(item);
		}
		JMenuItem validateMenuItem = new JMenuItem(new ActionValidate(mainFrame));
		positionsMenu.add(validateMenuItem);
		JMenuItem closeAllMenuItem = new JMenuItem(new ActionCloseAll(mainFrame));
		positionsMenu.add(closeAllMenuItem);

		JMenu helpMenu = new JMenu("Help");
		add(Box.createHorizontalGlue());
		add(helpMenu);

		//helpMenu.add(new JMenuItem(new ActionOnyx(mainFrame)));
		helpMenu.add(new JMenuItem(new ActionHelpSystem(mainFrame)));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem(new ActionAbout(mainFrame)));
	}
}

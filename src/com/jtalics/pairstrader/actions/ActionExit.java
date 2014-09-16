package com.jtalics.pairstrader.actions;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ib.client.EClientSocket;
import com.jtalics.pairstrader.MainFrame;

import TestJavaClient.ConnectDlg;
import TestJavaClient.IBTextPanel;

public class ActionExit extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionExit(MainFrame mainFrame) {
		super("Exit");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.exit();
	}
}

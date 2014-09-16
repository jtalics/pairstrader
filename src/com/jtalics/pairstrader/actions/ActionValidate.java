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

public class ActionValidate extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionValidate(MainFrame mainFrame) {
		super("Validate");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainFrame.reqPositions();
	}
}

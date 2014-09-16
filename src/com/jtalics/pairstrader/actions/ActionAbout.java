package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;

public class ActionAbout extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionAbout(MainFrame mainFrame) {
		super("About");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String message = "Pairs Trader\nJTalics LLC\nNo warranty, implied or express.\nExpires  " 
	    + Main.expirationDate.toString();
		JOptionPane.showMessageDialog(mainFrame, message, "About", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(mainFrame.getIconImage()));
	}
}

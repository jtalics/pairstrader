package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.help.CSH;
import javax.help.plaf.basic.BasicFavoritesNavigatorUI.AddAction;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;

public class ActionHelpSystem extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionHelpSystem(MainFrame mainFrame) {
		super("Help");
		this.mainFrame = mainFrame;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new CSH.DisplayHelpFromSource( Main.hb ).actionPerformed(e);
	}
}

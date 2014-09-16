package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.PreferencesDialog;

public class ActionResetPref extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionResetPref(MainFrame mainFrame) {
		super("Reset all pref");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(mainFrame, "Really reset all preferences to default?")) {
			PreferencesDialog.clear();
		}
	}
}

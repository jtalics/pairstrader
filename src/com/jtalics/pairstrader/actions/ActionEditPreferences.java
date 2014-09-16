package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.PreferencesDialog;

public class ActionEditPreferences extends AbstractAction {

	public final MainFrame mainFrame;

	public ActionEditPreferences(MainFrame mainFrame) {
		super("Edit");
		this.mainFrame = mainFrame;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		PreferencesDialog dialog = new PreferencesDialog(mainFrame);
		Main.centerOnScreen(dialog);
		dialog.setVisible(true);
		if (dialog.cancelled) {
			return;
		}
	}
}

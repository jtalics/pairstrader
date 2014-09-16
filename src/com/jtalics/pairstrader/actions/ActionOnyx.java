package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import com.jtalics.onyx.Main;

import com.jtalics.pairstrader.MainFrame;

public class ActionOnyx extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionOnyx(MainFrame mainFrame) {
		super("Experimental");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Main.main(null);
	}
}

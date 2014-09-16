package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.Main;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.util.FileChooser;

public class ActionSecExport extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionSecExport(MainFrame mainFrame) {
		super("Export");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileChooser fileChooser = mainFrame.getFileChooser();
		fileChooser.setDialogTitle("Select CSV file or specify name for export.");
		int option=fileChooser.showDialog(mainFrame.securityPanel, "Export CSV");
		if (option != JFileChooser.APPROVE_OPTION) {return;}
		File file = fileChooser.getSelectedFile();
		try (FileWriter fileWriter  = new FileWriter(file);) {
			mainFrame.securityPanel.exportAllRows(fileWriter);
		}
		catch (IOException e1) {
			String message = "Can't write to: "+file;
			JOptionPane.showMessageDialog(mainFrame.securityPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
			new Exception(message,e1).printStackTrace();
		}
	}
}

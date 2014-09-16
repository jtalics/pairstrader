package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.util.FileChooser;

public class ActionPairsExport extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionPairsExport(MainFrame mainFrame) {
		super("Export");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileChooser fileChooser = MainFrame.getFileChooser();
		fileChooser.setDialogTitle("Select CSV file or specify name for export.");
		fileChooser.setVisible(true);
		int option=fileChooser.showDialog(mainFrame.pairsPanel, "Export CSV");
		if (option != JFileChooser.APPROVE_OPTION) {return;}
		File file = fileChooser.getSelectedFile();
		try (FileWriter fw = new FileWriter(file)) {
			mainFrame.pairsPanel.exportAllRows(fw);
		}
		catch (IOException e1) {
			String message = "Can't write to: "+file;
			JOptionPane.showMessageDialog(mainFrame.securityPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
			new Exception(message,e1).printStackTrace();
		}
	}
}

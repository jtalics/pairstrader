package com.jtalics.pairstrader.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
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

public class ActionSecImport extends AbstractAction {

	private final MainFrame mainFrame;

	public ActionSecImport(MainFrame mainFrame) {
		super("Import");
		this.mainFrame = mainFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		FileChooser fileChooser = mainFrame.getFileChooser();
		int option=fileChooser.showDialog(mainFrame.securityPanel,"Import CSV");
		if (option != JFileChooser.APPROVE_OPTION) {return;}
		
		File file = fileChooser.getSelectedFile();
		try (FileReader fileReader = new FileReader(file)) {
			mainFrame.securityPanel.importCsv(fileReader);
		}
		catch (IOException e1) {
			String message = "Can't read from: "+file;
			JOptionPane.showMessageDialog(mainFrame.securityPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
			new Exception(message,e1).printStackTrace();
		}
	}
}

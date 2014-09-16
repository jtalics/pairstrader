package com.jtalics.pairstrader.util;

import java.awt.Component;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import com.jtalics.pairstrader.PreferencesDialog;

public class FileChooser extends JFileChooser {

	public static Preferences prefMainFrame=PreferencesDialog.pref.node("FILE_CHOOSER");
	private static String CURRENT_DIR="currentDir";
	
	@Override
	public int showDialog(Component owner, String title) {
		setCurrentDirectory(new File(prefMainFrame.get(CURRENT_DIR, System.getProperty("user.home"))));
		return super.showDialog(owner,title);
	}
	
	@Override
	public File getSelectedFile() {
		File currentDir = getCurrentDirectory();
		if (currentDir != null) {
			prefMainFrame.put(CURRENT_DIR, currentDir.getPath());
		}
		return super.getSelectedFile();
	}
}

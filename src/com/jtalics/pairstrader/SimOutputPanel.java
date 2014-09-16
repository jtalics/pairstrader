package com.jtalics.pairstrader;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class SimOutputPanel extends JPanel {
	private final JTextArea textArea = new JTextArea();
	public SimOutputPanel () {
		super(new BorderLayout());
		add(textArea);
		textArea.append("You are connected to the simulator.\n");
	}
	
	public void println(String ln) {
		textArea.append(ln);
		try {
			textArea.setCaretPosition(textArea.getLineStartOffset(textArea.getLineCount() - 1));
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		textArea.setText(null);
	}
}

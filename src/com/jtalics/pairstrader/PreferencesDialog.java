package com.jtalics.pairstrader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jtalics.pairstrader.MainFrame.ConnectedTo;
import com.jtalics.pairstrader.trades.AutoCloseMethod;
import com.jtalics.pairstrader.util.FontChooser;

public class PreferencesDialog extends JDialog {
	public static Preferences pref=Preferences.userRoot().node("JTalics").node(Main.PRODUCT_NAME);
	
	private final static String SHOW_ORDERS="showOrders";
	private final static String DEFAULT_BASE_QUANTITY="defBaseQty";
	private final static String AUTOCLOSE_METHOD="autoCloseMethod";
	private final static String AUTOCONNECT="autoConnect";
	private final static String CONNECTTO="connectTo";
	private final static String FONT="font";
	private final static String ROW_PADDING="rowPadding";

	private final JCheckBox showOrdersCheckBox = new JCheckBox();
	private final JSpinner defaultBaseQtySpinner = new JSpinner(new SpinnerNumberModel(new Integer(-100),new Integer(-1000000),new Integer(-1),new Integer(-1)));
	private final JComboBox<AutoCloseMethod> autoCloseMethodComboBox = new JComboBox<>(AutoCloseMethod.values());
	private final JCheckBox autoConnectCheckBox = new JCheckBox();
	private final JComboBox<MainFrame.ConnectedTo> connectToComboBox = new JComboBox<>(MainFrame.ConnectedTo.values());
	private final JButton fontButton = new JButton();
	private final FontChooser fontChooser = new FontChooser();
	private final JSpinner rowPaddingSpinner = new JSpinner(new SpinnerNumberModel(new Integer(2),new Integer(0),new Integer(1000),new Integer(1)));
	
	public boolean cancelled;
	
	public PreferencesDialog(final MainFrame mainFrame) {

		super(mainFrame);
		Font f =getAppFont();
		fontChooser.setSelectedFont(f);
		fontButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontChooser.showDialog(PreferencesDialog.this);
				Font f = fontChooser.getSelectedFont();
				String s = f.getFontName()+"-"+f.getStyle()+"-"+f.getSize();
				fontButton.setText(s);
			}
		});
		
		setLayout(new BorderLayout());
		add(getMainPanel(mainFrame), BorderLayout.CENTER);
		add(getButtonPanel(mainFrame), BorderLayout.SOUTH);
		setTitle("Edit Preferences");
		setModal(true);
		pack();
	}

	public static void clear() {
		pref.remove(SHOW_ORDERS);
		pref.remove(DEFAULT_BASE_QUANTITY);
		pref.remove(AUTOCLOSE_METHOD);
		pref.remove(AUTOCONNECT);
		pref.remove(CONNECTTO);
		pref.remove(FONT);
		pref.remove(ROW_PADDING);
		// TODO: clear secondary preferences
	}
	
	private JPanel getMainPanel(MainFrame mainFrame) {
		
		JPanel topPanel = new JPanel(new GridBagLayout());
		int gridx, gridy, gridwidth=1, gridheight=1;
		double weightx=1.0, weighty=1.0;
		int anchor=GridBagConstraints.CENTER, fill=GridBagConstraints.BOTH;    
    Insets insets = new Insets(2,2,2,2);
    int ipadx=0, ipady=0;

    /////////
    gridx=0; gridy=0; gridwidth=1; gridheight=1;
		GridBagConstraints gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty, anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Show orders before submitting",SwingConstants.RIGHT),gbc);
    
		boolean b=pref.getBoolean(SHOW_ORDERS, true);
		showOrdersCheckBox.setSelected(b);
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(showOrdersCheckBox, gbc);

		/////////
		gridx=0; gridy=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Default base quantity", SwingConstants.RIGHT) ,gbc);
    
		Integer i=pref.getInt(DEFAULT_BASE_QUANTITY, -100);
		defaultBaseQtySpinner.setValue(i);
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(defaultBaseQtySpinner, gbc);

		/////////
		gridx=0; gridy=2;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("AutoClose Method", SwingConstants.RIGHT) ,gbc);
    
		autoCloseMethodComboBox.setSelectedItem(AutoCloseMethod.valueOf(pref.get(AUTOCLOSE_METHOD, AutoCloseMethod.Stops.name())));
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(autoCloseMethodComboBox, gbc);

    /////////
    gridx=0; gridy=3; gridwidth=1; gridheight=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty, anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Connect to last server on start-up",SwingConstants.RIGHT),gbc);
    
		b=pref.getBoolean(AUTOCONNECT, true);
		autoConnectCheckBox.setSelected(b);
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(autoConnectCheckBox, gbc);

		/////////
		gridx=0; gridy=4;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Connect to ", SwingConstants.RIGHT) ,gbc);
    
		connectToComboBox.setSelectedItem(MainFrame.ConnectedTo.valueOf(pref.get(CONNECTTO, MainFrame.ConnectedTo.Standalone.name())));
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(connectToComboBox, gbc);

		/////////
		gridx=0; gridy=5;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Font", SwingConstants.RIGHT) ,gbc);
    
		Font f = fontChooser.getSelectedFont();
		fontButton.setText(f.getFontName()+"-"+f.getStyle()+"-"+f.getSize());
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(fontButton, gbc);

		/////////
		gridx=0; gridy=6;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(new JLabel("Row padding (pixels)", SwingConstants.RIGHT) ,gbc);
    
	  i=pref.getInt(ROW_PADDING, 2);
		rowPaddingSpinner.setValue(i);
		gridx=1;
		gbc = new GridBagConstraints(gridx,gridy,gridwidth,gridheight,weightx,weighty,anchor,fill,insets,ipadx,ipady);
		topPanel.add(rowPaddingSpinner, gbc);

		return topPanel;
	}

	private JPanel getButtonPanel(final MainFrame mainFrame) {
		JPanel topPanel = new JPanel(new FlowLayout(SwingConstants.RIGHT, 5,5));
		
		JButton okButton = new JButton("OK");
		topPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.cancelled = false;
//TODO: sense whether item changed before deciding to update
				boolean b=showOrdersCheckBox.isSelected();
				pref.putBoolean(SHOW_ORDERS,b);

				int i=(Integer)defaultBaseQtySpinner.getValue();
				pref.putInt(DEFAULT_BASE_QUANTITY,i);

				AutoCloseMethod a = (AutoCloseMethod) autoCloseMethodComboBox.getSelectedItem();
				pref.put(AUTOCLOSE_METHOD,a.name());

				b = autoConnectCheckBox.isSelected();
				pref.putBoolean(AUTOCONNECT,b);

				MainFrame.ConnectedTo c = (MainFrame.ConnectedTo) connectToComboBox.getSelectedItem();
				pref.put(CONNECTTO,c.name());

				Font f = fontChooser.getSelectedFont();
				setAppFont(f);

				i=(Integer)rowPaddingSpinner.getValue();
				pref.putInt(ROW_PADDING,i);
				
				// TODO: move next lines out of this class
				mainFrame.setAppFont(f);
				mainFrame.positionPanel.model.fireTableStructureChanged();
				mainFrame.positionPanel.setCellRenderers();

				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		topPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.cancelled = true;
				dispose();
			}
		});

		return topPanel;
	}

	/////////////////////////////////////////////////////////////////////////
	public static boolean getShowDialog() {
		boolean b=pref.getBoolean(SHOW_ORDERS, true);
		return b;
	}

	public static int getDefaultBaseQuantity() {
		int d=pref.getInt(DEFAULT_BASE_QUANTITY, -100);
		return d;
	}

	private static AutoCloseMethod autoCloseMethod = null;
	public static AutoCloseMethod getAutoCloseMethod() {
		if (autoCloseMethod==null) {;
			String s=pref.get(AUTOCLOSE_METHOD,AutoCloseMethod.None.name());
			autoCloseMethod = AutoCloseMethod.valueOf(s);
		}
		return autoCloseMethod;
	}

	public static void setAutoCloseMethod(AutoCloseMethod method) {
		autoCloseMethod = method;
		pref.put(AUTOCLOSE_METHOD, method.name());
	}
	
	public static boolean getAutoConnect() {
		boolean b=pref.getBoolean(AUTOCONNECT, true);
		return b;
	}

	public static double getDefaultStopsGain() {
		return Double.NaN;
	}

	public static double getDefaultStopsLose() {
		// TODO Auto-generated method stub
		return Double.NaN;
	}

	public static void setConnectTo(ConnectedTo value) {
		pref.put(CONNECTTO, value.name());
	}

	public static ConnectedTo getConnectTo() {
		String s=pref.get(CONNECTTO,MainFrame.ConnectedTo.Standalone.name());
		return MainFrame.ConnectedTo.valueOf(s);
	}

	public static Font getAppFont() {
		Font f = UIManager.getDefaults().getFont("Table.font");
		String s = f.getFontName()+"-"+f.getStyle()+"-"+f.getSize();
		s = pref.get(FONT, s);
		String name = s.split("-", 3)[0];
		int style = Integer.parseInt(s.split("-", 3)[1]);
		int size = Integer.parseInt(s.split("-", 3)[2]);
		f = new Font(name,style,size);
		return f;
	}

	public static int getRowPadding() {
		int d=pref.getInt(ROW_PADDING, 2);
		return d;
	}

	public static void enlargeAppFont() {
		Font f=getAppFont();
		Font newF=new Font(f.getFontName(),f.getStyle(),f.getSize()+1);
		setAppFont(newF);
	}

	public static void setAppFont(Font f) {
		String s=f.getFontName()+"-"+f.getStyle()+"-"+f.getSize();
		pref.put(FONT,s);
	}

	public static void reduceAppFont() {
		Font f=getAppFont();
		int i = f.getSize();
		if (i > 1) i--;
		Font newF=new Font(f.getFontName(),f.getStyle(),i);
		setAppFont(newF);
	}
}

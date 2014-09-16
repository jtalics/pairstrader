/* Copyright (C) 2013 Interactive Brokers LLC. All rights reserved.  This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.jtalics.pairstrader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import TestJavaClient.AlgoParamsDlg;
import TestJavaClient.ComboLegDlg;
import TestJavaClient.IBGridBagPanel;
import TestJavaClient.JFrameFA;
import TestJavaClient.SmartComboRoutingParamsDlg;
import TestJavaClient.UnderCompDlg;

import com.ib.client.Contract;
import com.ib.client.MarketDataType;
import com.ib.client.Order;
import com.ib.client.UnderComp;

public class OrderDialog extends JDialog {

	private final Object itemToOrder;

	public final static String ALL_GENERIC_TICK_TAGS = "100,101,104,105,106,107,165,221,225,233,236,258,293,294,295,318";
	final static int OPERATION_INSERT = 0;
	final static int OPERATION_UPDATE = 1;
	final static int OPERATION_DELETE = 2;

	final static int SIDE_ASK = 0;
	final static int SIDE_BID = 1;

	public boolean m_rc;
	public int orderId;
	public String m_backfillEndTime;
	public String m_backfillDuration;
	public String m_barSizeSetting;
	public int m_useRTH;
	public int m_formatDate;
	public int m_marketDepthRows;
	public String m_whatToShow;
	//public final Contract m_contract;
	public final Order order;
	public UnderComp m_underComp = new UnderComp();
	public int m_exerciseAction;
	public int m_exerciseQuantity;
	public int m_override;
	public int m_marketDataType;

	private JTextField orderIdTextField = new JTextField("?");
	private JTextField backfillEndTimeTextField = new JTextField(22);
	private JTextField backfillDurationTextField = new JTextField("1 M");
	private JTextField barSizeSettingTextField = new JTextField("1 day");
	private JTextField useRthTextField = new JTextField("1");
	private JTextField formatDateTextField = new JTextField("1");
	private JTextField whatToShowTextField = new JTextField("TRADES");
	private JTextField conIdTextField = new JTextField();
	private JTextField symbolTextField = new JTextField("?");
	private JTextField secTypeTextField = new JTextField("STK");
	private JTextField expiryTextField = new JTextField();
	private JTextField strikeTextField = new JTextField("0");
	private JTextField rightTextField = new JTextField();
	private JTextField multiplierTextField = new JTextField("");
	private JTextField exchangeTextField = new JTextField("SMART");
	private JTextField primaryExchTextField = new JTextField("ISLAND");
	private JTextField currencyTextField = new JTextField("USD");
	private JTextField localSymbolTextField = new JTextField();
	private JTextField tradingClassTextField = new JTextField();
	private JTextField includeExpiredTextField = new JTextField("0");
	private JTextField secIdTypeTextField = new JTextField();
	private JTextField secIdTextField = new JTextField();
	private JTextField actionTextField = new JTextField("BUY");
	private JTextField totalQuantityTextField = new JTextField("10");
	private JTextField orderTypeTextField = new JTextField("LMT");
	private JTextField lmtPriceTextField = new JTextField("40");
	private JTextField auxPriceTextField = new JTextField("0");
	private JTextField goodAfterTimeTextField = new JTextField();
	private JTextField goodTillDateTextField = new JTextField();
	private JTextField marketDepthRowTextField = new JTextField("20");
	private JTextField genericTicksTextField = new JTextField(ALL_GENERIC_TICK_TAGS);
	private JCheckBox snapshotMktDataCheckBox = new JCheckBox("Snapshot", false);
	private JTextField exerciseActionTextField = new JTextField("1");
	private JTextField exerciseQuantityTextField = new JTextField("1");
	private JTextField overrideTextField = new JTextField("0");
	private JComboBox<String> marketDataTypeComboBox = new JComboBox<>(MarketDataType.getFields());

	private JFrameFA dialogOwner;

	private String m_faGroup;
	private String m_faProfile;
	private String m_faMethod;
	private String m_faPercentage;
	public String m_genericTicks;
	public boolean isSnapshotMktData;

	private static final int COL1_WIDTH = 30;
	private static final int COL2_WIDTH = 100 - COL1_WIDTH;

	private final GridBagConstraints gbc = new java.awt.GridBagConstraints();

	public OrderDialog(JFrameFA owner, Order order, Object itemToOrder) {
		super(owner, true);

		this.order =order;
		this.itemToOrder = itemToOrder;

		dialogOwner = owner;
		setTitle("<title not set>");

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 1;

		buildOrderIdPanel();


		// create button panel
		buildButtonPanel();
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(buildOrderIdPanel());
		topPanel.add(buildMiddlePanel(order, itemToOrder));

		getContentPane().add(topPanel, BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);

		add(new JScrollPane(topPanel), BorderLayout.CENTER);

		pack();
	}

	private JPanel buildButtonPanel() {
		
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		return buttonPanel;

	}

	private JPanel buildMiddlePanel(Order order, Object itemToOrdr) {
		
		final JPanel pMidPanel = new JPanel();
		pMidPanel.setLayout(new BoxLayout(pMidPanel, BoxLayout.Y_AXIS));
		if (itemToOrdr instanceof Contract) {
			pMidPanel.add(buildContractDetailsPanel((Contract)itemToOrder), BorderLayout.CENTER);
		}

			pMidPanel.add(buildOrderDetailsPanel(order), BorderLayout.CENTER);
		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildMarketDepthPanel(), BorderLayout.CENTER);
		}

		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildMarketDataPanel(), BorderLayout.CENTER);
		}
		
		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildOptionsExercisePanel(), BorderLayout.CENTER);
		}

		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildHistoricalDataPanel(), BorderLayout.CENTER);
		}
		
		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildMarketDataTypePanel(), BorderLayout.CENTER);
		}
		
		if (itemToOrdr instanceof Void) { // TODO
			pMidPanel.add(buildOrderButtonPanel(), BorderLayout.CENTER);
		}
		return pMidPanel;
		
	}

	private Component buildOrderButtonPanel() {
		JPanel pOrderButtonPanel = new JPanel();

		JButton sharesAllocButton = new JButton("FA Allocation Info...");
		sharesAllocButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSharesAlloc();
			}
		});
		pOrderButtonPanel.add(sharesAllocButton);

		JButton comboLegsButton = new JButton("Combo Legs");
		comboLegsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				ComboLegDlg comboLegDlg = new ComboLegDlg(((Contract)itemToOrder).m_comboLegs, 
						order.m_orderComboLegs, exchangeTextField.getText(), OrderDialog.this);
				comboLegDlg.setVisible(true);
			}
		});
		pOrderButtonPanel.add(comboLegsButton);

		
		JButton underCompButton = new JButton("Delta Neutral");
		underCompButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnUnderComp();
			}
		});
		pOrderButtonPanel.add(underCompButton);

		JButton algoParamsButton = new JButton("Algo Params");
		algoParamsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				AlgoParamsDlg algoParamsDlg = new AlgoParamsDlg(order, OrderDialog.this);

				// show delta neutral dialog
				algoParamsDlg.setVisible(true);
			}
		});
		pOrderButtonPanel.add(algoParamsButton);
		
		JButton smartComboRoutingParamsButton = new JButton("Smart Combo Routing Params");
		smartComboRoutingParamsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				SmartComboRoutingParamsDlg smartComboRoutingParamsDlg = new SmartComboRoutingParamsDlg(order, OrderDialog.this);

				// show smart combo routing params dialog
				smartComboRoutingParamsDlg.setVisible(true);
			}
		});
		pOrderButtonPanel.add(smartComboRoutingParamsButton);

		return pOrderButtonPanel;
	}

	private IBGridBagPanel buildOrderIdPanel() {
		
		final IBGridBagPanel pId = new IBGridBagPanel();
		pId.setBorder(BorderFactory.createTitledBorder("Message Id"));
		addGBComponent(pId, new JLabel("Id"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		orderIdTextField.setText(Integer.toString(order.m_orderId));
		addGBComponent(pId, orderIdTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);

		return pId;
	}

	private IBGridBagPanel buildHistoricalDataPanel() {
		final IBGridBagPanel pBackfill = new IBGridBagPanel();

		pBackfill.setBorder(BorderFactory.createTitledBorder("Historical Data Query"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateTime = "" + gc.get(Calendar.YEAR) + pad(gc.get(Calendar.MONTH) + 1) + pad(gc.get(Calendar.DAY_OF_MONTH)) + " " + pad(gc.get(Calendar.HOUR_OF_DAY)) + ":" + pad(gc.get(Calendar.MINUTE)) + ":" + pad(gc.get(Calendar.SECOND)) + " " + gc.getTimeZone().getDisplayName(false, TimeZone.SHORT);

		backfillEndTimeTextField.setText(dateTime);
		addGBComponent(pBackfill, new JLabel("End Date/Time"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, backfillEndTimeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pBackfill, new JLabel("Duration"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, backfillDurationTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pBackfill, new JLabel("Bar Size Setting (1 to 11)"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, barSizeSettingTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pBackfill, new JLabel("What to Show"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, whatToShowTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pBackfill, new JLabel("Regular Trading Hours (1 or 0)"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, useRthTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pBackfill, new JLabel("Date Format Style (1 or 2)"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pBackfill, formatDateTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		return pBackfill;
	}

	private IBGridBagPanel buildMarketDataPanel() {
		final IBGridBagPanel pMarketData = new IBGridBagPanel();
		pMarketData.setBorder(BorderFactory.createTitledBorder("Market Data"));
		addGBComponent(pMarketData, new JLabel("Generic Tick Tags"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pMarketData, genericTicksTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pMarketData, snapshotMktDataCheckBox, gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		return pMarketData;
	}

	private IBGridBagPanel buildMarketDataTypePanel() {
		
		final IBGridBagPanel pMarketDataType = new IBGridBagPanel();
		pMarketDataType.setBorder(BorderFactory.createTitledBorder("Market Data Type"));
		addGBComponent(pMarketDataType, new JLabel("Market Data Type"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pMarketDataType, marketDataTypeComboBox, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		return pMarketDataType;
	}

	private IBGridBagPanel buildOptionsExercisePanel() {
		final IBGridBagPanel pOptionsExercise = new IBGridBagPanel();

		pOptionsExercise.setBorder(BorderFactory.createTitledBorder("Options Exercise"));
		addGBComponent(pOptionsExercise, new JLabel("Action (1 or 2)"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOptionsExercise, exerciseActionTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOptionsExercise, new JLabel("Number of Contracts"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOptionsExercise, exerciseQuantityTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOptionsExercise, new JLabel("Override (0 or 1)"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOptionsExercise, overrideTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);

		return pOptionsExercise;
	}

	private IBGridBagPanel buildMarketDepthPanel() {

		final IBGridBagPanel pMarketDepth = new IBGridBagPanel();
		pMarketDepth.setBorder(BorderFactory.createTitledBorder("Market Depth"));
		addGBComponent(pMarketDepth, new JLabel("Number of Rows"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pMarketDepth, marketDepthRowTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		return pMarketDepth;
	}

	private IBGridBagPanel buildOrderDetailsPanel(Order order) {
		
		final IBGridBagPanel pOrderDetails = new IBGridBagPanel();

		pOrderDetails.setBorder(BorderFactory.createTitledBorder("Order Info"));
		addGBComponent(pOrderDetails, new JLabel("Action"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, actionTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Total Order Size"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, totalQuantityTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Order Type"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, orderTypeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Lmt Price / Option Price / Volatility"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, lmtPriceTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Aux Price / Underlying Price"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, auxPriceTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Good After Time"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, goodAfterTimeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(pOrderDetails, new JLabel("Good Till Date"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(pOrderDetails, goodTillDateTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);

		actionTextField.setText(order.m_action);
		totalQuantityTextField.setText(Integer.toString(order.m_totalQuantity));
		orderTypeTextField.setText(order.m_orderType);
		lmtPriceTextField.setText(Double.toString(order.m_lmtPrice));
		auxPriceTextField.setText(Double.toString(order.m_auxPrice));
		goodAfterTimeTextField.setText(order.m_goodAfterTime);
		goodTillDateTextField.setText(order.m_goodTillDate);
		
		return pOrderDetails;
	}

	private IBGridBagPanel buildContractDetailsPanel(final Contract contract) {

		final IBGridBagPanel p = new IBGridBagPanel();
		p.setBorder(BorderFactory.createTitledBorder("Contract Info"));
		addGBComponent(p, new JLabel("Contract Id"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, conIdTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Symbol"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, symbolTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Security Type"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, secTypeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Expiry"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, expiryTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Strike"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, strikeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Put/Call"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, rightTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Option Multiplier"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, multiplierTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Exchange"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, exchangeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Primary Exchange"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, primaryExchTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Currency"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, currencyTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Local Symbol"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, localSymbolTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Trading Class"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, tradingClassTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Include Expired"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, includeExpiredTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Sec Id Type"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, secIdTypeTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		addGBComponent(p, new JLabel("Sec Id"), gbc, COL1_WIDTH, GridBagConstraints.RELATIVE);
		addGBComponent(p, secIdTextField, gbc, COL2_WIDTH, GridBagConstraints.REMAINDER);
		conIdTextField.setText(Integer.toString(contract.m_conId));
		symbolTextField.setText(contract.m_symbol);
		secTypeTextField.setText(contract.m_secType);
		expiryTextField.setText(contract.m_expiry);
		strikeTextField.setText(Double.toString(contract.m_strike));
		rightTextField.setText(contract.m_right);
		multiplierTextField.setText(contract.m_multiplier);
		exchangeTextField.setText(contract.m_exchange);
		primaryExchTextField.setText(contract.m_primaryExch);
		currencyTextField.setText(contract.m_currency);
		localSymbolTextField.setText(contract.m_localSymbol);
		tradingClassTextField.setText(contract.m_tradingClass);
		includeExpiredTextField.setText(Boolean.toString(contract.m_includeExpired));
		secIdTypeTextField.setText(contract.m_secIdType);
		secIdTextField.setText(contract.m_secId);
		return p;
	}

	private static String pad(int val) {
		return val < 10 ? "0" + val : "" + val;
	}

	void onSharesAlloc() {
		if (!dialogOwner.m_bIsFAAccount) {
			return;
		}

		throw new RuntimeException();
		// TODO: remove comment next line
		// FAAllocationInfoDlg dlg = new FAAllocationInfoDlg(this);

		// show the combo leg dialog
		// dlg.setVisible(true);
	}

	void onBtnUnderComp() {

		UnderCompDlg underCompDlg = new UnderCompDlg(m_underComp, this);

		// show delta neutral dialog
		underCompDlg.setVisible(true);
		if (underCompDlg.ok()) {
			((Contract)itemToOrder).m_underComp = m_underComp;
		}
		else if (underCompDlg.reset()) {
			((Contract)itemToOrder).m_underComp = null;
		}
	}

	void onOk() {
		m_rc = false;

		try {
			// set id
			orderId = Integer.parseInt(orderIdTextField.getText());

			if (itemToOrder instanceof Contract) {
				// set contract fields
				Contract c = (Contract) itemToOrder;
				c.m_conId = ParseInt(conIdTextField.getText(), 0);
				c.m_symbol = symbolTextField.getText();
				c.m_secType = secTypeTextField.getText();
				c.m_expiry = expiryTextField.getText();
				c.m_strike = ParseDouble(strikeTextField.getText(), 0.0);
				c.m_right = rightTextField.getText();
				c.m_multiplier = multiplierTextField.getText();
				c.m_exchange = exchangeTextField.getText();
				c.m_primaryExch = primaryExchTextField.getText();
				c.m_currency = currencyTextField.getText();
				c.m_localSymbol = localSymbolTextField.getText();
				c.m_tradingClass = tradingClassTextField.getText();
				try {
					int includeExpired = Integer.parseInt(includeExpiredTextField.getText());
					c.m_includeExpired = (includeExpired == 1);
				}
				catch (NumberFormatException ex) {
					c.m_includeExpired = false;
				}
				c.m_secIdType = secIdTypeTextField.getText();
				c.m_secId = secIdTextField.getText();
			}

			// set order fields
			order.m_action = actionTextField.getText();
			order.m_totalQuantity = Integer.parseInt(totalQuantityTextField.getText());
			order.m_orderType = orderTypeTextField.getText();
			order.m_lmtPrice = parseStringToMaxDouble(lmtPriceTextField.getText());
			order.m_auxPrice = parseStringToMaxDouble(auxPriceTextField.getText());
			order.m_goodAfterTime = goodAfterTimeTextField.getText();
			order.m_goodTillDate = goodTillDateTextField.getText();

			order.m_faGroup = m_faGroup;
			order.m_faProfile = m_faProfile;
			order.m_faMethod = m_faMethod;
			order.m_faPercentage = m_faPercentage;

			// set historical data fields
			m_backfillEndTime = backfillEndTimeTextField.getText();
			m_backfillDuration = backfillDurationTextField.getText();
			m_barSizeSetting = barSizeSettingTextField.getText();
			m_useRTH = Integer.parseInt(useRthTextField.getText());
			m_whatToShow = whatToShowTextField.getText();
			m_formatDate = Integer.parseInt(formatDateTextField.getText());
			m_exerciseAction = Integer.parseInt(exerciseActionTextField.getText());
			m_exerciseQuantity = Integer.parseInt(exerciseQuantityTextField.getText());
			m_override = Integer.parseInt(overrideTextField.getText());

			// set market depth rows
			m_marketDepthRows = Integer.parseInt(marketDepthRowTextField.getText());
			m_genericTicks = genericTicksTextField.getText();
			isSnapshotMktData = snapshotMktDataCheckBox.isSelected();

			m_marketDataType = marketDataTypeComboBox.getSelectedIndex() + 1;
		}
		catch (Exception e) {
			Main.inform(this, "Error - " + e);
			return;
		}

		m_rc = true;
		setVisible(false);
	}

	void onCancel() {
		m_rc = false;
		setVisible(false);
	}

	@Override
	public void show() {
		m_rc = false;
		super.show();
	}

	public void setIdAtLeast(int id) {
		try {
			// set id field to at least id
			int curId = Integer.parseInt(orderIdTextField.getText());
			if (curId < id) {
				orderIdTextField.setText(String.valueOf(id));
			}
		}
		catch (Exception e) {
			Main.inform(this, "Error - " + e);
		}
	}

	private static int ParseInt(String text, int defValue) {
		try {
			return Integer.parseInt(text);
		}
		catch (NumberFormatException e) {
			return defValue;
		}
	}

	private static double ParseDouble(String text, double defValue) {
		try {
			return Double.parseDouble(text);
		}
		catch (NumberFormatException e) {
			return defValue;
		}
	}

	private static double parseStringToMaxDouble(String value) {
		if (value.trim().length() == 0) {
			return Double.MAX_VALUE;
		}
		return Double.parseDouble(value);
	}
	

	public void faGroup(String s) {
		m_faGroup = s;
	}

	public void faProfile(String s) {
		m_faProfile = s;
	}

	public void faMethod(String s) {
		m_faMethod = s;
	}

	public void faPercentage(String s) {
		m_faPercentage = s;
	}

	private static void addGBComponent(IBGridBagPanel panel, Component comp, GridBagConstraints gbc, int weightx, int gridwidth) {
		gbc.weightx = weightx;
		gbc.gridwidth = gridwidth;
		panel.setConstraints(comp, gbc);
		panel.add(comp, gbc);
	}
}

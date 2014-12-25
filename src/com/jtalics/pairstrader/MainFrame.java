package com.jtalics.pairstrader;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import TestJavaClient.AccountDlg;
import TestJavaClient.FinancialAdvisorDlg;
import TestJavaClient.IBTextPanel;
import TestJavaClient.JFrameFA;
import TestJavaClient.Main;
import TestJavaClient.MktDepthDlg;
import TestJavaClient.OrderDlg;

import com.ib.client.AnyWrapperMsgGenerator;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.TickType;
import com.ib.client.UnderComp;
import com.jtalics.pairstrader.MarketDataListener.MdPriceTickEvent;
import com.jtalics.pairstrader.events.AccountDownloadEndEvent;
import com.jtalics.pairstrader.events.AccountSummaryEndEvent;
import com.jtalics.pairstrader.events.AccountSummaryEvent;
import com.jtalics.pairstrader.events.BondContractDetailsEvent;
import com.jtalics.pairstrader.events.CommissionReportEvent;
import com.jtalics.pairstrader.events.ContractDetailsEndEvent;
import com.jtalics.pairstrader.events.ContractDetailsEvent;
import com.jtalics.pairstrader.events.CurrentTimeEvent;
import com.jtalics.pairstrader.events.DeltaNeutralValidationEvent;
import com.jtalics.pairstrader.events.DisplayXMLEvent;
import com.jtalics.pairstrader.events.ExecDetailsEndEvent;
import com.jtalics.pairstrader.events.ExecDetailsEvent;
import com.jtalics.pairstrader.events.ManagedAccountsEvent;
import com.jtalics.pairstrader.events.OpenOrderEndEvent;
import com.jtalics.pairstrader.events.OpenOrderEvent;
import com.jtalics.pairstrader.events.OrderStatusEvent;
import com.jtalics.pairstrader.events.PositionEndEvent;
import com.jtalics.pairstrader.events.PositionEvent;
import com.jtalics.pairstrader.events.ValidIdEvent;
import com.jtalics.pairstrader.pairs.PairPanel;
import com.jtalics.pairstrader.server.MyServer;
import com.jtalics.pairstrader.server.OrderSimulator;
import com.jtalics.pairstrader.server.ServerListener;
import com.jtalics.pairstrader.server.ServerPanel;
import com.jtalics.pairstrader.server.TWSAPISimulator;
import com.jtalics.pairstrader.stocks.SecurityPanel;
import com.jtalics.pairstrader.stocks.SecurityTableRow;
import com.jtalics.pairstrader.trades.PositionPanel;
import com.jtalics.pairstrader.util.FileChooser;

public class MainFrame extends JFrameFA implements EWrapper, Outputable, ComponentListener, WindowListener {
	public static final DecimalFormat df = new DecimalFormat("0.00");

	private static final int NOT_AN_FA_ACCOUNT_ERROR = 321;
	private int faErrorCodes[] = { 503, 504, 505, 522, 1100, NOT_AN_FA_ACCOUNT_ERROR };
	private boolean faError;

	private EClientSocket m_client = new EClientSocket(this);
	public final IBTextPanel m_tickers = new IBTextPanel("Market and Historical Data", false);
	public final IBTextPanel m_TWS = new IBTextPanel("TWS Server Responses", false);
	public final IBTextPanel m_errors = new IBTextPanel("Errors and Messages", false);
	private OrderDlg m_orderDlg = new OrderDlg(this);
	private AccountDlg m_acctDlg = new AccountDlg(this);
	private HashMap<Integer, MktDepthDlg> m_mapRequestToMktDepthDlg = new HashMap<>();
	String faGroupXML;
	String faProfilesXML;
	String faAliasesXML;
	public String m_FAAcctCodes;
	private boolean m_disconnectInProgress = false;

	public final SecurityPanel securityPanel;
	public final PairPanel pairsPanel;
	public final PositionPanel positionPanel;
	private List<ServerErrorListener> errorListeners = new ArrayList<>();
	private List<MarketDataListener> marketDataListeners = new ArrayList<>();
	private List<ServerListener> serverListeners = new ArrayList<>();
	public int nextValidTickerId = 1;
	public int nextValidOrderId = 1;
	protected int nextValidReqId = 1;
	private final LinkedBlockingQueue<Runnable> twsOrderQueue = new LinkedBlockingQueue<>();
	static public enum ConnectedTo {
		Standalone, TWS, Simulator
	}
	public ConnectedTo connectedTo = ConnectedTo.Standalone;
	public TWSAPISimulator simulator;
	private ServerPanel serverPanel = new ServerPanel(this);
	
	private final Map<Contract,List<Integer>> contractToTickerIds = new ConcurrentHashMap<>();
	private final Map<Integer,Contract> tickerIdToContract = new ConcurrentHashMap<>();

	boolean debug = false;
	public static Preferences prefMainFrame=PreferencesDialog.pref.node("MAIN_FRAME");
	private final String KEY4="width";
	private final String KEY5="height";

	private boolean fillPartially=true; // fill an order a little at a time
	private boolean dropOrders=false;  // kill an order once in a while
	private boolean dropMessages=false; // drop a message once in a while
	private boolean marketDataFeed = false; // "firehose" feed in standalone for testing
	private static final FileChooser fileChooser = new FileChooser();

	Random random = new Random(0);

	public MainFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		m_tickers.setVisible(debug);
		try {
			connectedTo = PreferencesDialog.getConnectTo();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		JPanel top=new JPanel(new BorderLayout());
		add(top);
		addKeyBindings(top);
		setIcon();

		getRootPane().setJMenuBar(new MenuBar(this, m_client, m_TWS));
		final Preferences prefDividerLoc = prefMainFrame.node("DIVIDER_LOC");
		// p1 = tablesOutputSplitPane
		final JSplitPane p1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final String KEY1="tablesOutputSplitPane";
		p1.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String propertyName = pce.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
          int current = p1.getDividerLocation();
    			prefDividerLoc.putInt(KEY1,current);
				}				
			}
		});

		p1.setDividerLocation(prefDividerLoc.getInt(KEY1,400));
		p1.setRightComponent(serverPanel);
		p1.setOneTouchExpandable(true);
		top.add(p1, BorderLayout.CENTER);

		/// p2 = secSplitPane
		final JSplitPane p2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final String KEY2 = "secSplitPane";
		p2.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String propertyName = pce.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
          int current = p2.getDividerLocation();
    			prefDividerLoc.putInt(KEY2,current);
				}				
			}
		});
		p2.setDividerLocation(prefDividerLoc.getInt(KEY2,200));

		p1.setLeftComponent(p2);
		p2.setLeftComponent(securityPanel = new SecurityPanel(this));

		// p3 = virtPosSplitPane
		final JSplitPane p3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final String KEY3 = "virtPosSplitPane";
		p3.setDividerLocation(prefDividerLoc.getInt(KEY3,300));
		p3.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				String propertyName = pce.getPropertyName();
				if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
          int current = p3.getDividerLocation();
    			prefDividerLoc.putInt(KEY3,current);
				}				
			}
			});
		p2.setRightComponent(p3);

		p3.setLeftComponent(pairsPanel = new PairPanel(this));
		p3.setRightComponent(positionPanel = new PositionPanel(this));
		
		setSize(prefMainFrame.getInt(KEY4, 1000), prefMainFrame.getInt(KEY5, 600));
		setAppFont(PreferencesDialog.getAppFont());
		addComponentListener(this);

		new Thread("TWS ORDER QUEUE") {
			// IB TWS gets confused if blasted with many threads placing orders
			// as would occur in a panic auto-close.
			// Doesn't appear that they have a thread safe orderId 
			@Override
			public void run() {
				while (true) {
					try {
						twsOrderQueue.take().run();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private void addKeyBindings(JComponent top) {
		String enlarge="enlarge-font";
		top.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK|InputEvent.SHIFT_DOWN_MASK), enlarge);
		top.getActionMap().put(enlarge,new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.enlargeAppFont();
				if (debug) println("Reduce font by one pixel.");
				setAppFont(PreferencesDialog.getAppFont());
			}
			
		});
		String reduce="reduce-font";
		top.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK), reduce);
		top.getActionMap().put(reduce,new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.reduceAppFont();
				if (debug) println("Enlarge font by one pixel.");
				setAppFont(PreferencesDialog.getAppFont());
			}
			
		});
	}

	private void setIcon() {
		URL iconURL = getClass().getResource(/* "/some/package/favicon.png" */"arrows.gif");
		// iconURL is null when not found
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());
	}

	@Override
	public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
		// received price tick
		String msg = EWrapperMsgGenerator.tickPrice(tickerId, field, price, canAutoExecute);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
		fireMarketData(this, tickerId, field, price, canAutoExecute);
	}

	@Override
	public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
		// received computation tick
		String msg = EWrapperMsgGenerator.tickOptionComputation(tickerId, field, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void tickSize(int tickerId, int field, int size) {
		// received size tick
		String msg = EWrapperMsgGenerator.tickSize(tickerId, field, size);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		// received generic tick
		String msg = EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		// received String tick
		String msg = EWrapperMsgGenerator.tickString(tickerId, tickType, value);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void tickSnapshotEnd(int tickerId) {
		String msg = EWrapperMsgGenerator.tickSnapshotEnd(tickerId);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {
		// received EFP tick
		String msg = EWrapperMsgGenerator.tickEFP(tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureExpiry, dividendImpact, dividendsToExpiry);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		// received order status
		String msg = EWrapperMsgGenerator.orderStatus(orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);

		// make sure id for next order is at least orderId+1
		m_orderDlg.setIdAtLeast(orderId + 1);
		fireServerEvent(new OrderStatusEvent(this, orderId, status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld));
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
		// received open order
		String msg = EWrapperMsgGenerator.openOrder(orderId, contract, order, orderState);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new OpenOrderEvent(this, orderId, contract, order, orderState));
	}

	@Override
	public void openOrderEnd() {
		// received open order end
		String msg = EWrapperMsgGenerator.openOrderEnd();
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new OpenOrderEndEvent(this));
	}

	@Override
	public void contractDetails(int reqId, ContractDetails contractDetails) {
		String msg = EWrapperMsgGenerator.contractDetails(reqId, contractDetails);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new ContractDetailsEvent(this, reqId, contractDetails));
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		String msg = EWrapperMsgGenerator.contractDetailsEnd(reqId);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new ContractDetailsEndEvent(this, reqId));
	}

	@Override
	public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
		String msg = EWrapperMsgGenerator.scannerData(reqId, rank, contractDetails, distance, benchmark, projection, legsStr);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void scannerDataEnd(int reqId) {
		String msg = EWrapperMsgGenerator.scannerDataEnd(reqId);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails contractDetails) {
		String msg = EWrapperMsgGenerator.bondContractDetails(reqId, contractDetails);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new BondContractDetailsEvent(this, reqId, contractDetails));
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		String msg = EWrapperMsgGenerator.execDetails(reqId, contract, execution);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new ExecDetailsEvent(this, reqId, contract, execution));
	}

	@Override
	public void execDetailsEnd(int reqId) {
		String msg = EWrapperMsgGenerator.execDetailsEnd(reqId);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new ExecDetailsEndEvent(this, reqId));
	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {

		MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(tickerId);
		if (depthDialog != null) {
			depthDialog.updateMktDepth(tickerId, position, "", operation, side, price, size);
		}
		else {
			new Exception("cannot find dialog that corresponds to request id [" + tickerId + "]").printStackTrace();
		}
	}

	@Override
	public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {
		MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(tickerId);
		if (depthDialog != null) {
			depthDialog.updateMktDepth(tickerId, position, marketMaker, operation, side, price, size);
		}
		else {
			new Exception("cannot find dialog that corresponds to request id [" + tickerId + "]").printStackTrace();
		}
	}

	@Override
	public void nextValidId(int orderId) {
		// received next valid order id
		String msg = EWrapperMsgGenerator.nextValidId(orderId);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		m_orderDlg.setIdAtLeast(orderId);
		fireServerEvent(new ValidIdEvent(this, orderId));
	}

	@Override
	public void error(Exception ex) {
		// do not report exceptions if we initiated disconnect
		if (!isDisconnectInProgress()) {
			String msg = AnyWrapperMsgGenerator.error(ex);
			Main.inform(this, msg);
		}
	}

	@Override
	public void error(String str) {
		String msg = AnyWrapperMsgGenerator.error(str);
		if (connectedTo == ConnectedTo.TWS) m_errors.add(msg);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		// received error
		String msg = AnyWrapperMsgGenerator.error(id, errorCode, errorMsg);
		if (connectedTo == ConnectedTo.TWS) m_errors.add(msg);
		for (int ctr = 0; ctr < faErrorCodes.length; ctr++) {
			faError |= (errorCode == faErrorCodes[ctr]);
		}
		if (errorCode == MktDepthDlg.MKT_DEPTH_DATA_RESET) {

			MktDepthDlg depthDialog = m_mapRequestToMktDepthDlg.get(id);
			if (depthDialog != null) {
				depthDialog.reset();
			}
			else {
				new Exception("cannot find dialog that corresponds to request id [" + id + "]").printStackTrace();
			}
		}
		fireErrorEvent(new ServerErrorListener.ErrorEvent(this, id, errorCode, errorMsg));
	}

	@Override
	public void connectionClosed() {
		String msg = AnyWrapperMsgGenerator.connectionClosed();
		Main.inform(this, msg);
	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		m_acctDlg.updateAccountValue(key, value, currency, accountName);
	}

	@Override
	public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
		m_acctDlg.updatePortfolio(contract, position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName);
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		m_acctDlg.updateAccountTime(timeStamp);
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		m_acctDlg.accountDownloadEnd(accountName);

		String msg = EWrapperMsgGenerator.accountDownloadEnd(accountName);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new AccountDownloadEndEvent(this, accountName));
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
		String msg = EWrapperMsgGenerator.updateNewsBulletin(msgId, msgType, message, origExchange);
		JOptionPane.showMessageDialog(this, msg, "IB News Bulletin", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void managedAccounts(String accountsList) {
		m_bIsFAAccount = true;
		m_FAAcctCodes = accountsList;
		String msg = EWrapperMsgGenerator.managedAccounts(accountsList);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new ManagedAccountsEvent(this, accountsList));
	}

	@Override
	public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {
		String msg = EWrapperMsgGenerator.historicalData(reqId, date, open, high, low, close, volume, count, WAP, hasGaps);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {
		String msg = EWrapperMsgGenerator.realtimeBar(reqId, time, open, high, low, close, volume, wap, count);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void scannerParameters(String xml) {
		displayXML(EWrapperMsgGenerator.SCANNER_PARAMETERS, xml);
	}

	@Override
	public void currentTime(long time) {
		String msg = EWrapperMsgGenerator.currentTime(time);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new CurrentTimeEvent(this, time));
	}

	@Override
	public void fundamentalData(int reqId, String data) {
		String msg = EWrapperMsgGenerator.fundamentalData(reqId, data);
		if (connectedTo == ConnectedTo.TWS  && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
		String msg = EWrapperMsgGenerator.deltaNeutralValidation(reqId, underComp);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new DeltaNeutralValidationEvent(this, reqId, underComp));
	}

	private void displayXML(String title, String xml) {
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(title);
		if (connectedTo == ConnectedTo.TWS) m_TWS.addText(xml);
		fireServerEvent(new DisplayXMLEvent(this, title, xml));
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
		displayXML(EWrapperMsgGenerator.FINANCIAL_ADVISOR + " " + EClientSocket.faMsgTypeName(faDataType), xml);
		switch (faDataType) {
		case EClientSocket.GROUPS:
			faGroupXML = xml;
			break;
		case EClientSocket.PROFILES:
			faProfilesXML = xml;
			break;
		case EClientSocket.ALIASES:
			faAliasesXML = xml;
			break;
		}

		if (!faError && !(faGroupXML == null || faProfilesXML == null || faAliasesXML == null)) {
			FinancialAdvisorDlg dlg = new FinancialAdvisorDlg(this);
			dlg.receiveInitialXML(faGroupXML, faProfilesXML, faAliasesXML);
			dlg.setVisible(true);

			if (!dlg.isRc()) {
				return;
			}

			m_client.replaceFA(EClientSocket.GROUPS, dlg.groupsXML);
			m_client.replaceFA(EClientSocket.PROFILES, dlg.profilesXML);
			m_client.replaceFA(EClientSocket.ALIASES, dlg.aliasesXML);

		}
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		String msg = EWrapperMsgGenerator.marketDataType(reqId, marketDataType);
		if (connectedTo == ConnectedTo.TWS && m_tickers.isVisible()) m_tickers.add(msg);
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		String msg = EWrapperMsgGenerator.commissionReport(commissionReport);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new CommissionReportEvent(this, commissionReport));
	}

	private static void copyExtendedOrderDetails(Order destOrder, Order srcOrder) {
		destOrder.m_tif = srcOrder.m_tif;
		destOrder.m_activeStartTime = srcOrder.m_activeStartTime;
		destOrder.m_activeStopTime = srcOrder.m_activeStopTime;
		destOrder.m_ocaGroup = srcOrder.m_ocaGroup;
		destOrder.m_ocaType = srcOrder.m_ocaType;
		destOrder.m_openClose = srcOrder.m_openClose;
		destOrder.m_origin = srcOrder.m_origin;
		destOrder.m_orderRef = srcOrder.m_orderRef;
		destOrder.m_transmit = srcOrder.m_transmit;
		destOrder.m_parentId = srcOrder.m_parentId;
		destOrder.m_blockOrder = srcOrder.m_blockOrder;
		destOrder.m_sweepToFill = srcOrder.m_sweepToFill;
		destOrder.m_displaySize = srcOrder.m_displaySize;
		destOrder.m_triggerMethod = srcOrder.m_triggerMethod;
		destOrder.m_outsideRth = srcOrder.m_outsideRth;
		destOrder.m_hidden = srcOrder.m_hidden;
		destOrder.m_discretionaryAmt = srcOrder.m_discretionaryAmt;
		destOrder.m_goodAfterTime = srcOrder.m_goodAfterTime;
		destOrder.m_shortSaleSlot = srcOrder.m_shortSaleSlot;
		destOrder.m_designatedLocation = srcOrder.m_designatedLocation;
		destOrder.m_exemptCode = srcOrder.m_exemptCode;
		destOrder.m_ocaType = srcOrder.m_ocaType;
		destOrder.m_rule80A = srcOrder.m_rule80A;
		destOrder.m_allOrNone = srcOrder.m_allOrNone;
		destOrder.m_minQty = srcOrder.m_minQty;
		destOrder.m_percentOffset = srcOrder.m_percentOffset;
		destOrder.m_eTradeOnly = srcOrder.m_eTradeOnly;
		destOrder.m_firmQuoteOnly = srcOrder.m_firmQuoteOnly;
		destOrder.m_nbboPriceCap = srcOrder.m_nbboPriceCap;
		destOrder.m_optOutSmartRouting = srcOrder.m_optOutSmartRouting;
		destOrder.m_auctionStrategy = srcOrder.m_auctionStrategy;
		destOrder.m_startingPrice = srcOrder.m_startingPrice;
		destOrder.m_stockRefPrice = srcOrder.m_stockRefPrice;
		destOrder.m_delta = srcOrder.m_delta;
		destOrder.m_stockRangeLower = srcOrder.m_stockRangeLower;
		destOrder.m_stockRangeUpper = srcOrder.m_stockRangeUpper;
		destOrder.m_overridePercentageConstraints = srcOrder.m_overridePercentageConstraints;
		destOrder.m_volatility = srcOrder.m_volatility;
		destOrder.m_volatilityType = srcOrder.m_volatilityType;
		destOrder.m_deltaNeutralOrderType = srcOrder.m_deltaNeutralOrderType;
		destOrder.m_deltaNeutralAuxPrice = srcOrder.m_deltaNeutralAuxPrice;
		destOrder.m_deltaNeutralConId = srcOrder.m_deltaNeutralConId;
		destOrder.m_deltaNeutralSettlingFirm = srcOrder.m_deltaNeutralSettlingFirm;
		destOrder.m_deltaNeutralClearingAccount = srcOrder.m_deltaNeutralClearingAccount;
		destOrder.m_deltaNeutralClearingIntent = srcOrder.m_deltaNeutralClearingIntent;
		destOrder.m_deltaNeutralOpenClose = srcOrder.m_deltaNeutralOpenClose;
		destOrder.m_deltaNeutralShortSale = srcOrder.m_deltaNeutralShortSale;
		destOrder.m_deltaNeutralShortSaleSlot = srcOrder.m_deltaNeutralShortSaleSlot;
		destOrder.m_deltaNeutralDesignatedLocation = srcOrder.m_deltaNeutralDesignatedLocation;
		destOrder.m_continuousUpdate = srcOrder.m_continuousUpdate;
		destOrder.m_referencePriceType = srcOrder.m_referencePriceType;
		destOrder.m_trailStopPrice = srcOrder.m_trailStopPrice;
		destOrder.m_trailingPercent = srcOrder.m_trailingPercent;
		destOrder.m_scaleInitLevelSize = srcOrder.m_scaleInitLevelSize;
		destOrder.m_scaleSubsLevelSize = srcOrder.m_scaleSubsLevelSize;
		destOrder.m_scalePriceIncrement = srcOrder.m_scalePriceIncrement;
		destOrder.m_scalePriceAdjustValue = srcOrder.m_scalePriceAdjustValue;
		destOrder.m_scalePriceAdjustInterval = srcOrder.m_scalePriceAdjustInterval;
		destOrder.m_scaleProfitOffset = srcOrder.m_scaleProfitOffset;
		destOrder.m_scaleAutoReset = srcOrder.m_scaleAutoReset;
		destOrder.m_scaleInitPosition = srcOrder.m_scaleInitPosition;
		destOrder.m_scaleInitFillQty = srcOrder.m_scaleInitFillQty;
		destOrder.m_scaleRandomPercent = srcOrder.m_scaleRandomPercent;
		destOrder.m_scaleTable = srcOrder.m_scaleTable;
		destOrder.m_hedgeType = srcOrder.m_hedgeType;
		destOrder.m_hedgeParam = srcOrder.m_hedgeParam;
		destOrder.m_account = srcOrder.m_account;
		destOrder.m_settlingFirm = srcOrder.m_settlingFirm;
		destOrder.m_clearingAccount = srcOrder.m_clearingAccount;
		destOrder.m_clearingIntent = srcOrder.m_clearingIntent;
	}

	@Override
	public void position(String account, Contract contract, int pos, double avgCost) {
		String msg = EWrapperMsgGenerator.position(account, contract, pos, avgCost);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new PositionEvent(this, account, contract, pos, avgCost));
	}

	@Override
	public void positionEnd() {
		String msg = EWrapperMsgGenerator.positionEnd();
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new PositionEndEvent(this));
	}

	@Override
	public void accountSummary(int reqId, String account, String tag, String value, String currency) {
		String msg = EWrapperMsgGenerator.accountSummary(reqId, account, tag, value, currency);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new AccountSummaryEvent(this, reqId, account, tag, value, currency));
	}

	@Override
	public void accountSummaryEnd(int reqId) {
		String msg = EWrapperMsgGenerator.accountSummaryEnd(reqId);
		if (connectedTo == ConnectedTo.TWS) m_TWS.add(msg);
		fireServerEvent(new AccountSummaryEndEvent(this, reqId));
	}

	public boolean isDisconnectInProgress() {
		return m_disconnectInProgress;
	}

	public void setDisconnectInProgress(boolean m_disconnectInProgress) {
		this.m_disconnectInProgress = m_disconnectInProgress;
	}

	// ////////

	public void addServerErrorListener(ServerErrorListener el) {
		errorListeners.add(el);
	}

	public void removeServerErrorListener(ServerErrorListener el) {
		errorListeners.remove(el);
	}

	public void fireErrorEvent(ServerErrorListener.ErrorEvent ee) {
		for (ServerErrorListener el : errorListeners) {
			el.onServerErrorEvent(ee);
		}
	}

	// /////////

	public void addServerListener(ServerListener vil) {
		// println("ADDED VI LISTENER"+vil);
		serverListeners.add(vil);
	}

	public void removeServerListener(ServerListener vil) {
		// println("REMOVED VI LISTENER"+vil);
		serverListeners.remove(vil);
	}

	public void fireServerEvent(ServerListener.ServerEvent serverEvent) {
		try {
			// println("FIRED VI LISTENER" + ibValidId +
			// "on "+Thread.currentThread());
			for (ServerListener twsListener : serverListeners) {
				if (dropMessages && connectedTo==ConnectedTo.Standalone && random.nextDouble()<0.05) {
					System.out.println("DROPPED MESSAGE: "+serverEvent.getClass().getSimpleName()+": "+serverEvent.toString());
					continue;
				}
				twsListener.onServerEvent(serverEvent);
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// ////////

	public void addMarketDataListener(MarketDataListener mdl) {
		marketDataListeners.add(mdl);
	}

	public void removeMarketDataListener(MarketDataListener mdl) {
		marketDataListeners.remove(mdl);
	}

	public void fireMarketData(Object source, int tickerId, int field, Double price, int canAutoExecute) {
		MdPriceTickEvent mde = new MdPriceTickEvent(source, tickerId, field, price, canAutoExecute);
		for (MarketDataListener mdl : marketDataListeners) {
			mdl.onMdPriceTickEvent(mde);
		}
	}

	public void reqMktData(final int tickerId, final Contract contract, final String genericTicks, final boolean snapshotMktData) {

		switch (connectedTo) {
		case Standalone:
			tickerIdToContract.put(tickerId, contract);
			List<Integer> tickerIds = contractToTickerIds.get(contract);
			if (tickerIds == null) {
				tickerIds = new ArrayList<Integer>();
				tickerIds.add(tickerId);
				contractToTickerIds.put(contract,tickerIds);
			}
			else {
				synchronized(tickerIds) {
//System.out.println("adding(2) on "+Thread.currentThread().getName());
					tickerIds.add(tickerId); // TODO: check duplicate
				}
				contractToTickerIds.put(contract,tickerIds);
			}
			break;
		case TWS:
			if (debug) println("Requesting Market Data for " + contract.m_symbol + ", ticker id = " + tickerId);
			new Thread() {
				@Override
				public void run() {
					m_client.reqMktData(tickerId, contract, genericTicks, snapshotMktData);
				}
			}.start();
			break;
		case Simulator:
			simulator.reqMktData(tickerId, contract, genericTicks, snapshotMktData);
			break;
		}
	}

	public void cancelMktData(final int tickerId) {
		switch (connectedTo) {
		case Standalone:
			Contract contract = tickerIdToContract.remove(tickerId);
			if (contract == null) {
				new Exception().printStackTrace();
			}
			else {
				List<Integer> tickerIds = contractToTickerIds.get(contract);
				if (tickerIds == null) {
					new Exception("tickerIds == null").printStackTrace();
				}
				else {
					boolean found = tickerIds.remove((Integer) tickerId);
					if (!found) {
						new Exception("").printStackTrace();
					}
					if (tickerIds.size() <= 0) {
						contractToTickerIds.remove(contract);
					}
				}
			}
			break;
		case TWS:
			if (debug) println("Canceling Market Data for ticker id = " + tickerId);
			new Thread() {
				@Override
				public void run() {
					m_client.cancelMktData(tickerId);
				}
			}.start();
			break;
		case Simulator:
			simulator.cancelMktData(tickerId);
			break;
		}
	}

	public void reqIds(final int idCount) {
		switch (connectedTo) {
		case Standalone:
			new Exception().printStackTrace();
			break;
		case TWS:
			new Thread() {
				@Override
				public void run() {
					m_client.reqIds(idCount);
				}
			}.start();
			break;
		case Simulator:
			simulator.reqIds(idCount);
			break;
		}
	}

	public void disconnectFrom(ConnectedTo connectedTo) {
		switch (connectedTo) {
		case Standalone:
			break;
		case TWS:
			new Thread() {
				@Override
				public void run() {

					setDisconnectInProgress(true);

					m_client.eDisconnect();
					if (!m_client.isConnected()) {
						if (MainFrame.this.connectedTo == ConnectedTo.TWS) m_TWS.add("Disconnected.");
						fireServerEvent(new ManagedAccountsEvent(MainFrame.this, null));
					}
				}
			}.start();
			break;
		case Simulator:
			if (simulator != null) {
				clear();
				// Announce that simulator is disconnecting
				fireServerEvent(new ManagedAccountsEvent(MainFrame.this, null));
				simulator.disconnect();
				simulator=null;

				//				marketDataListeners.clear();				
				//				twsListeners.clear();
				//				errorListeners.clear();
			}
			break;
		}
	}

	public void placeOrder(final int orderId, final Contract contract, final Order order) {
		if (invalidOrder(order)) {
			new Exception("invalid order: "+order.toString()).printStackTrace();
		}
		
		switch (connectedTo) {
		case Standalone:
			new Thread() {

				@Override
				public void run() {
					// "Fill" all orders
					OrderState orderState = new OrderState();
					orderState.m_status = "Submitted";
					openOrder(orderId, contract, order, orderState);
					int filled=0;
					int remaining=order.m_totalQuantity;
					SecurityTableRow row = securityPanel.model.getRowForContract(contract);
					
					double lastFillPrice=0.0;
					double avgFillPrice=0.0;
					
					if (order.m_action.equals("BUY")) {
						lastFillPrice=avgFillPrice=row.ask;
					}
					else if (order.m_action.equals("SELL")) {
						lastFillPrice=avgFillPrice=row.bid;						
					}
					if (fillPartially) {
						do {
							// Once in a while, kill an order for no apparent reason
							if (dropOrders && random.nextFloat() < 0.1f) {
								orderStatus(orderId, "Inactive", filled, remaining, avgFillPrice, 0, 0, lastFillPrice, order.m_clientId, null);
								return;
							}
							int i = random.nextInt(remaining) + 1;
							filled += i;
							remaining -= i;
							try {
								currentThread();
								Thread.sleep(random.nextInt(2000));
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (remaining > 0) {
								orderStatus(orderId, "Submitted", filled, remaining, avgFillPrice, 0, 0, lastFillPrice, order.m_clientId, null);
							}
						} while (remaining > 0);
					}
					else {
						filled=remaining;
						remaining=0;
					}
					orderStatus(orderId, "Filled", filled, remaining, avgFillPrice, 0, 0, lastFillPrice, order.m_clientId, null);
				}
			}.start();
			break;
		case TWS:
			if (debug) println("Placing order, orderId = " + orderId);
			twsOrderQueue.add(new Runnable() {
				@Override
				public void run() {
					m_client.placeOrder(orderId, contract, order);
				}
			});
			break;
		case Simulator:
			simulator.placeOrder(orderId, contract, order);
			break;
		}
	}

	private boolean invalidOrder(Order order) {
		if (order.m_totalQuantity <= 0) {
			return true;
		}
		return false;
	}

	public boolean isConnected() {
		switch (connectedTo) {
		case Standalone:
			return true;
		case TWS:
			return m_client.isConnected();
		case Simulator:
			return simulator.isConnected();
		}
		return false;
	}

	public void connectTo(final String ipAddr, final int port, final int clientId) {
		disconnectFrom(connectedTo);
		switch (connectedTo) {
		case Standalone:
			if (marketDataFeed) startMarketDataFeed();
			break;
		case TWS:
			if (debug) println("CONNECTING.");
			// m_bIsFAAccount = false;
			// get connection parameters
			
			new SwingWorker<String, Void>() {

				{setName("CONNECTION THREAD");}
				
				@Override
				protected String doInBackground() throws Exception {
					// connect to TWS
					setDisconnectInProgress(false);
					m_client.eConnect(ipAddr, port, clientId);
					if (m_client.isConnected()) {
						if (connectedTo == ConnectedTo.TWS) m_TWS.add("Connected to Tws server version " + m_client.serverVersion() + " at " + m_client.TwsConnectionTime());
					}
					return "SUCCESS";
				}
			}.execute();
			break;
		case Simulator:
			simulator = new MyServer(this);
			simulator.eConnect(ipAddr, port, clientId);
			if (simulator.isConnected()) {
				simulator.reqIds(1);
			}
			break;
		}
		serverPanel.updatePanel(connectedTo);

	}

	private void startMarketDataFeed() {
		new Thread() {
			@Override
			public void run() {
				do {
					Set<Integer> tickerIds = tickerIdToContract.keySet();
					for (Integer tickerId : tickerIds) {
						double price = OrderSimulator.getNextPrice(50.0);
						int canAutoExecute = 0;
						switch (random.nextInt(3)) {
						case 0:
							tickPrice(tickerId, TickType.BID, price, canAutoExecute);
							break;
						case 1:
							tickPrice(tickerId, TickType.LAST, price, canAutoExecute);
							break;
						case 2:
							tickPrice(tickerId, TickType.ASK, price, canAutoExecute);
							break;
						}
					}
				} while (connectedTo == ConnectedTo.Standalone);
			}
		}.start();
	}

	public void reqPositions() {
		switch (connectedTo) {
		case Standalone:
			new Exception().printStackTrace();
			break;
		case TWS:
			new Thread() {
				@Override
				public void run() {

					m_client.reqPositions();
				}
			}.start();
			break;
		case Simulator:
			simulator.reqPositions();
			break;
		}
	}

	public void reqContractDetails(final int reqId, final Contract contract) {

		switch (connectedTo) {
		case Standalone:
			new Exception().printStackTrace();
			break;
		case TWS:
			new Thread() {
				@Override
				public void run() {
					m_client.reqContractDetails(reqId, contract);
				}
			}.start();
			break;
		case Simulator:
			simulator.reqContractDetails(reqId, contract);
			break;
		}
	}

	public void cancelOrder(final int orderId) {
		switch (connectedTo) {
		case Standalone:
			new Exception().printStackTrace();
			break;
		case TWS:
			new Thread() {
				@Override
				public void run() {
					m_client.cancelOrder(orderId);
				}
			}.start();
			break;
		case Simulator:
			simulator.cancelOrder(orderId);
			break;
		}
	}
	
	@Override
	public void println(String ln) {
		serverPanel.println(ln);
	}

	@Override
	public void print(String string) {
		serverPanel.print(string);
	}

	@Override
	public void println() {
		serverPanel.println();
	}

	public void setConnectTo(final ConnectedTo connectTo) {
		this.connectedTo=connectTo;
		// Remember who we connected-to for next time we start the app.
		PreferencesDialog.setConnectTo(connectTo);
		serverPanel.updatePanel(connectTo);
		disconnectFrom(connectedTo);
		connectTo(null, 7496, 0);
	}

	@Override
	public void clear() {
		serverPanel.clear();
	}

	public void fireTickPrice(Contract contract, int tickType, double price) {
		if (connectedTo != ConnectedTo.Standalone) {
			return;
		}
		List<Integer> tickerIds = contractToTickerIds.get(contract);
		if (tickerIds == null) {
			new RuntimeException("tickerIds == null").printStackTrace();
		}
		for (int tickerId : tickerIds) {
			tickPrice(tickerId, tickType, price, 0);
		}
	}

	public void setAppFont(Font font) {
		securityPanel.setAppFont(font);
		pairsPanel.setAppFont(font);
		positionPanel.setAppFont(font);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		prefMainFrame.putInt(KEY4, getWidth());	
		prefMainFrame.putInt(KEY5, getHeight());
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// nop
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// nop
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// nop
	}

	public static FileChooser getFileChooser() {
		return fileChooser;
	}
	
	public void persist() {
		String s = "Can't write to: ";
		File dir = new File(System.getProperty("user.home")+File.separator+".PairsTrader");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File sf = new File(dir.getPath()+File.separator+"sec.csv");
		try (FileWriter fw = new FileWriter(sf)) {
			securityPanel.exportAllRows(fw);
		}
		catch (IOException e) {
			new Exception(s+sf,e).printStackTrace();
		}

		File pf = new File(dir.getPath()+File.separator+"pairs.csv");
		try (FileWriter fw = new FileWriter(pf)) {
			pairsPanel.exportAllRows(fw);
		}
		catch (IOException e) {
			new Exception(s+pf,e).printStackTrace();
		}
	}
	
	public void fetch() {
		File dir = new File(System.getProperty("user.home")+File.separator+".PairsTrader");
		if (!dir.exists()) {
			return;
		}
		File stocksFile = new File(dir.getPath()+File.separator+"sec.csv");
		try (FileReader fr = new FileReader(stocksFile)) {
			securityPanel.importCsv(fr);
		}
		catch (IOException e) {
			new Exception("Can't read from: "+stocksFile,e).printStackTrace();
		}

		File pairsFile = new File(dir.getPath()+File.separator+"pairs.csv");
		try (FileReader fileReader = new FileReader(pairsFile)) {
			pairsPanel.importCsv(fileReader);
		}
		catch (IOException e) {
			new Exception("Can't read from: "+stocksFile,e).printStackTrace();
		}
	}

	public void exit() {
		try {
			persist();
			dispose();
			System.out.println("N O R M A L    C O M P L E T I O N");
			System.exit(0);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// nop		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		exit();		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// nop
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// nop
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// nop
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// nop		
	}
}

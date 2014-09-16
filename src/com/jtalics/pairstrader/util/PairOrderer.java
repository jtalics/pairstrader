// WARNING: NO "ACID" GUARANTEE ON TRANSACTION
// http://ibkb.interactivebrokers.com/article/1323
// http://www.interactivebrokers.com/en/software/api/apiguide/java/placing_a_combination_order.htm

// EXAMPLE OF USE:

//				int baseReqId = mainFrame.nextValidReqId++;
//				int mateReqId = mainFrame.nextValidReqId++;
//				int pairOrderId = mainFrame.nextValidOrderId++;
//				PairOrderer po=new PairOrderer(mainFrame,ownablePair.baseContract,ownablePair.mateContract,baseReqId,mateReqId,baseOrder,mateOrder,pairOrderId);
//				mainFrame.addTwsListener(po);
//				mainFrame.addTwsErrorListener(po);
//				pairOrderIdToPairOrderer.put(pairOrderId, po);
//				po.start();
//
//	Map<Integer,PairOrderer> pairOrderIdToPairOrderer = new HashMap<>();
//
//	@Override
//	public void onTwsEvent(TwsEvent twsEvent) {
//		if (twsEvent instanceof ValidIdEvent) {
//			ValidIdEvent vie = (ValidIdEvent) twsEvent;
//			mainFrame.nextValidOrderId = vie.nextValidOrderId;
//		}
//		else if (twsEvent instanceof OrderStatusEvent) {			
//			OrderStatusEvent ose = (OrderStatusEvent)twsEvent;
//			if (ose.status != "Filled") return;
//			PairOrderer po = pairOrderIdToPairOrderer.get(ose.orderId);
//			if (po != null) {
//				mainFrame.removeTwsListener(po);
//				mainFrame.removeTwsErrorListener(po);
//			}
//		}
//	}


package com.jtalics.pairstrader.util;

import java.util.Vector;

import com.ib.client.ComboLeg;
import com.ib.client.Contract;
import com.ib.client.Order;
import com.jtalics.pairstrader.MainFrame;
import com.jtalics.pairstrader.ServerErrorListener;
import com.jtalics.pairstrader.ServerErrorListener.ErrorEvent;
import com.jtalics.pairstrader.events.ContractDetailsEndEvent;
import com.jtalics.pairstrader.events.ContractDetailsEvent;
import com.jtalics.pairstrader.server.ServerListener;

/**
 * makes an ACID transaction for ordering pair. (not!)
 * @author talafous
 *
 */
public class PairOrderer implements ServerErrorListener, ServerListener {

	private final int baseReqId;
	private final int mateReqId;
	private final MainFrame mainFrame;
	private final Order baseOrder;
	private final Order mateOrder;
	private final int pairOrderId;
	private final Contract baseContract;
	private final Contract mateContract;
	private Integer baseConId = null;
	private Integer mateConId = null;
	
	public PairOrderer(MainFrame mainFrame, Contract baseContract, Contract mateContract, int baseReqId, int mateReqId, Order baseOrder, Order mateOrder, int pairOrderId) {
		this.mainFrame=mainFrame; 
		this.baseContract = baseContract;
		this.mateContract = mateContract;
		this.baseOrder=baseOrder;
		this.mateOrder=mateOrder; 
		this.baseReqId=baseReqId; 
		this.mateReqId=mateReqId;
		this.pairOrderId = pairOrderId;
	}

	public void start() {
		mainFrame.reqContractDetails(baseReqId,baseContract);
		mainFrame.reqContractDetails(mateReqId,mateContract);
	}
	
	@Override
	public void onServerEvent(ServerEvent twsEvent) {
		if (twsEvent instanceof ContractDetailsEvent) {
			ContractDetailsEvent cde = (ContractDetailsEvent)twsEvent;			
			if (cde.reqId == baseReqId) {
				baseConId = cde.contractDetails.m_summary.m_conId;
			}
			else if (cde.reqId == mateReqId) {
				mateConId = cde.contractDetails.m_summary.m_conId;				
			}

			if (baseConId!=null && mateConId!=null) {
				placeOrder(baseConId,mateConId);
			}
		}
		else if (twsEvent instanceof ContractDetailsEndEvent) {
		}		
	}

	private void placeOrder(Integer baseConId, Integer mateConId) {

		ComboLeg baseComboLeg = new ComboLeg(); // for the first leg
		ComboLeg mateComboLeg = new ComboLeg(); // for the second leg
		Vector<ComboLeg> addAllLegs = new Vector<>();
		addAllLegs.add(mateComboLeg);
		addAllLegs.add(baseComboLeg);
		
		baseComboLeg.m_conId = baseConId;
		baseComboLeg.m_ratio = 1;
		baseComboLeg.m_action = baseOrder.m_action;
		baseComboLeg.m_exchange = "SMART";
		baseComboLeg.m_openClose = 0;
		baseComboLeg.m_shortSaleSlot = 0;
		baseComboLeg.m_designatedLocation = "";

		mateComboLeg.m_conId = mateConId;
		mateComboLeg.m_ratio = 1;
		mateComboLeg.m_action = mateOrder.m_action;
		mateComboLeg.m_exchange = "SMART";
		mateComboLeg.m_openClose = 0;
		mateComboLeg.m_shortSaleSlot = 0;
		mateComboLeg.m_designatedLocation = "";

		Contract pairContract = new Contract();

		Order pairOrder = new Order();

		pairContract.m_symbol = "USD";     // For combo order use “USD” as the symbol value all the time
		pairContract.m_secType = "BAG";   // BAG is the security type for COMBO order
		pairContract.m_exchange = "SMART";
		pairContract.m_currency = "USD";
		pairContract.m_comboLegs = addAllLegs; //including combo order in contract object

		pairOrder.m_action = "BUY";
		pairOrder.m_totalQuantity = 1;
		pairOrder.m_orderType = "MKT";

		mainFrame.placeOrder(pairOrderId, pairContract, pairOrder);
	}

	@Override
	public void onServerErrorEvent(ErrorEvent ee) {
		mainFrame.println(ee.id +" | "+ ee.errorCode+" | "+ee.errorMsg);
	}
}

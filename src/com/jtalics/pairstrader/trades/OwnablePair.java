package com.jtalics.pairstrader.trades;

import com.ib.client.Contract;

public class OwnablePair {

	public enum Side {
		None, Buy, Sell, Bought, Sold
	};
	
	public final Contract baseContract;
	public final double basePrice;
	public int baseTargQty; // target/desired base quantity
	public int baseActQty=0;  // actual base quantity owned
	
	public final Contract mateContract;
	public Integer mateTargQty; // target / desired mate quantity
	public int mateActQty=0;  // actual mate quantity owned
	public final double matePrice; // price paid to own 1 mate

	public OwnablePair(Contract baseContract, double basePrice, int baseTargQuantity, Contract mateContract, double matePrice, Integer mateTargQuantity) throws CloneNotSupportedException {
		this.baseContract = baseContract;//(Contract)baseContract.clone();
		this.mateContract = mateContract;//(Contract)mateContract.clone();
		this.basePrice = basePrice;
		this.baseTargQty = baseTargQuantity;
		this.matePrice = matePrice;
		this.mateTargQty = mateTargQuantity;		
	}

//	@Override
	public String toString2() {
		return "("+baseTargQty+":"+baseContract.m_symbol+","+mateTargQty+":"+mateContract.m_symbol+")";
	}
	
	public String getQuantitiesString() {
		if (mateTargQty == null) {
			return "("+baseActQty+"/"+baseTargQty+",~/~)";
		}
		return "("+baseActQty+"/"+baseTargQty+","+mateActQty+"/"+mateTargQty+")";
	}

	public String getNamesString() {
		return "("+baseContract.m_symbol+","+mateContract.m_symbol+")";
	}
}

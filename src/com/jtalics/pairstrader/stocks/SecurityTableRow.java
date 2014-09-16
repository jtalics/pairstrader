package com.jtalics.pairstrader.stocks;

import com.ib.client.Contract;

public class SecurityTableRow {
	public Contract contract;
	public double bid = Double.NaN;
	public double last = Double.NaN;
	public double ask = Double.NaN;
	public double prevBid = Double.NaN;
	public double prevLast = Double.NaN;
	public double prevAsk = Double.NaN;
	public int errorCode;
	public SecurityTableRow(Contract contract) {
		this.contract = contract;
	}
	
	public SecurityTableRow(Contract contract, double bid, double last, double ask) {
		this.contract = contract;
		this.bid = bid;
		this.last = last;
		this.ask = ask;
	}
}
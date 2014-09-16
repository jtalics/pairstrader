package com.jtalics.pairstrader.pairs;

import com.jtalics.pairstrader.stocks.SecurityTableRowPair;
import com.jtalics.pairstrader.trades.OwnablePair;
import com.jtalics.pairstrader.util.Util;

class PairTableRow {
	public final SecurityTableRowPair pair;
	public int baseQuantity;

	PairTableRow(SecurityTableRowPair pair, int baseQuantity) {
		this.pair = pair;
		this.baseQuantity = baseQuantity;
	}

	public Integer calcMateQuantity() {
		double basePrice = pair.getBaseRow().last;
		double matePrice = pair.getMateRow().last;
		if (Double.isNaN(basePrice) || Double.isNaN(matePrice)) {
			return null;
		}
		double d= -baseQuantity * basePrice / matePrice;
		return new Integer((int)d);
	}

	public double getBid() {
		// To calculate BID: A buyer wants to buy the pair. But how much? 
		// The bid is the price the buyer will pay for the pair.
		// The buyer will pay no more than
		// what buyer would pay to build the pair individually from the open market.
		// Buyer needs a short base and a long mate to build the pair.
		// To get a short base, buyer needs to sell the base at its ask to another buyer.
		// To get a long mate, buyer needs to buy the mate at its bid from another seller.
		// NOTE: BASE QTY IS ALWAYS NEGATIVE, MATE QTY IS ALWAYS POSITIVE
		double basePrice = pair.getBaseRow().ask;
		double matePrice = pair.getMateRow().bid;
		Integer mateQty = calcMateQuantity();
		if (mateQty == null) {
			return Double.NaN;
		}
		return Util.round(baseQuantity * basePrice + mateQty.intValue() * matePrice,2);
	}

	public double getAsk() {
		// To calculate ASK: A seller wants to sell the pair.  But how much?
		// The ask is the price the buyer will take for the pair.
		// The seller will take no more than what the seller  
		// would take to build the pair individually from legs on the open market.
		// Seller needs a long base and a short mate to build the pair.
		// To get a long base, seller needs to buy the base at its bid from another seller.
		// To get a short mate, seller needs to sell the mate at its ask from another buyer.
		// NOTE: BASE QTY IS ALWAYS NEGATIVE, MATE QTY IS ALWAYS POSITIVE
		double basePrice = pair.getBaseRow().bid;
		double matePrice = pair.getMateRow().ask;
		Integer mateQty = calcMateQuantity();
		if (mateQty == null) {
			return Double.NaN;
		}
		return Util.round(baseQuantity * basePrice + mateQty.intValue() * matePrice,2);
	}
}
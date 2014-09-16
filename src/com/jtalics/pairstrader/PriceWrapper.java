package com.jtalics.pairstrader;

public class PriceWrapper {
	public final Double price;
	public final Double delta;
	public final int errorCode;
	public final boolean drawBorder;

	public PriceWrapper(Double price, Double delta, int errorCode, boolean drawBorder) {
		this.price = price;
		this.delta = delta;
		this.errorCode = errorCode;
		this.drawBorder = drawBorder;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+" [");
		sb.append("price="+price);
		sb.append(";delta="+delta);
		sb.append(";errorCode="+errorCode);
		sb.append(";drawBorder="+drawBorder);
		sb.append(";hashCode="+hashCode());
		return sb.toString();
	}
}
package com.jtalics.pairstrader.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {
	
	public final static int BaseBorderThickness = 2;
	
	public static double round(double value, int places) {
		if (Double.isNaN(value)) {
			return value;
		}

		if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
}

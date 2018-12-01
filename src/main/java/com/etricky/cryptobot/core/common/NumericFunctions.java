package com.etricky.cryptobot.core.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.ta4j.core.num.Num;

public class NumericFunctions {
	public static final int PERCENTAGE_SCALE = 2;
	public static final int BALANCE_SCALE = 4;
	public static final int AMOUNT_SCALE = 8;
	public static final int FEE_SCALE = 4;
	public static final int PRICE_SCALE = 2;
	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	public static final BigDecimal _100 = BigDecimal.valueOf(100);
	private static final MathContext MC_PER = new MathContext(4, ROUNDING_MODE);

	public static BigDecimal convertToBigDecimal(Num value) {
		return BigDecimal.valueOf(value.doubleValue());
	}

	public static BigDecimal convertToBigDecimal(Num value, int decimalPart) {
		return BigDecimal.valueOf(value.doubleValue()).setScale(decimalPart, ROUNDING_MODE);
	}

	public static BigDecimal percentage(BigDecimal value, BigDecimal baseValue) {
		return percentage(value, baseValue, false);
	}

	public static BigDecimal percentage(BigDecimal value, BigDecimal baseValue, boolean delta) {

		if (baseValue.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		} else if (delta) {
			return value.subtract(baseValue).divide(baseValue, MC_PER).multiply(_100).setScale(PERCENTAGE_SCALE,
					ROUNDING_MODE);
		} else {
			return value.divide(baseValue, MC_PER).multiply(_100).setScale(PERCENTAGE_SCALE, ROUNDING_MODE);
		}
	}

	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
		return dividend.divide(divisor, scale, NumericFunctions.ROUNDING_MODE);
	}

	public static BigDecimal subtract(BigDecimal subtrahend, BigDecimal minuend, int scale) {
		return subtrahend.subtract(minuend).setScale(scale, ROUNDING_MODE);
	}
}
package com.icarus;


import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by sony on 04/08/2015.
 */
public class ProgressiveChargeAlgorithm extends ChargeAlgorithm {

    private static final long QUICK_CHARGE_QUOTE_AGE_MILLIS = 2 * 60 * 1000;
    private static final long AVERAGE_CHARGE_QUOTE_AGE_MILLIS = 10 * 60 * 1000;
    private static final long SLOW_CHARGE_QUOTE_AGE_MILLIS = 20 * 60 * 1000;
    public static final BigDecimal QUICK_PROCESSING_CHARGE = new BigDecimal(0);
    public static final BigDecimal STANDARD_PROCESSING_CHARGE = BigDecimal.TEN;//??? how to avoid duplication?
    public static final BigDecimal SLOW_PROCESSING_CHARGE = new BigDecimal(20);


    public BigDecimal processingCharge(long timeElapsed, BigDecimal quoteAmount) {
        if (timeElapsed <= QUICK_CHARGE_QUOTE_AGE_MILLIS) {
            charge = QUICK_PROCESSING_CHARGE;
        } else if (timeElapsed <= AVERAGE_CHARGE_QUOTE_AGE_MILLIS) {
            charge = STANDARD_PROCESSING_CHARGE.min(quoteAmount.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP));
        } else charge = SLOW_PROCESSING_CHARGE;
        return charge;
    }
}
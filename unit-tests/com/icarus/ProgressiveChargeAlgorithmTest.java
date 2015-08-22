package com.icarus;


import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

public class ProgressiveChargeAlgorithmTest {




    private static final int MINUTE = 60 * 1000;
    private BigDecimal smallPriceBeforeSurcharge = new BigDecimal(100);
    private BigDecimal bigPriceBeforeSurcharge = new BigDecimal(300);

    @Test
    public void shouldReturnZeroWhenQuickOrder() {
        long timeElapsed =  MINUTE;
        validateResult(timeElapsed, smallPriceBeforeSurcharge, new BigDecimal(0));
    }

    @Test
    public void shouldReturnTwentyWhenSlowOrder() {
        long timeElapsed = 21*MINUTE;
        validateResult(timeElapsed,smallPriceBeforeSurcharge, new BigDecimal(20));
    }

    @Test
    public void shouldReturnFivePercentWhenAverageTImeOrder() {
        long timeElapsed = 10 * MINUTE;
        validateResult(timeElapsed, smallPriceBeforeSurcharge, new BigDecimal(5.00).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void shouldReturnTenWhenAverageTimeOrder() {
        long timeElapsed = 10 * MINUTE;
        validateResult(timeElapsed,bigPriceBeforeSurcharge, new BigDecimal(10));
    }

    private void validateResult(long timeElapsed, BigDecimal totalAmount, BigDecimal expected) {
        ChargeAlgorithm algorithm = new ProgressiveChargeAlgorithm();
        BigDecimal actual = algorithm.processingCharge(timeElapsed, totalAmount);
        Assert.assertEquals("Should return " + expected, expected, actual);
    }
}

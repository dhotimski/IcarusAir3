/**
 * Created by sony on 04/08/2015.
 */
package com.icarus;


import java.math.BigDecimal;

public class FixedChargeAlgorithm extends ChargeAlgorithm {

    public static final BigDecimal STANDARD_PROCESSING_CHARGE = BigDecimal.TEN;

    @Override
    public BigDecimal processingCharge ( long timeElapsed, BigDecimal quoteAmount) {
        charge = STANDARD_PROCESSING_CHARGE;
        return charge;
    }
}

package com.icarus;

import java.math.BigDecimal;

/**
 * Created by sony on 04/08/2015.
 */
public abstract class ChargeAlgorithm {
   protected BigDecimal charge;
   public BigDecimal getCharge() {return charge;}

   public BigDecimal processingCharge(long timeElapsed, BigDecimal totalAmount){return null;};
}

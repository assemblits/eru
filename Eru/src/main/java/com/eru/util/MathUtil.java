package com.eru.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by mtrujillo on 05/09/2014.
 */
public final class MathUtil {

    /**
     * Don't let anyone instantiate this class.
     */
    private MathUtil() {}

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

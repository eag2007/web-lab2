package ru.web2;

import java.math.BigDecimal;

public class Validator {
    public static boolean validate(BigDecimal x, BigDecimal y, BigDecimal r) {
        return x.compareTo(BigDecimal.valueOf(-4)) >= 0 && x.compareTo(BigDecimal.valueOf(4)) <= 0 &&
                x.stripTrailingZeros().scale() <= 0 &&

                y.compareTo(BigDecimal.valueOf(-5)) >= 0 && y.compareTo(BigDecimal.valueOf(5)) <= 0 &&

                r.compareTo(BigDecimal.ONE) >= 0 &&
                r.compareTo(BigDecimal.valueOf(5)) <= 0 &&
                r.stripTrailingZeros().scale() <= 0;
    }
}

package ru.web2;

import java.math.BigDecimal;

public class AreaChecker {
    public static boolean check(BigDecimal x, BigDecimal y, BigDecimal global_r) {
        // прямоугольник
        if (x.compareTo(global_r.negate()) >= 0 &&
                x.compareTo(BigDecimal.ZERO) <= 0 &&
                y.compareTo(BigDecimal.ZERO) >= 0 &&
                y.compareTo(global_r.divide(BigDecimal.TWO)) <= 0) {
            return true;
        }

        // четверть круга
        if (x.pow(2).add(y.pow(2)).compareTo(global_r.divide(BigDecimal.TWO).pow(2)) <= 0 &&
                x.compareTo(BigDecimal.ZERO) <= 0 &&
                y.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }

        // треугольник
        if (y.compareTo(x.subtract(global_r)) >= 0 &&
            y.compareTo(BigDecimal.ZERO) <= 0 &&
            x.compareTo(BigDecimal.ZERO) >= 0 &&
            x.compareTo(global_r) <= 0) {
            return true;
        }
        return false;
    }
}

package validator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ValidatorCheckPointController {

    @GetMapping("/api/check")
    public String check(@RequestParam BigDecimal x, @RequestParam BigDecimal y, @RequestParam BigDecimal global_r) {
        // прямоугольник
        if (x.compareTo(global_r.negate()) >= 0 &&
                x.compareTo(BigDecimal.ZERO) <= 0 &&
                y.compareTo(BigDecimal.ZERO) >= 0 &&
                y.compareTo(global_r.divide(BigDecimal.TWO)) <= 0) {
            return "Попала";
        }

        // четверть круга
        if (x.pow(2).add(y.pow(2)).compareTo(global_r.divide(BigDecimal.TWO).pow(2)) <= 0 &&
                x.compareTo(BigDecimal.ZERO) <= 0 &&
                y.compareTo(BigDecimal.ZERO) <= 0) {
            return "Попала";
        }

        // треугольник
        if (y.compareTo(x.subtract(global_r)) >= 0 &&
                y.compareTo(BigDecimal.ZERO) <= 0 &&
                x.compareTo(BigDecimal.ZERO) >= 0 &&
                x.compareTo(global_r) <= 0) {
            return "Попала";
        }
        return "Не попала";
    }
}

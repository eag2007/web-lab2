package validator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class ValidatorController {

    @GetMapping("api/validate")
    public boolean validate(@RequestParam BigDecimal x, @RequestParam BigDecimal y, @RequestParam BigDecimal r) {
        return x.compareTo(BigDecimal.valueOf(-4)) >= 0 && x.compareTo(BigDecimal.valueOf(4)) <= 0 &&
                x.stripTrailingZeros().scale() <= 0 &&

                y.compareTo(BigDecimal.valueOf(-5)) >= 0 && y.compareTo(BigDecimal.valueOf(5)) <= 0 &&

                r.compareTo(BigDecimal.ONE) >= 0 &&
                r.compareTo(BigDecimal.valueOf(5)) <= 0 &&
                r.stripTrailingZeros().scale() <= 0;
    }
}

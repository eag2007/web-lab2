package writer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReaderController {

    private final StringRedisTemplate redis;

    public ReaderController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @GetMapping(value = "/read", produces = "application/json")
    public String readPoints() {
        List<String> points = redis.opsForList().range("points", 0, -1);

        return "[" + String.join(",", points) + "]";
    }
}

package writer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClearController {

    private final StringRedisTemplate redis;

    public ClearController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostMapping("/clear")
    public void clear() {
        redis.delete("points");
    }
}
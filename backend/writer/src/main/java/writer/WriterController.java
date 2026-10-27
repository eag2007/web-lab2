package writer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriterController {

    private final StringRedisTemplate redis;

    public WriterController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @PostMapping("/write")
    public void writePoint(@RequestBody String point) {
        redis.opsForList().rightPush("points", point);
    }
}

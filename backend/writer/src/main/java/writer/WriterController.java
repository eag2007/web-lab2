package writer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriterController {

    @GetMapping("/write")
    public void writePoints() {

    }
}

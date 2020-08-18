package fm.douban.app.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainControl {

    @GetMapping(path = "/index")
    public String index(Model model) {
        return "index";
    }
}

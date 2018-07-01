package uaic.fii.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class StartPageController {
    @RequestMapping(method = GET)
    public String loginPage() {
        return "login";
    }
}

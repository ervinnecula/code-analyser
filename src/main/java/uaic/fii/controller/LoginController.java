package uaic.fii.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/login")
public class LoginController {

    @RequestMapping(method = GET)
    public String loadLoginPage() {
        return "login";
    }
}

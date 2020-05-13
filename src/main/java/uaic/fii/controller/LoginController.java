package uaic.fii.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class LoginController {

    @RequestMapping(value = {"/", "/login"}, method = GET)
    public String loadLoginPage() {
        return "login";
    }

    @RequestMapping(value = "/login-error", method = GET)
    public String loginPage(Model model) {
        model.addAttribute("isInvalid", "is-invalid");
        model.addAttribute("errorMessage", "Incorrect username or password.");
        model.addAttribute("hasDanger", "has-danger");

        return "login";
    }
}

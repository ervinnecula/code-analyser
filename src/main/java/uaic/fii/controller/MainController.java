package uaic.fii.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import uaic.fii.model.User;
import uaic.fii.security.CustomUserDetails;
import uaic.fii.service.UserService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class MainController {
    final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/main", method = GET)
    public String loadMainPage(Model model) {
        Integer id = getAuthenticatedEntity().getUserId();
        User user = userService.getUserById(id);

        model.addAttribute("username", user.getName());
        return "main";
    }

    private CustomUserDetails getAuthenticatedEntity() {
        return (CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }


}

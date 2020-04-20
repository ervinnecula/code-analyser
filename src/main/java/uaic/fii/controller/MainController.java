package uaic.fii.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uaic.fii.model.UserDAO;
import uaic.fii.security.CustomUserDetails;
import uaic.fii.service.CommitService;
import uaic.fii.service.UserService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final UserService userService;

    private final CommitService commitService;

    @Autowired
    public MainController(UserService userService, CommitService commitService) {
        this.userService = userService;
        this.commitService = commitService;
    }

    @RequestMapping(value = "/main", method = GET)
    public String loadMainPage(Model model) {
        logger.info("MainController() - loadMainPage - Loading main page");
        Integer id = getAuthenticatedEntity().getUserId();
        UserDAO user = userService.getUserById(id);

        commitService.loadProperties(user.getName());
        model.addAttribute("username", user.getName());
        return "main";
    }

    private CustomUserDetails getAuthenticatedEntity() {
        return (CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }


}

package uaic.fii.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uaic.fii.model.UserDAO;
import uaic.fii.security.CustomUserDetails;
import uaic.fii.service.PropertiesService;
import uaic.fii.service.UserService;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Scope("session")
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final UserService userService;

    private final PropertiesService propertiesService;

    @Autowired
    public MainController(UserService userService, PropertiesService propertiesService) {
        this.userService = userService;
        this.propertiesService = propertiesService;
    }

    @RequestMapping(value = "/main", method = GET)
    public String loadMainPage(HttpServletRequest request) {
        logger.info("MainController() - loadMainPage - Loading main page");
        Integer id = getAuthenticatedEntity().getUserId();
        UserDAO user = userService.getUserById(id);

        propertiesService.loadPropertiesByUsername(user.getName());

        request.getSession().setAttribute("username", user.getName());
        return "main";
    }

    private CustomUserDetails getAuthenticatedEntity() {
        return (CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }


}

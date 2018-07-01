package uaic.fii.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/enter")
public class LoginSuccessController {

    static Map<String, String> userPages;
    static {
        userPages = new HashMap<>();
        userPages.put("ROLE_USER", "redirect:/main");
//        userPages.put("ROLE_ADM", "redirect:/admin");
    }

    @RequestMapping(method = GET)
    public String getUserPage() {
        for( GrantedAuthority authority : getAuthenticatedUserRoles()) {
            return userPages.get(authority.getAuthority());
        }
        return "unauthorized";
    }

    private Collection<? extends GrantedAuthority> getAuthenticatedUserRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}

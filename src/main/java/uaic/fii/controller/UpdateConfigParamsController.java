package uaic.fii.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uaic.fii.bean.PropertiesBean;
import uaic.fii.service.PropertiesService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class UpdateConfigParamsController {

    private final PropertiesService propertiesService;

    @Autowired
    public UpdateConfigParamsController(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @RequestMapping(value="/update-config", method = POST)
    public String updateConfigParams(HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     @Valid @ModelAttribute PropertiesBean propertiesBean) {

        String sessionUsername = (String)request.getSession().getAttribute("username");
        propertiesService.updateProperties(sessionUsername, propertiesBean.getLargeFileSize(),
                                            propertiesBean.getHugeFileSize(), propertiesBean.getManyCommitters(),
                                            propertiesBean.getFewCommitters(), propertiesBean.getMediumChangeSize(),
                                            propertiesBean.getMajorChangeSize(), propertiesBean.getPeriodOfTime());
        propertiesService.loadPropertiesByUsername(sessionUsername);

        redirectAttributes.addAttribute("repoGitUrl", propertiesBean.getRepoGitUrl());
        redirectAttributes.addAttribute("repoName", propertiesBean.getRepoName());
        redirectAttributes.addAttribute("username", sessionUsername);
        return "redirect:/analysis";

    }
}

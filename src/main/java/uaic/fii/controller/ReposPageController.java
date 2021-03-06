package uaic.fii.controller;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import uaic.fii.bean.RepoNameHtmlGitUrlsBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ReposPageController {

    private final static Logger logger = LoggerFactory.getLogger(ReposPageController.class);

    @RequestMapping(value = "/repos", method = GET)
    public String getReposPage(@ModelAttribute("repoOwner") String repoOwner, Model model) {
        List<RepoNameHtmlGitUrlsBean> repoBeans = new ArrayList<>();
        GitHubClient client = new GitHubClient();
        RepositoryService service = new RepositoryService(client);

        client.setCredentials(System.getenv("GITHUB_USER"), System.getenv("GITHUB_PASS"));
        try {
            for (Repository repo : service.getRepositories(repoOwner)) {
                repoBeans.add(new RepoNameHtmlGitUrlsBean(repo.getName(), repo.getHtmlUrl(), repo.getGitUrl(), repo.getLanguage(), false));
            }

        } catch (IOException e) {
            logger.error("ReposPageController - getReposPage() - Could not get repositories for username {}", repoOwner, e);
            model.addAttribute("isInvalid", "is-invalid");
            model.addAttribute("errorMessage", "Couldn't find such user named \"" + repoOwner + "\"");
            return "main";
        }

        model.addAttribute("repoNameHtmlGitUrlsBeans", repoBeans);

        return "repos";
    }
}

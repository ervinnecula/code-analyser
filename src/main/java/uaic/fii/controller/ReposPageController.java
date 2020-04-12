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
    public String getReposPage(@ModelAttribute("username") String username, Model model) {
        List<RepoNameHtmlGitUrlsBean> repoBeans = new ArrayList<>();
        GitHubClient client = new GitHubClient();

        client.setCredentials(System.getenv("GITHUB_USER"), System.getenv("GITHUB_PASS"));
        RepositoryService service = new RepositoryService(client);
        try {
            for (Repository repo : service.getRepositories(username)) {
                repoBeans.add(new RepoNameHtmlGitUrlsBean(repo.getName(), repo.getHtmlUrl(), repo.getGitUrl(), repo.getLanguage(), false));
            }

        } catch (IOException e) {
            logger.error("ReposPageController - getReposPage() - Could not get repositories for username {}", username, e);
            return "error";
        }

        model.addAttribute("repoNameHtmlGitUrlsBeans", repoBeans);

        return "repos";
    }
}

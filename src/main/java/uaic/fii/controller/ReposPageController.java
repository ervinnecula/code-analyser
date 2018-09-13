package uaic.fii.controller;

import net.sourceforge.pmd.PMDException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.RepoNameHtmlGitUrlsBean;
import uaic.fii.service.HeatMapService;
import uaic.fii.service.RepoService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ReposPageController {
    final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private final String REPO_LANGUAGE = "Java";

    @Autowired
    private RepoService repoService;

    @Autowired
    private HeatMapService heatMapService;

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
            e.printStackTrace();
        }

        model.addAttribute("repoNameHtmlGitUrlsBeans", repoBeans);

        return "repos";
    }

    @RequestMapping(value = "/clone", method = RequestMethod.POST)
    public String cloneRepo(@ModelAttribute RepoNameHtmlGitUrlsBean repoBean, @ModelAttribute("username") String username, Model model) {
        File resourceFolder;
        logger.debug("ReposPageController - cloneRepo() - /clone endpoint called ");

        logger.debug("ReposPageController- cloneRepo() - calling repoService.getPathToCloneDir() ");
        resourceFolder = new File(repoService.getPathToCloneDir() + File.separator + repoBean.getRepoName());

        try {
            repoService.cloneRepo(repoBean, resourceFolder);
            logger.debug(format("MainController - cloneRepo() - Repository language is: %s", repoBean.getRepoLanguage()));
            if (REPO_LANGUAGE.equals(repoBean.getRepoLanguage())) {
                logger.debug("MainController - cloneRepo() - Calling repoService.analyzeClonedProject() ");
                model.addAttribute("violationsCount", repoService.shortAnalyzeClonedProject(resourceFolder.getPath()));
            } else {
                return "error";
            }
        } catch (GitAPIException e) {
            logger.error(format("ReposPageController - cloneRepo() - Git exception happened when trying get data from %s. Full exception: %s", repoBean.getRepoName(), e));
            return "error";
        } catch (IOException e) {
            logger.error(format("ReposPageController - cloneRepo() - Git exception happened when opening folder %s. Full exception: %s", resourceFolder, e));
            return "error";
        } catch (PMDException e) {
            logger.error(format("ReposPageController - cloneRepo() - Exception when running PMD over %s. Full exception: %s", resourceFolder, e));
        }

        model.addAttribute("repositoryName", repoBean.getRepoName());
        return "analysis";
    }

    @RequestMapping(value = "/commits", method = RequestMethod.GET)
    public String getCommits(@ModelAttribute("repositoryName") String repositoryName, @ModelAttribute("username") String username, Model model) {
        File resourceFolder = new File(repoService.getPathToCloneDir() + "//" + repositoryName);
        try {
            List<CommitDiffBean> commits = repoService.getCommitsAndDiffs(resourceFolder);
            String csvFile = heatMapService.getPathDiffsCsvFile(commits);
            model.addAttribute("mapData", csvFile);
        } catch (IOException e) {
            logger.error(format("ReposPageController - getCommits() - Git exception happened when opening folder %s. Full exception: %s", resourceFolder, e));
            return "error";
        }
        //TODO add processed data
        return "map";
    }

    @RequestMapping(value = "/static", method = RequestMethod.GET)
    public String staticAnalyse(@ModelAttribute("repositoryName") String repositoryName, @ModelAttribute("username") String username, Model model) {
        logger.debug("ReposPageController - staticAnalyse() - /static endpoint called ");

        File resourceFolder = new File(repoService.getPathToCloneDir() + File.separator + repositoryName);
        logger.debug("ReposPageController- staticAnalyse() - calling repoService.getPathToCloneDir() ");

        try {
            logger.debug("MainController - cloneRepo() - Calling repoService.analyzeClonedProject() ");
            model.addAttribute("violations", repoService.analyzeClonedProject(resourceFolder.getPath()));
        } catch (IOException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", resourceFolder, e));
            return "error";
        } catch (PMDException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", resourceFolder, e));
        }

        model.addAttribute("repositoryName", repositoryName);
        return "static";
    }

}

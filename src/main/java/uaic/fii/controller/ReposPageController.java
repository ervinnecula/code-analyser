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
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.RepoNameHtmlGitUrlsBean;
import uaic.fii.bean.RuleViolationBean;
import uaic.fii.service.HeatMapCommitService;
import uaic.fii.service.HeatMapContributorService;
import uaic.fii.service.LocChartService;
import uaic.fii.service.RepoService;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ReposPageController {
    final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private RepoService repoService;

    @Autowired
    private HeatMapCommitService heatMapCommitService;

    @Autowired
    private LocChartService locChartService;

    @Autowired
    private HeatMapContributorService heatMapContributorService;

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
            logger.error("Could not get repositories for username {}", username, e);
            return "error";
        }

        model.addAttribute("repoNameHtmlGitUrlsBeans", repoBeans);

        return "repos";
    }

    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    public String getCommits(@ModelAttribute RepoNameHtmlGitUrlsBean repoBean, @ModelAttribute("username") String username, Model model) {
        File resourceFolder = new File(repoService.getPathToCloneDir() + "//" + repoBean.getRepoName());
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            repoService.cloneRepo(repoBean, resourceFolder);
            List<RuleViolationBean> ruleViolations = staticAnalyse(resourceFolder);

            List<CommitDiffBean> commits = repoService.getCommitsAndDiffs(resourceFolder);
            Date startDate = commits.get(commits.size() - 1).getCommitDate();
            Date endDate = commits.get(0).getCommitDate();

            String heatMapCommitsCsvFile = heatMapCommitService.getPathDiffsCsvFile(commits);
            String heatMapContributorsCsvFile = heatMapContributorService.getPathContributorsCsvFile(commits);
            String addRemoveLinesCsvFile = locChartService.getAddRemoveLinesOverTime(commits);
            String locCsvFile = locChartService.getLOCOverTime(commits, startDate, endDate);

            model.addAttribute("startDate", formatter.format(startDate));
            model.addAttribute("endDate", formatter.format(endDate));
            model.addAttribute("heatMapCommitsData", heatMapCommitsCsvFile);
            model.addAttribute("heatMapContributorsData", heatMapContributorsCsvFile);
            model.addAttribute("addRemoveLinesData", addRemoveLinesCsvFile);
            model.addAttribute("locData", locCsvFile);
            model.addAttribute("violationsData", ruleViolations);
            model.addAttribute("repositoryName", repoBean.getRepoName());
            model.addAttribute("username", username);
        } catch (IOException e) {
            logger.error(format("ReposPageController - analysis() - Git exception happened when opening folder %s. Full exception: %s", resourceFolder, e));
            return "error";
        } catch (GitAPIException e) {
            logger.error(format("ReposPageController - analysis() - Git exception happened when trying get data from %s. Full exception: %s", repoBean.getRepoName(), e));
            return "error";
        }
        return "map";
    }

    private List<RuleViolationBean> staticAnalyse(File projectPath) {
        logger.debug("ReposPageController - staticAnalyse() - calling repoService.getPathToCloneDir() ");
        List<RuleViolationBean> ruleViolationBeans = new ArrayList<>();
        try {
            ruleViolationBeans = repoService.analyzeClonedProject(projectPath.getPath());
            logger.debug("MainController - cloneRepo() - Calling repoService.analyzeClonedProject() ");
        } catch (IOException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", projectPath, e));
        } catch (PMDException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", projectPath, e));
        }
        return ruleViolationBeans;
    }

}

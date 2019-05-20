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
import uaic.fii.service.AntiPatternsService;
import uaic.fii.service.CommitService;
import uaic.fii.service.HeatMapContributorService;
import uaic.fii.service.LocChartService;
import uaic.fii.service.OverviewService;
import uaic.fii.service.RepoService;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static uaic.fii.service.ChartDataStringWriters.*;
import static uaic.fii.service.ChartDataStringWriters.writeHeatMapContributorsToCSVFormat;

@Controller
public class ReposPageController {
    final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private RepoService repoService;

    @Autowired
    private CommitService commitService;

    @Autowired
    private LocChartService locChartService;

    @Autowired
    private HeatMapContributorService heatMapContributorService;

    @Autowired
    private OverviewService overviewService;

    @Autowired
    private AntiPatternsService antipatternsService;

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
            repoService.cloneOrPullRepo(repoBean, resourceFolder);

            List<CommitDiffBean> commits = repoService.getCommitsAndDiffs(resourceFolder);
            Date startDate = commits.get(commits.size() - 1).getCommitDate();
            Date endDate = commits.get(0).getCommitDate();

            model.addAttribute("startDate", formatter.format(startDate));
            model.addAttribute("endDate", formatter.format(endDate));
            model.addAttribute("locByLanguage", overviewService.getLocByLanguage(resourceFolder));
            model.addAttribute("filesByLanguage", overviewService.getNumberOfFilesByLanguage(resourceFolder));
            model.addAttribute("heatMapCommitsData", writeHeatMapContributorsToCSVFormat(heatMapContributorService.getPathContributorsCsvFile(commits)));
            model.addAttribute("heatMapContributorsData", writeStringStringIntegerMapToCSVFormat(commitService.getPathDiffsCsvFile(commits)));
            model.addAttribute("addRemoveLinesData", writeLinesAddedRemovedToCSVFormat(locChartService.getAddRemoveLinesOverTime(commits)));
            model.addAttribute("locData", writeStringIntegerMapToCSVFormat(locChartService.getLOCOverTime(commits, startDate, endDate)));
            model.addAttribute("violationsData", staticAnalyse(resourceFolder));
            model.addAttribute("repositoryName", repoBean.getRepoName());
            model.addAttribute("username", username);

            model.addAllAttributes(prepareAntiPatternsMap(commits));
            model.addAttribute("mediumAndHugeChanges", antipatternsService.detectMediumAndMajorChangesPattern(commits));

        } catch (IOException e) {
            logger.error(format("ReposPageController - analysis() - Git exception happened when opening folder %s. Full exception: %s", resourceFolder, e));
            return "error";
        } catch (GitAPIException e) {
            logger.error(format("ReposPageController - analysis() - Git exception happened when trying get data from %s. Full exception: %s", repoBean.getRepoName(), e));
            return "error";
        }
        return "map";
    }

    private Map<String, String> prepareAntiPatternsMap(List<CommitDiffBean> commits) {
        Map<String, String> antiPatternsMap = new HashMap<>();
        antiPatternsMap.put("singlePointOfFailure", antipatternsService.singlePointOfFailurePattern(commits));
        antiPatternsMap.put("conglomerate", antipatternsService.conglomeratePattern(commits));

        return antiPatternsMap;
    }

    private List<RuleViolationBean> staticAnalyse(File projectPath) {
        logger.info("ReposPageController - staticAnalyse() - calling repoService.getPathToCloneDir() ");
        List<RuleViolationBean> ruleViolationBeans = new ArrayList<>();
        try {
            ruleViolationBeans = repoService.analyzeClonedProject(projectPath.getPath());
            logger.info("MainController - cloneOrPullRepo() - Calling repoService.analyzeClonedProject() ");
        } catch (IOException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", projectPath, e));
        } catch (PMDException e) {
            logger.error(format("ReposPageController - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", projectPath, e));
        }
        return ruleViolationBeans;
    }

}

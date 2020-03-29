package uaic.fii.controller;

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
import uaic.fii.service.AuthorService;
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
    final static Logger logger = LoggerFactory.getLogger(ReposPageController.class);

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

    @Autowired
    private AuthorService authorService;

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
            antipatternsService.loadStaticAnalysisResults(resourceFolder);
            commitService.loadProperties(username);

            List<CommitDiffBean> commits = repoService.getCommitsAndDiffs(resourceFolder);
            Date startDate = commits.get(commits.size() - 1).getCommitDate();
            Date endDate = commits.get(0).getCommitDate();

            model.addAttribute("startDate", formatter.format(startDate));
            model.addAttribute("endDate", formatter.format(endDate));
            model.addAttribute("fewCommiters", commitService.getProperties().getFewCommitersSize());
            model.addAttribute("manyCommiters", commitService.getProperties().getManyCommitersSize());
            model.addAllAttributes(prepareCustomOverviewMap(resourceFolder, commits));
            model.addAttribute("top5ActiveContributorsLoC", overviewService.getActiveContributorsLoC(commits));
            model.addAttribute("top5ActiveContributorsFiles", overviewService.getActiveContributorsFilesTouched(commits));
            model.addAttribute("top5InvolvedContributors", overviewService.getMostInvolvedContributors(commits));
            model.addAttribute("heatMapCommitsData", writeStringStringIntegerMapToCSVFormat(commitService.getPathDiffsCsvFile(commits)));
            model.addAttribute("heatMapContributorsData", writeHeatMapContributorsToCSVFormat(heatMapContributorService.getPathContributorsCsvFile(commits)));
            model.addAttribute("addRemoveLinesData", writeLinesAddedRemovedToCSVFormat(locChartService.getAddRemoveLinesOverTime(commits)));
            model.addAttribute("locData", writeStringIntegerMapToCSVFormat(locChartService.getLOCOverTime(commits, startDate, endDate)));
            model.addAttribute("authorsData", authorService.getAuthorActivityList(commits));
            model.addAttribute("mediumAndHugeChanges", antipatternsService.detectMediumAndMajorChangesPattern(commits));
            model.addAttribute("filesAndPeriods", writePeriodOfTimeFilesToCSVFormat(antipatternsService.getPeriodOfTimeAllFiles(commits)));
            model.addAttribute("orphanedFiles", antipatternsService.getOrphanedFiles(commits));
            model.addAllAttributes(preparePMDAntiPatternsMap());
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

    private Map<String, String> prepareCustomOverviewMap(File resourceFolder, List<CommitDiffBean> commits) {
        Map<String, String> customOverviewMap = new HashMap<>();

        customOverviewMap.put("locByLanguage", overviewService.getLocByLanguage(resourceFolder));
        customOverviewMap.put("filesByLanguage", overviewService.getNumberOfFilesByLanguage(resourceFolder));
        customOverviewMap.put("recentFilesChanged", overviewService.countRecentFilesChanged(commits));
        customOverviewMap.put("recentLinesChanged", overviewService.countRecentLinesChanged(commits));
        customOverviewMap.put("recentContributors", overviewService.countRecentContributors(commits));

        return customOverviewMap;
    }

    private Map<String, List<RuleViolationBean>> preparePMDAntiPatternsMap() {
        Map<String, List<RuleViolationBean>> PMDAntiPatternsMap = new HashMap<>();
        PMDAntiPatternsMap.put("basicViolations", antipatternsService.getBasicAnalysis());
        PMDAntiPatternsMap.put("optimizationViolations", antipatternsService.getOptimizationViolations());
        PMDAntiPatternsMap.put("couplingViolations", antipatternsService.getCouplingViolations());
        PMDAntiPatternsMap.put("codesizeViolations", antipatternsService.getCodesizeViolations());
        PMDAntiPatternsMap.put("designViolations", antipatternsService.getDesignViolations());

        return PMDAntiPatternsMap;
    }

}

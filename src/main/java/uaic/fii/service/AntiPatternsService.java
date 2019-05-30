package uaic.fii.service;

import net.sourceforge.pmd.PMDException;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitChangeSize;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateHashSetBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.RuleViolationBean;
import uaic.fii.controller.MainController;
import uaic.fii.model.ChangeSize;
import uaic.fii.model.Properties;
import uaic.fii.model.StaticDetectionKind;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.lang.String.format;
import static uaic.fii.model.StaticDetectionKind.BASIC;
import static uaic.fii.model.StaticDetectionKind.CODESIZE;
import static uaic.fii.model.StaticDetectionKind.COUPLING;
import static uaic.fii.model.StaticDetectionKind.DESIGN;
import static uaic.fii.model.StaticDetectionKind.OPTIMIZATION;
import static uaic.fii.service.ChartDataStringWriters.writeHeatMapContributorsToCSVFormat;

@Service
public class AntiPatternsService {
    final static Logger logger = LoggerFactory.getLogger(AntiPatternsService.class);

    @Autowired
    private HeatMapContributorService heatMapContributorService;

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private RepoService repoService;

    private Properties properties;

    private Map<StaticDetectionKind, List<RuleViolationBean>> staticRuleViolations;

    public void loadProperties(String userName) {
        properties = propertiesService.getPropertiesByUserId(userName);
    }

    public String singlePointOfFailurePattern(List<CommitDiffBean> commitList) {
        Map<String, DateHashSetBean> allContributors = heatMapContributorService.getPathContributorsCsvFile(commitList);
        Map<String, DateHashSetBean> potentialSinglePointOfFailures = new TreeMap<>();

        for (Map.Entry<String, DateHashSetBean> entry : allContributors.entrySet()) {
            if (entry.getValue().getListOfContributors().size() <= properties.getFewCommitersSize()) {
                potentialSinglePointOfFailures.put(entry.getKey(), entry.getValue());
            }
        }
        return writeHeatMapContributorsToCSVFormat(potentialSinglePointOfFailures);
    }

    public String conglomeratePattern(List<CommitDiffBean> commitList) {
        Map<String, DateHashSetBean> allContributors = heatMapContributorService.getPathContributorsCsvFile(commitList);
        Map<String, DateHashSetBean> potentialConglomerations = new TreeMap<>();

        for (Map.Entry<String, DateHashSetBean> entry : allContributors.entrySet()) {
            if (entry.getValue().getListOfContributors().size() >= properties.getManyCommitersSize()) {
                potentialConglomerations.put(entry.getKey(), entry.getValue());
            }
        }
        return writeHeatMapContributorsToCSVFormat(potentialConglomerations);
    }

    public Map<String, List<CommitChangeSize>> detectMediumAndMajorChangesPattern(List<CommitDiffBean> commitList) {
        Map<String, List<CommitChangeSize>> mediumAndMajorChangesPattern = new HashMap<>();
        Set<String> filesChangedInCommit;

        for (CommitDiffBean commit : commitList) {
            filesChangedInCommit = new HashSet<>();
            int linesChangedInCommit = 0;
            for (DiffBean diff : commit.getDiffs()) {
                int linesChangedInDiff = 0;
                for (Edit edit : diff.getEdits()) {
                    linesChangedInDiff += edit.getLengthB() + edit.getLengthA();
                }
                filesChangedInCommit.add(diff.getFilePath());
                linesChangedInCommit += linesChangedInDiff;
            }
            ChangeSize changeSize = getChangeSizeFromLocChanged(linesChangedInCommit);
            for (String fileChanged : filesChangedInCommit) {
                CommitChangeSize commitChangeSize = new CommitChangeSize(commit.getCommitHash(), commit.getCommitDate(), commit.getCommiterName(), linesChangedInCommit, changeSize);
                List<CommitChangeSize> changesOnFile = mediumAndMajorChangesPattern.getOrDefault(fileChanged, new ArrayList<>());
                changesOnFile.add(commitChangeSize);
                mediumAndMajorChangesPattern.put(fileChanged, changesOnFile);
            }
        }
        return mediumAndMajorChangesPattern;
    }

    public Map<StaticDetectionKind, List<RuleViolationBean>> loadStaticAnalysisResults(File projectPath) {
        logger.info("AntiPatternsService - staticAnalyse() - calling repoService.analyzeClonedProject() - getting BASIC flaws");
        try {
            staticRuleViolations = repoService.analyzeClonedProject(projectPath.getPath());
        } catch (IOException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", projectPath, e));
        } catch (PMDException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", projectPath, e));
        }
        return staticRuleViolations;
    }

    public List<RuleViolationBean> getBasicAnalysis() {
        logger.info("AntiPatternsService - getBasicAnalysis() - calling repoService.getBasicAnalysis() ");
        return staticRuleViolations.get(BASIC);
    }

    public List<RuleViolationBean> getOptimizationViolations() {
        logger.info("AntiPatternsService - getOptimizationViolations() - calling repoService.getOptimizationViolations() ");
        return staticRuleViolations.get(OPTIMIZATION);
    }

    public List<RuleViolationBean> getCouplingViolations() {
        logger.info("AntiPatternsService - getCouplingViolations() - calling repoService.getCouplingViolations() ");
        return staticRuleViolations.get(COUPLING);
    }

    public List<RuleViolationBean> getCodesizeViolations() {
        logger.info("AntiPatternsService - getCodesizeViolations() - calling repoService.getCodesizeViolations() ");
        return staticRuleViolations.get(CODESIZE);
    }

    public List<RuleViolationBean> getDesignViolations() {
        logger.info("AntiPatternsService - getDesignViolations() - calling repoService.getDesignViolations() ");
        return staticRuleViolations.get(DESIGN);
    }

    private ChangeSize getChangeSizeFromLocChanged(int locChanged) {
        ChangeSize changeSize = null;
        if (locChanged >= properties.getMajorChangeSize()) {
            changeSize = ChangeSize.MAJOR;
        }
        if (locChanged >= properties.getMediumChangeSize() && locChanged < properties.getMajorChangeSize()) {
            changeSize = ChangeSize.MEDIUM;
        }
        if (locChanged < properties.getMediumChangeSize()) {
            changeSize = ChangeSize.SMALL;
        }
        return changeSize;
    }
}

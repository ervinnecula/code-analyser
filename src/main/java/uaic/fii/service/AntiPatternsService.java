package uaic.fii.service;

import net.sourceforge.pmd.PMDException;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitChangeSize;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.RuleViolationBean;
import uaic.fii.model.ChangeSize;
import uaic.fii.model.Period;
import uaic.fii.model.Properties;
import uaic.fii.model.StaticDetectionKind;

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static uaic.fii.model.StaticDetectionKind.BASIC;
import static uaic.fii.model.StaticDetectionKind.CODESIZE;
import static uaic.fii.model.StaticDetectionKind.COUPLING;
import static uaic.fii.model.StaticDetectionKind.DESIGN;
import static uaic.fii.model.StaticDetectionKind.OPTIMIZATION;
import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class AntiPatternsService {
    final static Logger logger = LoggerFactory.getLogger(AntiPatternsService.class);

    @Autowired
    private RepoService repoService;

    @Autowired
    private CommitService commitService;

    private Map<StaticDetectionKind, List<RuleViolationBean>> staticRuleViolations;

    public Map<String, List<CommitChangeSize>> detectMediumAndMajorChangesPattern(List<CommitDiffBean> commits) {
        Map<String, List<CommitChangeSize>> mediumAndMajorChangesPattern = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            for (DiffBean diff : commit.getDiffs()) {
                int linesChangedInDiff = 0;
                for (Edit edit : diff.getEdits()) {
                    linesChangedInDiff += edit.getLengthB() + edit.getLengthA();
                }
                ChangeSize changeSize = getChangeSizeFromLocChanged(linesChangedInDiff);
                if (changeSize.equals(ChangeSize.MAJOR) || changeSize.equals(ChangeSize.MEDIUM)) {
                    CommitChangeSize commitChangeSize = new CommitChangeSize(commit.getCommitHash(), commit.getCommitDate(), commit.getCommiterName(), linesChangedInDiff, changeSize);
                    List<CommitChangeSize> changesOnFile = mediumAndMajorChangesPattern.getOrDefault(diff.getFilePath(), new ArrayList<>());
                    changesOnFile.add(commitChangeSize);
                    mediumAndMajorChangesPattern.put(diff.getFilePath(), changesOnFile);
                }
            }
        }
        return mediumAndMajorChangesPattern;
    }

    public Integer getLocChangedRecently(List<CommitDiffBean> commits) {
        int linesOfCodeChangedRecently = 0;
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            if (period == Period.RECENT) {
                linesOfCodeChangedRecently += commitService.getLoCChangedInCommit(commit);
            }
        }
        return linesOfCodeChangedRecently;
    }

    public Map<String, Period> getPeriodOfTimeAllFiles(List<CommitDiffBean> commits) {
        Map<String, Period> filesAndPeriods = new HashMap<>();
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            for (DiffBean diff : commit.getDiffs()) {
                if (!diff.getFilePath().equals("/dev/null")) {
                    filesAndPeriods.put(diff.getFilePath(), period);
                    List<String> parents = buildParentsOfPath(diff.getFilePath());
                    for (String parent : parents) {
                        filesAndPeriods.putIfAbsent(parent, Period.PARENT);
                    }
                }
            }
        }
        return filesAndPeriods;
    }

    public Map<String, Period> getPeriodOfTimeContributors(List<CommitDiffBean> commits) {
        Map<String, Period> authorsAndPeriods = new HashMap<>();
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            authorsAndPeriods.put(commit.getCommiterName(), period);
        }

        return authorsAndPeriods;
    }

    public void loadStaticAnalysisResults(File projectPath) {
        logger.info("AntiPatternsService - staticAnalyse() - calling repoService.analyzeClonedProject() - getting BASIC flaws");
        try {
            staticRuleViolations = repoService.analyzeClonedProject(projectPath.getPath());
        } catch (IOException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", projectPath, e));
        } catch (PMDException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", projectPath, e));
        }
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
        if (locChanged >= commitService.getProperties().getMajorChangeSize()) {
            changeSize = ChangeSize.MAJOR;
        }
        if (locChanged >= commitService.getProperties().getMediumChangeSize() && locChanged < commitService.getProperties().getMajorChangeSize()) {
            changeSize = ChangeSize.MEDIUM;
        }
        if (locChanged < commitService.getProperties().getMediumChangeSize()) {
            changeSize = ChangeSize.SMALL;
        }
        return changeSize;
    }
}

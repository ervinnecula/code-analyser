package uaic.fii.service;

import net.sourceforge.pmd.PMDException;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitChangeSizeBean;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.FileOwnerPeriodBean;
import uaic.fii.bean.RuleViolationBean;
import uaic.fii.model.ChangeSize;
import uaic.fii.bean.OwnerLinesAddedBean;
import uaic.fii.model.Period;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static uaic.fii.model.ChangeSize.MAJOR;
import static uaic.fii.model.ChangeSize.MEDIUM;
import static uaic.fii.model.StaticDetectionKind.BASIC;
import static uaic.fii.model.StaticDetectionKind.CODESIZE;
import static uaic.fii.model.StaticDetectionKind.COUPLING;
import static uaic.fii.model.StaticDetectionKind.DESIGN;
import static uaic.fii.model.StaticDetectionKind.OPTIMIZATION;
import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class AntiPatternsService {

    private final static Logger logger = LoggerFactory.getLogger(AntiPatternsService.class);

    private final RepoService repoService;

    private final CommitService commitService;

    private final AuthorService authorService;

    @Autowired
    public AntiPatternsService(RepoService repoService, CommitService commitService, AuthorService authorService) {
        this.repoService = repoService;
        this.commitService = commitService;
        this.authorService = authorService;
    }

    public Map<String, List<CommitChangeSizeBean>> detectMediumAndMajorChangesPattern(List<CommitDiffBean> commits) {
        logger.info("AntiPatternsService - detectMediumAndMajorChangesPattern()");
        Map<String, List<CommitChangeSizeBean>> mediumAndMajorChangesPattern = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            for (DiffBean diff : commit.getDiffs()) {
                int linesChangedInDiff = 0;
                for (Edit edit : diff.getEdits()) {
                    linesChangedInDiff += edit.getLengthB() + edit.getLengthA();
                }
                ChangeSize changeSize = getChangeSizeFromLocChanged(linesChangedInDiff);
                if (changeSize.equals(MAJOR) || changeSize.equals(MEDIUM)) {
                    CommitChangeSizeBean commitChangeSize =
                            new CommitChangeSizeBean(commit.getCommitHash(), commit.getCommitDate(), commit.getCommitterName(), linesChangedInDiff, changeSize);
                    List<CommitChangeSizeBean> changesOnFile = mediumAndMajorChangesPattern.getOrDefault(diff.getFilePath(), new ArrayList<>());
                    changesOnFile.add(commitChangeSize);
                    mediumAndMajorChangesPattern.put(diff.getFilePath(), changesOnFile);
                }
            }
        }
        logger.info("AntiPatternsService - detectMediumAndMajorChangesPattern() ended");
        return mediumAndMajorChangesPattern;
    }

    public Integer getLocChangedRecently(List<CommitDiffBean> commits) {
        logger.info("AntiPatternsService - getLocChangedRecently() - getting number of LOC changed recently");

        int linesOfCodeChangedRecently = 0;
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            if (period == Period.RECENT) {
                linesOfCodeChangedRecently += commitService.getLoCChangedInCommit(commit);
            }
        }
        logger.info("AntiPatternsService - getLocChangedRecently() - calculated number of LOC changed recently");
        return linesOfCodeChangedRecently;
    }

    public Map<String, Period> getPeriodOfTimeAllFiles(List<CommitDiffBean> commits) {
        logger.info("AntiPatternsService - getPeriodOfTimeAllFiles() - loading period of time for all source files");
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
        logger.info("AntiPatternsService - getPeriodOfTimeAllFiles() - computed period of time for all source files");
        return filesAndPeriods;
    }

    public Map<String, Period> getPeriodOfTimeContributors(List<CommitDiffBean> commits) {
        return authorService.getAuthorsAndPeriods(commits);
    }

    public void loadStaticAnalysisResults(File projectPath) {
        logger.info("AntiPatternsService - staticAnalyse() - calling repoService.analyzeClonedProject() - getting BASIC flaws");
        try {
            repoService.analyzeClonedProject(projectPath.getPath());
        } catch (IOException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Git exception happened when opening folder %s. Full exception: %s", projectPath, e));
        } catch (PMDException e) {
            logger.error(format("AntiPatternsService - staticAnalyse() - Exception when running PMD over %s. Full exception: %s", projectPath, e));
        }
    }

    public List<FileOwnerPeriodBean> getOrphanedFiles(List <CommitDiffBean> commits) {
        logger.info("AntiPatternsService - staticAnalyse() - authorsAndPeriods retrieved file owners and periods of time");
        Map<String, Period> authorsAndPeriod = authorService.getAuthorsAndPeriods(commits);
        Map<String, OwnerLinesAddedBean> fileOwners = authorService.getFileOwners(commits);
        List<FileOwnerPeriodBean> orphanedFiles = new ArrayList<>();

        for (Map.Entry<String, OwnerLinesAddedBean> entry : fileOwners.entrySet()) {
            Period period = authorsAndPeriod.get(entry.getValue().getOwner());
            if (period.equals(Period.OLD) || period.equals(Period.VERY_OLD)) {
                orphanedFiles.add(new FileOwnerPeriodBean(entry.getKey(), entry.getValue().getOwner(), period));
            }
        }
        logger.info("AntiPatternsService- getOrphanedFiles() - Successfully retrieved potential orphan files");
        return orphanedFiles;
    }

    public List<RuleViolationBean> getBasicAnalysis() {
        logger.info("AntiPatternsService - getBasicAnalysis() - calling repoService.getBasicAnalysis()");
        return repoService.getRuleViolations().get(BASIC.getDetectedKind());
    }

    public List<RuleViolationBean> getOptimizationViolations() {
        logger.info("AntiPatternsService - getOptimizationViolations() - calling repoService.getOptimizationViolations()");
        return repoService.getRuleViolations().get(OPTIMIZATION.getDetectedKind());
    }

    public List<RuleViolationBean> getCouplingViolations() {
        logger.info("AntiPatternsService - getCouplingViolations() - calling repoService.getCouplingViolations()");
        return repoService.getRuleViolations().get(COUPLING.getDetectedKind());
    }

    public List<RuleViolationBean> getCodesizeViolations() {
        logger.info("AntiPatternsService - getCodesizeViolations() - calling repoService.getCodesizeViolations()");
        return repoService.getRuleViolations().get(CODESIZE.getDetectedKind());
    }

    public List<RuleViolationBean> getDesignViolations() {
        logger.info("AntiPatternsService - getDesignViolations() - calling repoService.getDesignViolations()");
        return repoService.getRuleViolations().get(DESIGN.getDetectedKind());
    }

    private ChangeSize getChangeSizeFromLocChanged(int locChanged) {
        ChangeSize changeSize = null;
        if (locChanged >= commitService.getProperties().getMajorChangeSize()) {
            changeSize = MAJOR;
        }
        if (locChanged >= commitService.getProperties().getMediumChangeSize() && locChanged < commitService.getProperties().getMajorChangeSize()) {
            changeSize = MEDIUM;
        }
        if (locChanged < commitService.getProperties().getMediumChangeSize()) {
            changeSize = ChangeSize.SMALL;
        }
        return changeSize;
    }
}

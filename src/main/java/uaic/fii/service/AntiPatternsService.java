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
import java.util.ListIterator;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static uaic.fii.model.ChangeSize.MAJOR;
import static uaic.fii.model.ChangeSize.MEDIUM;
import static uaic.fii.model.ChangeSize.SMALL;
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

    private final PropertiesService propertiesService;

    @Autowired
    public AntiPatternsService(RepoService repoService, CommitService commitService, AuthorService authorService, PropertiesService propertiesService) {
        this.repoService = repoService;
        this.commitService = commitService;
        this.authorService = authorService;
        this.propertiesService = propertiesService;
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
                    CommitChangeSizeBean commitChangeSize = new CommitChangeSizeBean(commit.getCommitHash(),
                                                                                        commit.getCommitDate(),
                                                                                        commit.getCommitterName(),
                                                                                        linesChangedInDiff,
                                                                                        changeSize,
                                                                                        commitService.isIncreaseCommit(commit));
                    List<CommitChangeSizeBean> changesOnFile = mediumAndMajorChangesPattern.getOrDefault(diff.getFilePath(), new ArrayList<>());
                    changesOnFile.add(commitChangeSize);
                    mediumAndMajorChangesPattern.put(diff.getFilePath(), changesOnFile);
                }
            }
        }
        logger.info("AntiPatternsService - detectMediumAndMajorChangesPattern() ended");
        return mediumAndMajorChangesPattern;
    }

    public Map<String, Period> getPeriodOfTimeAllFiles(List<CommitDiffBean> commits, boolean withParents) {
        logger.info("AntiPatternsService - getPeriodOfTimeAllFiles() - loading period of time for all source files");
        Map<String, Period> filesAndPeriods = new HashMap<>();
        ListIterator<CommitDiffBean> commitIterator = commits.listIterator(commits.size());
        CommitDiffBean commit;

        while (commitIterator.hasPrevious()) {
            commit = commitIterator.previous();
            Period period = commitService.getPeriodOfTimeCommit(commit);
            for (DiffBean diff : commit.getDiffs()) {
                if (!diff.getFilePath().equals("/dev/null")) {
                    filesAndPeriods.put(diff.getFilePath(), period);
                    if(withParents) {
                        List<String> parents = buildParentsOfPath(diff.getFilePath());
                        for (String parent : parents) {
                            filesAndPeriods.putIfAbsent(parent, Period.PARENT);
                        }
                    }
                }
            }
        }
        logger.info("AntiPatternsService - getPeriodOfTimeAllFiles() - computed period of time for all source files");
        return filesAndPeriods;
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

    public List<FileOwnerPeriodBean> getOrphanedFiles(List<CommitDiffBean> commits) {
        logger.info("AntiPatternsService - staticAnalyse() - authorsAndPeriods retrieved file owners and periods of time");
        Map<String, Period> authorsAndPeriod = authorService.getAuthorsAndPeriods(commits);
        Map<String, OwnerLinesAddedBean> fileOwners = authorService.getFileOwners(commits, false, false);
        List<FileOwnerPeriodBean> orphanedFiles = new ArrayList<>();

        for (Map.Entry<String, OwnerLinesAddedBean> entry : fileOwners.entrySet()) {
            Period periodOfOwner = authorsAndPeriod.get(entry.getValue().getOwner());
            if (periodOfOwner.equals(Period.OLD) || periodOfOwner.equals(Period.VERY_OLD)) {
                orphanedFiles.add(new FileOwnerPeriodBean(entry.getKey(), entry.getValue().getOwner(), periodOfOwner));
            }
        }
        logger.info("AntiPatternsService - getOrphanedFiles() - Successfully retrieved potential orphan files");
        return orphanedFiles;
    }

    public Map<String, Period> getFilteredFilesByPeriod(List<CommitDiffBean> commits, List<Period> acceptedPeriods) {
        Map<String, Period> filteredMap = new HashMap<>();
        Map<String, Period> fileNamePeriodMap = getPeriodOfTimeAllFiles(commits, false);
        for (Map.Entry<String, Period> entry : fileNamePeriodMap.entrySet()) {
            if (acceptedPeriods.contains(entry.getValue())) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredMap;
    }

    public Map<String, Period> getFilteredContributorsByPeriod(List<CommitDiffBean> commits, List<Period> acceptedPeriods) {
        Map<String, Period> filteredMap = new HashMap<>();
        Map<String, Period> contributorsPeriodMap = authorService.getAuthorsAndPeriods(commits);
        for (Map.Entry<String, Period> entry : contributorsPeriodMap.entrySet()) {
            if (acceptedPeriods.contains(entry.getValue())) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredMap;
    }

    public Map<Integer, List<CommitChangeSizeBean>> getIncreaseDecreaseSpikeCycles(List<CommitDiffBean> commits) {
        Map<Integer, List<CommitChangeSizeBean>> spikeCycles = new HashMap<>();
        int i = 0;
        int index = 0;
        while (i < commits.size() - 2) {
            CommitDiffBean first = commits.get(i);
            CommitDiffBean second = commits.get(i+1);
            CommitDiffBean third = commits.get(i+2);

            boolean firstIsIncreaseCommit = commitService.isIncreaseCommit(first);
            boolean secondIsIncreaseCommit = commitService.isIncreaseCommit(second);
            boolean thirdIsIncreaseCommit = commitService.isIncreaseCommit(third);

            if (firstIsIncreaseCommit && !secondIsIncreaseCommit && thirdIsIncreaseCommit || !firstIsIncreaseCommit && secondIsIncreaseCommit && !thirdIsIncreaseCommit) {
                ChangeSize firstChangeSize = getChangeSizeFromLocChanged(commitService.getLoCChangedInCommit(first));
                ChangeSize secondChangeSize = getChangeSizeFromLocChanged(commitService.getLoCChangedInCommit(second));
                ChangeSize thirdChangeSize = getChangeSizeFromLocChanged(commitService.getLoCChangedInCommit(third));

                    if(firstChangeSize == secondChangeSize && secondChangeSize == thirdChangeSize && thirdChangeSize == MAJOR) {
                        index++;
                        spikeCycles.put(index, asList(
                                new CommitChangeSizeBean(first.getCommitHash(), first.getCommitDate(), first.getCommitterName(), commitService.getLoCChangedInCommit(first), firstChangeSize, firstIsIncreaseCommit ),
                                new CommitChangeSizeBean(second.getCommitHash(), second.getCommitDate(), second.getCommitterName(), commitService.getLoCChangedInCommit(second), secondChangeSize, secondIsIncreaseCommit),
                                new CommitChangeSizeBean(third.getCommitHash(), third.getCommitDate(), third.getCommitterName(), commitService.getLoCChangedInCommit(third), thirdChangeSize, thirdIsIncreaseCommit)));
                    }
            }
            i++;
        }
        return spikeCycles;
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
        Map<String, Integer> propertiesMap = propertiesService.getPropertiesMap();
        ChangeSize changeSize = SMALL;
        if (locChanged >= propertiesMap.get("majorChangeSize")) {
            changeSize = MAJOR;
        }
        if (locChanged >= propertiesMap.get("mediumChangeSize") &&
                locChanged < propertiesMap.get("majorChangeSize")) {
            changeSize = MEDIUM;
        }
        if (locChanged < propertiesMap.get("mediumChangeSize")) {
            changeSize = SMALL;
        }
        return changeSize;
    }
}

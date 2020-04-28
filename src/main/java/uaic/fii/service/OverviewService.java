package uaic.fii.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.model.Language;
import uaic.fii.model.Period;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static uaic.fii.model.Period.RECENT;

@Service
public class OverviewService {

    private final static Logger logger = LoggerFactory.getLogger(OverviewService.class);

    private final AntiPatternsService antiPatternsService;

    private final CommitService commitService;

    @Autowired
    public OverviewService(AntiPatternsService antiPatternsService, CommitService commitService) {
        this.antiPatternsService = antiPatternsService;
        this.commitService = commitService;
    }

    public String getLocByLanguage(File projectPath) {
        Map<Language, List<File>> languageFilesMap;
        Map<Language, Long> locByLanguageMap = new HashMap<>();
        languageFilesMap = getFileListByExtension(projectPath);
        try {
            for (Map.Entry<Language, List<File>> languageFiles : languageFilesMap.entrySet()) {
                long count = 0;
                List<File> files = languageFiles.getValue();
                for (File file : files) {
                    count += countLocOfFile(file);
                }
                locByLanguageMap.put(languageFiles.getKey(), count);
            }
        } catch (IOException e) {
            logger.error("OverviewService - Could not open file ", e);
        }
        return ChartDataStringWriters.writeLanguageLocCount(locByLanguageMap);
    }

    public String getNumberOfFilesByLanguage(File projectPath) {
        Map<Language, List<File>> languageFilesMap = getFileListByExtension(projectPath);
        Map<Language, Integer> numberOfFilesByLanguageMap = new HashMap<>();

        for (Map.Entry<Language, List<File>> languageFiles : languageFilesMap.entrySet()) {
            numberOfFilesByLanguageMap.put(languageFiles.getKey(), languageFiles.getValue().size());
        }
        return ChartDataStringWriters.writeNumberOfFilesPerLanguage(numberOfFilesByLanguageMap);
    }

    public String countRecentFilesChanged(List<CommitDiffBean> commits) {
        return Integer.toString(antiPatternsService.getFilteredFilesByPeriod(commits, singletonList(RECENT)).size());
    }

    public String countRecentLinesChanged(List<CommitDiffBean> commits) {
        logger.info("OverviewService - countRecentLinesChanged() - getting number of LOC changed recently");

        int linesOfCodeChangedRecently = 0;
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            if (period == RECENT) {
                linesOfCodeChangedRecently += commitService.getLoCChangedInCommit(commit);
            }
        }
        logger.info("OverviewService - countRecentLinesChanged() - calculated number of LOC changed recently");
        return Integer.toString(linesOfCodeChangedRecently);
    }

    public String countRecentContributors(List<CommitDiffBean> commits) {
        return Integer.toString(antiPatternsService.getFilteredContributorsByPeriod(commits, singletonList(RECENT)).size());
    }

    public Map<String, Integer> getActiveContributorsLoC(List<CommitDiffBean> commits) {
        Map<String, Integer> contributorsLoC = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            int locMap = contributorsLoC.getOrDefault(commit.getCommitterName(), 0);
            contributorsLoC.put(commit.getCommitterName(), locMap + commitService.getLoCChangedInCommit(commit));
        }

        return entriesSortedByValuesInteger(contributorsLoC);
    }

    public Map<String, Integer> getActiveContributorsFilesTouched(List<CommitDiffBean> commits) {
        Map<String, Integer> contributorsFilesTouched = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            int filesTouched = contributorsFilesTouched.getOrDefault(commit.getCommitterName(), 0);
            contributorsFilesTouched.put(commit.getCommitterName(), filesTouched + commit.getDiffs().size());
        }

        return entriesSortedByValuesInteger(contributorsFilesTouched);
    }

    public Map<String, String> getMostInvolvedContributors(List<CommitDiffBean> commits) {
        Map<String, Integer> mostInvolvedContributors = getActiveContributorsFilesTouched(commits);
        Map<String, Float> result = new HashMap<>();

        int totalNumberOfFiles = 0;
        for (Integer value : mostInvolvedContributors.values()) {
            totalNumberOfFiles += value;
        }
        for (Map.Entry<String, Integer> entry : mostInvolvedContributors.entrySet()) {
            float percentage = (float) entry.getValue() / totalNumberOfFiles * 100;
            result.put(entry.getKey(), percentage);
        }

        return entriesSortedByValuesFloat(result);
    }

    private int countLocOfFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

    private Map<Language, List<File>> getFileListByExtension(File projectPath) {
        Map<Language, List<File>> languageFilesMap = new HashMap<>();
        Iterator<File> files;
        for (Language language : Language.values()) {
            File[] childFiles = projectPath.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        files = FileUtils.iterateFiles(childFile, new String[]{language.getExtension()}, true);
                        List<File> filesForExtension = languageFilesMap.getOrDefault(language, new ArrayList<>());
                        Iterator<File> it = files;
                        while (it.hasNext()) {
                            File file = it.next();
                            filesForExtension.add(file);
                        }
                        languageFilesMap.put(language, filesForExtension);
                    }
                }
            }
        }
        return languageFilesMap;
    }

    public static LinkedHashMap<String, Integer> entriesSortedByValuesInteger(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static LinkedHashMap<String, String> entriesSortedByValuesFloat(Map<String, Float> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.format ("%,.2f", entry.getValue()),
                        (e1, e2) -> e1, LinkedHashMap::new));

    }
}

package uaic.fii.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.Language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OverviewService {

    final static Logger logger = LoggerFactory.getLogger(RepoService.class);

    @Autowired
    private AntiPatternsService antiPatternsService;

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
        return Integer.toString(antiPatternsService.getPeriodOfTimeFiles(commits).values().size());
    }

    public String countRecentLinesChanged(List<CommitDiffBean> commits) {
        return Integer.toString(antiPatternsService.getLocChangedRecently(commits));
    }

    public String countRecentContributors(List<CommitDiffBean> commits) {
        return Integer.toString(antiPatternsService.getPeriodOfTimeContributors(commits).values().size());
    }

    public Map<String, Integer> getActiveContributorsLoC(List<CommitDiffBean> commits) {
        Map<String, Integer> contributorsLoC = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            int linesChangedInCommit = 0;

            for (DiffBean diff : commit.getDiffs()) {
                for (Edit edit : diff.getEdits()) {
                    linesChangedInCommit += edit.getLengthB() + edit.getLengthA();
                }
            }
            int locMap = contributorsLoC.getOrDefault(commit.getCommiterName(), 0);
            contributorsLoC.put(commit.getCommiterName(), locMap + linesChangedInCommit);
        }
        return contributorsLoC;
    }

    public Map<String, Integer> getActiveContributorsFilesTouched(List<CommitDiffBean> commits) {
        Map<String, Integer> contributorsFilesTouched = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            int filesTouched = contributorsFilesTouched.getOrDefault(commit.getCommiterName(), 0);
            contributorsFilesTouched.put(commit.getCommiterName(), filesTouched + commit.getDiffs().size());
        }

        return contributorsFilesTouched;
    }

    public Map<String, String> getMostInvolvedContributors(List<CommitDiffBean> commits) {
        Map<String, Integer> mostInvolvedContributors = getActiveContributorsFilesTouched(commits);
        Map<String, String> result = new HashMap<>();

        int totalNumberOfFiles = 0;
        for (Integer value : mostInvolvedContributors.values()) {
            totalNumberOfFiles += value;
        }
        for (Map.Entry<String, Integer> entry : mostInvolvedContributors.entrySet()) {
            float percentage = (float) entry.getValue() / totalNumberOfFiles * 100;
            result.put(entry.getKey(), String.format ("%,.2f", percentage));
        }

        return result;
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
}

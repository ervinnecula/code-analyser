package uaic.fii.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uaic.fii.model.Language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class OverviewService {

    final static Logger logger = LoggerFactory.getLogger(RepoService.class);

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
        return ChartsUtils.writeLanguageLocCount(locByLanguageMap);
    }

    public String getNumberOfFilesByLanguage(File projectPath) {
        Map<Language, List<File>> languageFilesMap = getFileListByExtension(projectPath);
        Map<Language, Integer> numberOfFilesByLanguageMap = new HashMap<>();

        for (Map.Entry<Language, List<File>> languageFiles : languageFilesMap.entrySet()) {
            numberOfFilesByLanguageMap.put(languageFiles.getKey(), languageFiles.getValue().size());
        }
        return ChartsUtils.writeNumberOfFilesPerLanguage(numberOfFilesByLanguageMap);
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

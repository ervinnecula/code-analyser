package uaic.fii.service;

import uaic.fii.bean.DateCountBean;
import uaic.fii.bean.DateHashSetBean;
import uaic.fii.bean.PathEditBean;
import uaic.fii.model.Language;
import uaic.fii.model.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartDataStringWriters {
    private static final String eol = System.getProperty("line.separator");

    static List<String> buildParentsOfPath(String path) {
        List<String> parentsOfPath = new ArrayList<>();
        String[] pathSections = path.split("/");
        if (pathSections.length > 1) {
            String base = pathSections[0];
            parentsOfPath.add(base);
            for (int i = 1; i < pathSections.length; i++) {
                base = base.concat("/").concat(pathSections[i]);
                parentsOfPath.add(base);
            }
        }
        return parentsOfPath;
    }

    public static String writeStringStringIntegerMapToCSVFormat(Map<String, DateCountBean> diffsPerFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, DateCountBean> entry : diffsPerFilePath.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue().getDate())
                    .append(',')
                    .append(entry.getValue().getCount()).append(eol);
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    public static String writeStringIntegerMapToCSVFormat(Map<String, Integer> diffsPerFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : diffsPerFilePath.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue()).append(eol);
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    public static String writeLinesAddedRemovedToCSVFormat(Map<String, PathEditBean> locChangePerFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, PathEditBean> entry : locChangePerFilePath.entrySet()) {
            PathEditBean pathEditBean = entry.getValue();
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(pathEditBean.getLinesAdded())
                    .append(',')
                    .append(pathEditBean.getLinesRemoved())
                    .append(eol);
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    public static String writeHeatMapContributorsToCSVFormat(Map<String, DateHashSetBean> contributorsPerFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, DateHashSetBean> entry : contributorsPerFilePath.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue().getDate())
                    .append(',')
                    .append(entry.getValue().getListOfContributors().size())
                    .append(eol);
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    static String writeLanguageLocCount(Map<Language, Long> languageLocCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Language, Long> entry : languageLocCount.entrySet()) {
            if (entry.getValue() != 0) {
                stringBuilder.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(eol);
            }
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    static String writeNumberOfFilesPerLanguage(Map<Language, Integer> numberOfFilesPerLanguage) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Language, Integer> entry : numberOfFilesPerLanguage.entrySet()) {
            if (entry.getValue() != 0) {
                stringBuilder.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(eol);
            }
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    static String writePeriodOfTimeFilesToCSVFormat(Map<String, Period> periodOfTimeMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Period> entry : periodOfTimeMap.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(eol);
        }
        if (stringBuilder.length() != 0)
            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }
}

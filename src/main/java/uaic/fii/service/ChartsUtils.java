package uaic.fii.service;

import uaic.fii.bean.PathEditBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ChartsUtils {
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

    static String writeHeatMapCommitsToCSVFormat(Map<String, Integer> diffsPerFilePath) {
        String eol = System.getProperty("line.separator");
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : diffsPerFilePath.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(Integer.toString(entry.getValue())).append(eol);
        }
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    static String writeLocDataToCSVFormat(Map<String, PathEditBean> locChangePerFilePath) {
        String eol = System.getProperty("line.separator");
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();
        for (Map.Entry<String, PathEditBean> entry : locChangePerFilePath.entrySet()) {
            PathEditBean pathEditBean = entry.getValue();
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(pathEditBean.getLinesAdded())
                    .append(',')
                    .append(pathEditBean.getLinesRemoved())
                    .append(eol);
        }
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }

    static String writeHeatMapContributorsToCSVFormat(Map<String, Set<String>> contributorsPerFilePath) {
        String eol = System.getProperty("line.separator");
        StringBuilder stringBuilder;

        stringBuilder = new StringBuilder();
        for (Map.Entry<String, Set<String>> entry : contributorsPerFilePath.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue().size())
                    .append(eol);
        }
        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), "");
        return stringBuilder.toString();
    }
}

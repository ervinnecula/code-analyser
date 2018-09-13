package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class HeatMapService {

    public String getPathDiffsCsvFile(List<CommitDiffBean> commitList) {
        Map<String, Integer> diffsPerFilePath = new TreeMap<>();

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                if (diff.getFilePath().substring(0, 4).equals("src/")) {
                    int currentCount = diffsPerFilePath.getOrDefault(diff.getFilePath(), 0);
                    List<String> parents = buildParentsOfPath(diff.getFilePath());
//                    parents.stream().map(p -> diffsPerFilePath.put(p, 0)).collect(Collectors.toList());
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, 0);
                    }
                    diffsPerFilePath.put(diff.getFilePath(), currentCount + 1);
                }
            }
        }
        return writeMapToCSVFormat(diffsPerFilePath);
    }

    private List<String> buildParentsOfPath(String path) {
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

    private String writeMapToCSVFormat(Map<String, Integer> diffsPerFilePath) {
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
}

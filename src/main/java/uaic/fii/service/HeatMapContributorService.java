package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static uaic.fii.service.ChartsUtils.buildParentsOfPath;

@Service
public class HeatMapContributorService {

    private final String SRC_PATH = "src/";

    public String getPathContributorsCsvFile(List<CommitDiffBean> commitList) {
        Map<String, Set<String>> diffsPerFilePath = new TreeMap<>();

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                if (diff.getFilePath().toLowerCase().contains(SRC_PATH)) {
                    Set<String> listOfContributors = diffsPerFilePath.getOrDefault(diff.getFilePath(), new HashSet<>());
                    List<String> parents = buildParentsOfPath(diff.getFilePath());
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new HashSet<>());
                    }
                    listOfContributors.add(commit.getCommiterName());
                    diffsPerFilePath.put(diff.getFilePath(), listOfContributors);
                }
            }
        }
        return ChartsUtils.writeHeatMapContributorsToCSVFormat(diffsPerFilePath);
    }

}

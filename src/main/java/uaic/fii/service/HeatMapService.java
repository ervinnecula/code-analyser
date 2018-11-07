package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uaic.fii.service.ChartsUtils.buildParentsOfPath;

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
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, 0);
                    }
                    diffsPerFilePath.put(diff.getFilePath(), currentCount + 1);
                }
            }
        }
        return ChartsUtils.writeHeatMapDataToCSVFormat(diffsPerFilePath);
    }

}

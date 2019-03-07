package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateCountBean;
import uaic.fii.bean.DiffBean;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uaic.fii.service.ChartsUtils.buildParentsOfPath;

@Service
public class HeatMapCommitService {

    public String getPathDiffsCsvFile(List<CommitDiffBean> commitList) {
        String SRC_PATH = "src/";
        Map<String, DateCountBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                int currentCount = 0;
                if (diff.getFilePath().toLowerCase().contains(SRC_PATH)) {
                    if (diffsPerFilePath.containsKey(diff.getFilePath())) {
                        currentCount = diffsPerFilePath.get(diff.getFilePath()).getCount();
                    }
                    List<String> parents = buildParentsOfPath(diff.getFilePath());
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new DateCountBean(dateFormat.format(commit.getCommitDate()), 0));
                    }
                    diffsPerFilePath.put(diff.getFilePath(), new DateCountBean(dateFormat.format(commit.getCommitDate()), currentCount + 1));
                }
            }
        }
        return ChartsUtils.writeStringStringIntegerMapToCSVFormat(diffsPerFilePath);
    }

}

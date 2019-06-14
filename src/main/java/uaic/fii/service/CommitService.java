package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateCountBean;
import uaic.fii.bean.DiffBean;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class CommitService {

    public Map<String, DateCountBean> getPathDiffsCsvFile(List<CommitDiffBean> commitList) {
        Map<String, DateCountBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                int currentCount = 0;
                String filePathComplete = "__project__/".concat(diff.getFilePath());
                if (!filePathComplete.equals("__project__//dev/null")) {
                    if (diffsPerFilePath.containsKey(filePathComplete)) {
                        currentCount = diffsPerFilePath.get(filePathComplete).getCount();
                    }
                    List<String> parents = buildParentsOfPath(filePathComplete);
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new DateCountBean(dateFormat.format(commit.getCommitDate()), 0));
                    }
                    diffsPerFilePath.put(filePathComplete, new DateCountBean(dateFormat.format(commit.getCommitDate()), currentCount + 1));
                }
            }
        }
        return diffsPerFilePath;
    }

}

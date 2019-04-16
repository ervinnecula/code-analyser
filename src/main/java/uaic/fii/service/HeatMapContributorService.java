package uaic.fii.service;

import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateHashSetBean;
import uaic.fii.bean.DiffBean;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static uaic.fii.service.ChartsUtils.buildParentsOfPath;

@Service
public class HeatMapContributorService {

    public String getPathContributorsCsvFile(List<CommitDiffBean> commitList) {
        Map<String, DateHashSetBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                String filePathComplete = "project_/".concat(diff.getFilePath());
                if (!filePathComplete.equals("project_//dev/null")) {
                    Set<String> listOfContributors = new HashSet<>();

                    if (diffsPerFilePath.containsKey(filePathComplete)) {
                        listOfContributors = diffsPerFilePath.get(filePathComplete).getListOfContributors();
                    }
                    listOfContributors.add(commit.getCommiterName());

                    List<String> parents = buildParentsOfPath(filePathComplete);
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new DateHashSetBean(dateFormat.format(commit.getCommitDate()), new HashSet<>()));
                    }
                    diffsPerFilePath.put(filePathComplete, new DateHashSetBean(dateFormat.format(commit.getCommitDate()), listOfContributors));
                }
            }
        }
        return ChartsUtils.writeHeatMapContributorsToCSVFormat(diffsPerFilePath);
    }

}

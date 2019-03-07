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
        String SRC_PATH = "src/";
        Map<String, DateHashSetBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                Set<String> listOfContributors = new HashSet<>();
                if (diff.getFilePath().toLowerCase().contains(SRC_PATH)) {
                    if (diffsPerFilePath.containsKey(diff.getFilePath())) {
                        listOfContributors = diffsPerFilePath.get(diff.getFilePath()).getListOfContributors();
                    }
                    List<String> parents = buildParentsOfPath(diff.getFilePath());
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new DateHashSetBean(dateFormat.format(commit.getCommitDate()), new HashSet<>()));
                    }
                    listOfContributors.add(commit.getCommiterName());
                    diffsPerFilePath.put(diff.getFilePath(), new DateHashSetBean(dateFormat.format(commit.getCommitDate()), listOfContributors));
                }
            }
        }
        return ChartsUtils.writeHeatMapContributorsToCSVFormat(diffsPerFilePath);
    }

}

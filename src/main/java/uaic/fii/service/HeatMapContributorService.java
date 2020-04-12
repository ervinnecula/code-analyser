package uaic.fii.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class HeatMapContributorService {

    private static final Logger logger = LoggerFactory.getLogger(HeatMapContributorService.class);

    public Map<String, DateHashSetBean> getPathContributorsCsvFile(List<CommitDiffBean> commitList) {
        logger.info("HeatMapContributorService - getPathContributorsCsvFile() - getting data for heat map contributor");
        Map<String, DateHashSetBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Set<String> listOfContributors;

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                String filePathComplete = "__project__/".concat(diff.getFilePath());
                if (!filePathComplete.equals("__project__//dev/null")) {
                    listOfContributors = new HashSet<>();

                    if (diffsPerFilePath.containsKey(filePathComplete)) {
                        listOfContributors = diffsPerFilePath.get(filePathComplete).getListOfContributors();
                    }
                    listOfContributors.add(commit.getCommitterName());

                    List<String> parents = buildParentsOfPath(filePathComplete);
                    for (String parent : parents) {
                        diffsPerFilePath.put(parent, new DateHashSetBean(dateFormat.format(commit.getCommitDate()), new HashSet<>()));
                    }
                    diffsPerFilePath.put(filePathComplete, new DateHashSetBean(dateFormat.format(commit.getCommitDate()), listOfContributors));
                }
            }
        }
        logger.info("HeatMapContributorService - getPathContributorsCsvFile() - loaded data for heat map contributor");
        return diffsPerFilePath;
    }

}

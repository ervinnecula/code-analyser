package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateCountBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.Period;
import uaic.fii.model.Properties;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class CommitService {

    @Autowired
    private PropertiesService propertiesService;

    private Properties properties;

    public void loadProperties(String userName) {
        properties = propertiesService.getPropertiesByUserId(userName);
    }

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

    public int getLoCChangedInCommit(CommitDiffBean commit) {
        int linesChangedInCommit = 0;

        for (DiffBean diff : commit.getDiffs()) {
            if (!diff.getFilePath().equals("/dev/null"))
                for (Edit edit : diff.getEdits()) {
                    linesChangedInCommit += edit.getLengthB() + edit.getLengthA();
                }
        }
        return linesChangedInCommit;
    }

    public int getAddedLinesInCommit(CommitDiffBean commit) {
        int linesAddedInCommit = 0;

        for (DiffBean diff : commit.getDiffs()) {
            if (!diff.getFilePath().equals("/dev/null"))
                for (Edit edit : diff.getEdits()) {
                    linesAddedInCommit += edit.getLengthB();
                }
        }
        return linesAddedInCommit;
    }

    public int getRemovedLinesInCommit(CommitDiffBean commit) {
        int linesRemovedInCommit = 0;

        for (DiffBean diff : commit.getDiffs()) {
            if (!diff.getFilePath().equals("/dev/null"))
                for (Edit edit : diff.getEdits()) {
                    linesRemovedInCommit += edit.getLengthA();
                }
        }
        return linesRemovedInCommit;
    }

    public Period getPeriodOfTimeCommit(CommitDiffBean commit) {
        Period period;
        long daysBetween = ChronoUnit.DAYS.between(commit.getCommitDate().toInstant(), new Date().toInstant());

        if (daysBetween < properties.getPeriodOfTime()) {
            period = Period.RECENT;
        } else if (daysBetween >= properties.getPeriodOfTime() && daysBetween < properties.getPeriodOfTime() * 2) {
            period = Period.MEDIUM;
        } else if (daysBetween >= properties.getPeriodOfTime() * 2 && daysBetween < properties.getPeriodOfTime() * 6) {
            period = Period.OLD;
        } else {
            period = Period.VERY_OLD;
        }
        return period;
    }

    public Properties getProperties() {
        return properties;
    }
}

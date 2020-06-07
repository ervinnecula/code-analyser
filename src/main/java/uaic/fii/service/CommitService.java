package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateCountBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.Period;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static uaic.fii.service.ChartDataStringWriters.buildParentsOfPath;

@Service
public class CommitService {

    private final static Logger logger = LoggerFactory.getLogger(CommitService.class);

    private final PropertiesService propertiesService;

    @Autowired
    public CommitService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public Map<String, DateCountBean> getPathDiffsCsvFile(List<CommitDiffBean> commitList) {
        logger.info("CommitService - getPathDiffsCsvFile() - getting data for heat map commits");
        Map<String, DateCountBean> diffsPerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            for (DiffBean diff : commit.getDiffs()) {
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
        logger.info("CommitService - getPathDiffsCsvFile() - loaded data for heat map commits");
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
        long daysBetween = ChronoUnit.DAYS.between(commit.getCommitDate().toInstant(), new Date().toInstant());
        int periodOfTime = propertiesService.getPropertiesMap().get("periodOfTime");

        Period period;
        if (daysBetween < periodOfTime) {
            period = Period.RECENT;
        } else if (daysBetween >= periodOfTime && daysBetween < periodOfTime * 2) {
            period = Period.MEDIUM;
        } else if (daysBetween >= periodOfTime * 2 && daysBetween < periodOfTime * 6) {
            period = Period.OLD;
        } else {
            period = Period.VERY_OLD;
        }
        return period;
    }

    public boolean isIncreaseCommit(CommitDiffBean commit) {
        int linesRemovedInCommit = 0;
        int linesAddedInCommit = 0;

        for (DiffBean diff : commit.getDiffs()) {
            if (!diff.getFilePath().equals("/dev/null"))
                for (Edit edit : diff.getEdits()) {
                    linesRemovedInCommit += edit.getLengthA();
                    linesAddedInCommit += edit.getLengthB();
                }
        }
        return linesAddedInCommit > linesRemovedInCommit;
    }

}

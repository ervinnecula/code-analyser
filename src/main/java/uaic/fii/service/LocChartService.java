package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.PathEditBean;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class LocChartService {

    public String getAddRemoveLinesOverTime(List<CommitDiffBean> commitList) {
        Map<String, PathEditBean> locChangePerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                for (Edit edits : diff.getEdits()) {
                    PathEditBean pathEditBean = locChangePerFilePath.get(dateFormat.format(commit.getCommitDate()));
                    if (pathEditBean != null) {
                        pathEditBean.setLinesAdded(pathEditBean.getLinesAdded() + edits.getLengthB());
                        pathEditBean.setLinesRemoved(pathEditBean.getLinesRemoved() + edits.getLengthA());

                    } else {
                        pathEditBean = new PathEditBean(edits.getLengthA(), edits.getLengthB());
                    }
                    locChangePerFilePath.put(dateFormat.format(commit.getCommitDate()), pathEditBean);
                }
            }
        }
        return ChartsUtils.writeLinesAddedRemovedToCSVFormat(locChangePerFilePath);
    }

    public String getLOCOverTime(List<CommitDiffBean> commitList, Date startDate, Date endDate) {
        Map<String, Integer> locChangePerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Collections.reverse(commitList);
        int loc = 0;
        for (CommitDiffBean commit : commitList) {
            Date commitDate = commit.getCommitDate();
            if ((commitDate.after(startDate) || commitDate.equals(startDate))
                    && (commitDate.before(endDate) || commitDate.equals(endDate))) {
                List<DiffBean> diffs = commit.getDiffs();
                for (DiffBean diff : diffs) {
                    int linesAddedInFile = 0;
                    for (Edit edit : diff.getEdits()) {
                        linesAddedInFile += edit.getLengthB();
                        linesAddedInFile -= edit.getLengthA();
                    }
                    loc += linesAddedInFile;
                }
                locChangePerFilePath.put(dateFormat.format(commit.getCommitDate()), loc);
            }
        }
        return ChartsUtils.writeStringIntegerMapToCSVFormat(locChangePerFilePath);
    }
}

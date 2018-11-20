package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.PathEditBean;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class LocChartService {

    public String getLocProgressOverTime(List<CommitDiffBean> commitList) {
        Map<String, PathEditBean> locChangePerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (CommitDiffBean commit : commitList) {
            List<DiffBean> diffs = commit.getDiffs();
            for (DiffBean diff : diffs) {
                if (diff.getFilePath().substring(0, 4).equals("src/")) {
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
        }
        return ChartsUtils.writeLocDataToCSVFormat(locChangePerFilePath);
    }
}

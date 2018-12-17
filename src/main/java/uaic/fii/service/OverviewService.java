package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OverviewService {
    private final String SRC_PATH = "src/";

    @Autowired
    private LocChartService locChartService;

    public String getRepoOverviewData(List<CommitDiffBean> commitList, Date startDate, Date endDate, String language) {
        Map<String, PathEditBean> locChangePerFilePath = new TreeMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        locChartService.getLOCOverTime(commitList, startDate, endDate);
        return "asd";
    }
}

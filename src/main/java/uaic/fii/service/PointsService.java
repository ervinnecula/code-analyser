package uaic.fii.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateHashSetBean;
import uaic.fii.model.Period;

import java.util.Collection;
import java.util.List;

@Service
public class PointsService {

    private HeatMapContributorService heatMapContributorService;

    private CommitService commitService;

    private AntiPatternsService antiPatternsService;

    @Autowired
    public PointsService(HeatMapContributorService heatMapContributorService, CommitService commitService, AntiPatternsService antiPatternsService) {
        this.heatMapContributorService = heatMapContributorService;
        this.commitService = commitService;
        this.antiPatternsService = antiPatternsService;
    }

    public long getFewCommittersPoints(List<CommitDiffBean> commits) {
        long points = 0;
        Collection<DateHashSetBean> contributorsPerFile = heatMapContributorService.getPathContributorsCsvFile(commits, false).values();
        for (DateHashSetBean dateHashSetBean : contributorsPerFile) {
            if (dateHashSetBean.getListOfContributors().size() <= commitService.getProperties().getFewCommittersSize()) {
                points++;
            }
        }
        return points;
    }

    public long getManyCommittersPoints(List<CommitDiffBean> commits) {
        long points = 0;
        Collection<DateHashSetBean> contributorsPerFile = heatMapContributorService.getPathContributorsCsvFile(commits, false).values();
        for (DateHashSetBean dateHashSetBean : contributorsPerFile) {
            if (dateHashSetBean.getListOfContributors().size() >= commitService.getProperties().getManyCommittersSize()) {
                points++;
            }
        }
        return points;
    }

    public long periodOfTimeAllFilesPoints(List<CommitDiffBean> commits, List<Period> periods) {
        return antiPatternsService.getFilteredFilesByPeriod(commits, periods).size();
    }

}

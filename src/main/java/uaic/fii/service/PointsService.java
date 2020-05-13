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

    private final HeatMapContributorService heatMapContributorService;

    private final AntiPatternsService antiPatternsService;

    private final PropertiesService propertiesService;

    @Autowired
    public PointsService(HeatMapContributorService heatMapContributorService, AntiPatternsService antiPatternsService, PropertiesService propertiesService) {
        this.heatMapContributorService = heatMapContributorService;
        this.antiPatternsService = antiPatternsService;
        this.propertiesService = propertiesService;
    }

    public long getFewCommittersPoints(List<CommitDiffBean> commits) {
        long points = 0;
        Collection<DateHashSetBean> contributorsPerFile = heatMapContributorService.getPathContributorsCsvFile(commits, false).values();
        for (DateHashSetBean dateHashSetBean : contributorsPerFile) {
            if (dateHashSetBean.getListOfContributors().size() <= propertiesService.getPropertiesMap().get("fewCommitters")) {
                points++;
            }
        }
        return points;
    }

    public long getManyCommittersPoints(List<CommitDiffBean> commits) {
        long points = 0;
        Collection<DateHashSetBean> contributorsPerFile = heatMapContributorService.getPathContributorsCsvFile(commits, false).values();
        for (DateHashSetBean dateHashSetBean : contributorsPerFile) {
            if (dateHashSetBean.getListOfContributors().size() >= propertiesService.getPropertiesMap().get("manyCommitters")) {
                points++;
            }
        }
        return points;
    }

    public long periodOfTimeAllFilesPoints(List<CommitDiffBean> commits, List<Period> periods) {
        return antiPatternsService.getFilteredFilesByPeriod(commits, periods).size();
    }

}

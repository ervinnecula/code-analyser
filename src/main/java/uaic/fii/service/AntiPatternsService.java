package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitChangeSize;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DateHashSetBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.ChangeSize;
import uaic.fii.model.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static uaic.fii.service.ChartDataStringWriters.writeHeatMapContributorsToCSVFormat;

@Service
public class AntiPatternsService {

    @Autowired
    private HeatMapContributorService heatMapContributorService;

    @Autowired
    private PropertiesService propertiesService;

    private Properties properties;

    public void loadProperties(String userName) {
        properties = propertiesService.getPropertiesByUserId(userName);
    }

    public String singlePointOfFailurePattern(List<CommitDiffBean> commitList) {
        Map<String, DateHashSetBean> allContributors = heatMapContributorService.getPathContributorsCsvFile(commitList);
        Map<String, DateHashSetBean> potentialSinglePointOfFailures = new TreeMap<>();

        for (Map.Entry<String, DateHashSetBean> entry : allContributors.entrySet()) {
            if (entry.getValue().getListOfContributors().size() <= properties.getFewCommitersSize()) {
                potentialSinglePointOfFailures.put(entry.getKey(), entry.getValue());
            }
        }
        return writeHeatMapContributorsToCSVFormat(potentialSinglePointOfFailures);
    }

    public String conglomeratePattern(List<CommitDiffBean> commitList) {
        Map<String, DateHashSetBean> allContributors = heatMapContributorService.getPathContributorsCsvFile(commitList);
        Map<String, DateHashSetBean> potentialConglomerations = new TreeMap<>();

        for (Map.Entry<String, DateHashSetBean> entry : allContributors.entrySet()) {
            if (entry.getValue().getListOfContributors().size() >= properties.getManyCommitersSize()) {
                potentialConglomerations.put(entry.getKey(), entry.getValue());
            }
        }
        return writeHeatMapContributorsToCSVFormat(potentialConglomerations);
    }

    public Map<String, List<CommitChangeSize>> detectMediumAndMajorChangesPattern(List<CommitDiffBean> commitList) {
        Map<String, List<CommitChangeSize>> mediumAndMajorChangesPattern = new HashMap<>();
        Set<String> filesChangedInCommit;

        for (CommitDiffBean commit : commitList) {
            filesChangedInCommit = new HashSet<>();
            int linesChangedInCommit = 0;
            for (DiffBean diff : commit.getDiffs()) {
                int linesChangedInDiff = 0;
                for (Edit edit : diff.getEdits()) {
                    linesChangedInDiff += edit.getLengthB() + edit.getLengthA();
                }
                filesChangedInCommit.add(diff.getFilePath());
                linesChangedInCommit += linesChangedInDiff;
            }
            ChangeSize changeSize = getChangeSizeFromLocChanged(linesChangedInCommit);
            for (String fileChanged : filesChangedInCommit) {
                CommitChangeSize commitChangeSize = new CommitChangeSize(commit.getCommitHash(), commit.getCommitDate(), commit.getCommiterName(), linesChangedInCommit, changeSize);
                List<CommitChangeSize> changesOnFile = mediumAndMajorChangesPattern.getOrDefault(fileChanged, new ArrayList<>());
                changesOnFile.add(commitChangeSize);
                mediumAndMajorChangesPattern.put(fileChanged, changesOnFile);
            }
        }
        return mediumAndMajorChangesPattern;
    }

    private ChangeSize getChangeSizeFromLocChanged(int locChanged) {
        ChangeSize changeSize = null;
        if (locChanged >= properties.getMajorChangeSize()) {
            changeSize = ChangeSize.MAJOR;
        }
        if (locChanged >= properties.getMediumChangeSize() && locChanged < properties.getMajorChangeSize()) {
            changeSize = ChangeSize.MEDIUM;
        }
        if (locChanged < properties.getMediumChangeSize()) {
            changeSize = ChangeSize.SMALL;
        }
        return changeSize;
    }
}

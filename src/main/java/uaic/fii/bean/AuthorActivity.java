package uaic.fii.bean;

import uaic.fii.model.DeveloperStatus;

public class AuthorActivity {
    private Integer numberOfCommits;
    private Integer totalChanges;
    private Integer netContribution;
    private DeveloperStatus developerStatus;

    public AuthorActivity(Integer numberOfCommits, Integer totalChanges, Integer netContribution, DeveloperStatus developerStatus) {
        this.numberOfCommits = numberOfCommits;
        this.totalChanges = totalChanges;
        this.netContribution = netContribution;
        this.developerStatus = developerStatus;
    }

    public Integer getNumberOfCommits() {
        return numberOfCommits;
    }

    public Integer getTotalChanges() {
        return totalChanges;
    }

    public Integer getNetContribution() {
        return netContribution;
    }

    public DeveloperStatus getDeveloperStatus() {
        return developerStatus;
    }

}

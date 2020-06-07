package uaic.fii.bean;

import uaic.fii.model.DeveloperStatus;

public class AuthorActivityBean implements Comparable<AuthorActivityBean>{
    private Integer numberOfCommits;
    private Integer totalChanges;
    private Integer netContribution;
    private DeveloperStatus developerStatus;

    public AuthorActivityBean(Integer numberOfCommits, Integer totalChanges, Integer netContribution, DeveloperStatus developerStatus) {
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

    @Override
    public int compareTo(AuthorActivityBean o) {
        return o.getNetContribution() - this.getNetContribution();
    }
}

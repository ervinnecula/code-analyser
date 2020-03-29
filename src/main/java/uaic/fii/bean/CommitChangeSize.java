package uaic.fii.bean;

import uaic.fii.model.ChangeSize;

import java.util.Date;

public class CommitChangeSize {
    private String commitHash;
    private Date commitDate;
    private String committerName;
    private int linesChanged;
    private ChangeSize changeSize;

    public CommitChangeSize(String commitHash, Date commitDate, String committerName, int linesChanged, ChangeSize changeSize) {
        this.commitHash = commitHash;
        this.commitDate = commitDate;
        this.committerName = committerName;
        this.linesChanged = linesChanged;
        this.changeSize = changeSize;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public int getLinesChanged() {
        return linesChanged;
    }

    public ChangeSize getChangeSize() {
        return changeSize;
    }

    public String getCommitterName() {
        return committerName;
    }
}

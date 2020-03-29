package uaic.fii.bean;

import java.util.Date;
import java.util.List;

public class CommitDiffBean {
    private String commitHash;
    private Date commitDate;
    private List<DiffBean> diffs;
    private String committerName;

    public CommitDiffBean(String commitHash, Date commitDate, List<DiffBean> diffs, String committerName) {
        this.commitHash = commitHash;
        this.commitDate = commitDate;
        this.diffs = diffs;
        this.committerName = committerName;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public List<DiffBean> getDiffs() {
        return diffs;
    }

    public String getCommitterName() {
        return committerName;
    }
}

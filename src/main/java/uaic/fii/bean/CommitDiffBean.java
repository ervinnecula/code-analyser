package uaic.fii.bean;

import java.util.Date;
import java.util.List;

public class CommitDiffBean {
    String commitHash;
    Date commitDate;
    List<DiffBean> diffs;

    public CommitDiffBean() {}

    public CommitDiffBean(String commitHash, Date commitDate, List<DiffBean> diffs) {
        this.commitHash = commitHash;
        this.commitDate = commitDate;
        this.diffs = diffs;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public List<DiffBean> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<DiffBean> diffs) {
        this.diffs = diffs;
    }
}

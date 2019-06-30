package uaic.fii.bean;

import java.util.Date;
import java.util.List;

public class CommitDiffBean {
    private String commitHash;
    private Date commitDate;
    private List<DiffBean> diffs;
    private String commiterName;

    public CommitDiffBean(String commitHash, Date commitDate, List<DiffBean> diffs, String commiterName) {
        this.commitHash = commitHash;
        this.commitDate = commitDate;
        this.diffs = diffs;
        this.commiterName = commiterName;
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

    public String getCommiterName() {
        return commiterName;
    }
}

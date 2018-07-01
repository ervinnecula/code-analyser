package uaic.fii.bean;

import org.eclipse.jgit.diff.EditList;

import java.util.List;

public class DiffBean {
    String changeType;
    String filePath;
    List<EditList> edits;

    public DiffBean() {}

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<EditList> getEdits() {
        return edits;
    }

    public void setEdits(List<EditList> edits) {
        this.edits = edits;
    }

}

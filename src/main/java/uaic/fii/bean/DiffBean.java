package uaic.fii.bean;

import org.eclipse.jgit.diff.EditList;

public class DiffBean {
    private String changeType;
    private String filePath;
    private EditList edits;

    public DiffBean() {}

    public DiffBean(String changeType, String filePath, EditList edits) {
        this.changeType = changeType;
        this.filePath = filePath;
        this.edits = edits;
    }

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

    public EditList getEdits() {
        return edits;
    }

    public void setEdits(EditList edits) {
        this.edits = edits;
    }

}

package uaic.fii.bean;

public class PathEditBean {
    private int linesRemoved;
    private int linesAdded;

    public PathEditBean(int linesRemoved, int linesAdded) {
        this.linesRemoved = linesRemoved;
        this.linesAdded = linesAdded;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public void setLinesRemoved(int linesRemoved) {
        this.linesRemoved = linesRemoved;
    }

    public int getLinesAdded() {
        return linesAdded;
    }

    public void setLinesAdded(int linesAdded) {
        this.linesAdded = linesAdded;
    }
}

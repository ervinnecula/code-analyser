package uaic.fii.model;


public class OwnerLinesAdded {
    private String owner;
    private Integer linesAdded;

    public OwnerLinesAdded(String owner, Integer linesAdded) {
        this.owner = owner;
        this.linesAdded = linesAdded;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getLinesAdded() {
        return linesAdded;
    }
}
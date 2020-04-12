package uaic.fii.bean;


public class OwnerLinesAddedBean {
    private String owner;
    private Integer linesAdded;

    public OwnerLinesAddedBean(String owner, Integer linesAdded) {
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
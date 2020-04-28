package uaic.fii.bean;


public class OwnerLinesAddedBean {
    private String owner;
    private String date;
    private Integer linesAdded;

    public OwnerLinesAddedBean(String owner, String date, Integer linesAdded) {
        this.owner = owner;
        this.date = date;
        this.linesAdded = linesAdded;
    }

    public String getOwner() {
        return owner;
    }

    public String getDate() {
        return date;
    }

    public Integer getLinesAdded() {
        return linesAdded;
    }
}
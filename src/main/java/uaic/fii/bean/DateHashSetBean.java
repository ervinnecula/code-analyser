package uaic.fii.bean;

import java.util.Set;

public class DateHashSetBean {
    private String date;
    private Set<String> listOfContributors;

    public DateHashSetBean(String date, Set<String> listOfContributors) {
        this.date = date;
        this.listOfContributors = listOfContributors;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Set<String> getListOfContributors() {
        return listOfContributors;
    }

    public void setListOfContributors(Set<String> listOfContributors) {
        this.listOfContributors = listOfContributors;
    }
}

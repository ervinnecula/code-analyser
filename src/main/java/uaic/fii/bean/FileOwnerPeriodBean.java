package uaic.fii.bean;

import uaic.fii.model.Period;

public class FileOwnerPeriodBean {
    private String filePath;
    private String owner;
    private Period period;

    public FileOwnerPeriodBean(String filePath, String owner, Period period) {
        this.filePath = filePath;
        this.owner = owner;
        this.period = period;
    }

    public String getFilePath() {
        return filePath;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getOwner() {
        return owner;
    }

}

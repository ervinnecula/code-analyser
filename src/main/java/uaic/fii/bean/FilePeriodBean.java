package uaic.fii.bean;

import uaic.fii.model.Period;

public class FilePeriodBean {
    private String filePath;
    private Period period;

    public FilePeriodBean(String filePath, Period period) {
        this.filePath = filePath;
        this.period = period;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}

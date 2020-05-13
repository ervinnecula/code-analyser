package uaic.fii.bean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PropertiesBean {

    @NotBlank
    private String repoName;

    @NotBlank
    private String repoGitUrl;

    @NotNull
    @Min(1)
    private Integer fewCommitters;

    @NotNull
    @Min(1)
    private Integer manyCommitters;

    @NotNull
    @Min(1)
    private Integer largeFileSize;

    @NotNull
    @Min(1)
    private Integer hugeFileSize;

    @NotNull
    @Min(1)
    private Integer mediumChangeSize;

    @NotNull
    @Min(1)
    private Integer majorChangeSize;

    @NotNull
    @Min(1)
    private Integer periodOfTime;

    public PropertiesBean(@NotBlank String repoName, @NotBlank String repoGitUrl, @NotNull @Min(1) Integer fewCommitters,
                          @NotNull @Min(1) Integer manyCommitters, @NotNull @Min(1) Integer largeFileSize,
                          @NotNull @Min(1) Integer hugeFileSize, @NotNull @Min(1) Integer mediumChangeSize,
                          @NotNull @Min(1) Integer majorChangeSize, @NotNull @Min(1) Integer periodOfTime) {
        this.repoName = repoName;
        this.repoGitUrl = repoGitUrl;
        this.fewCommitters = fewCommitters;
        this.manyCommitters = manyCommitters;
        this.largeFileSize = largeFileSize;
        this.hugeFileSize = hugeFileSize;
        this.mediumChangeSize = mediumChangeSize;
        this.majorChangeSize = majorChangeSize;
        this.periodOfTime = periodOfTime;
    }

    public PropertiesBean() {
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoGitUrl() {
        return repoGitUrl;
    }

    public Integer getFewCommitters() {
        return fewCommitters;
    }

    public Integer getManyCommitters() {
        return manyCommitters;
    }

    public Integer getLargeFileSize() {
        return largeFileSize;
    }

    public Integer getHugeFileSize() {
        return hugeFileSize;
    }

    public Integer getMediumChangeSize() {
        return mediumChangeSize;
    }

    public Integer getMajorChangeSize() {
        return majorChangeSize;
    }

    public Integer getPeriodOfTime() {
        return periodOfTime;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setRepoGitUrl(String repoGitUrl) {
        this.repoGitUrl = repoGitUrl;
    }

    public void setFewCommitters(Integer fewCommitters) {
        this.fewCommitters = fewCommitters;
    }

    public void setManyCommitters(Integer manyCommitters) {
        this.manyCommitters = manyCommitters;
    }

    public void setLargeFileSize(Integer largeFileSize) {
        this.largeFileSize = largeFileSize;
    }

    public void setHugeFileSize(Integer hugeFileSize) {
        this.hugeFileSize = hugeFileSize;
    }

    public void setMediumChangeSize(Integer mediumChangeSize) {
        this.mediumChangeSize = mediumChangeSize;
    }

    public void setMajorChangeSize(Integer majorChangeSize) {
        this.majorChangeSize = majorChangeSize;
    }

    public void setPeriodOfTime(Integer periodOfTime) {
        this.periodOfTime = periodOfTime;
    }
}

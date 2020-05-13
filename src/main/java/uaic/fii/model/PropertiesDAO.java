package uaic.fii.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "properties")
public class PropertiesDAO {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "large_file_size")
    private Integer largeFileSize;

    @Column(name = "huge_file_size")
    private Integer hugeFileSize;

    @Column(name = "many_committers_size")
    private Integer manyCommittersSize;

    @Column(name = "few_committers_size")
    private Integer fewCommittersSize;

    @Column(name = "small_change_size")
    private Integer smallChangeSize;

    @Column(name = "medium_change_size")
    private Integer mediumChangeSize;

    @Column(name = "major_change_size")
    private Integer majorChangeSize;

    @Column(name = "period_of_time")
    private Integer periodOfTime;

    public PropertiesDAO() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getLargeFileSize() {
        return largeFileSize;
    }

    public void setLargeFileSize(Integer largeFileSize) {
        this.largeFileSize = largeFileSize;
    }

    public Integer getHugeFileSize() {
        return hugeFileSize;
    }

    public void setHugeFileSize(Integer hugeFileSize) {
        this.hugeFileSize = hugeFileSize;
    }

    public Integer getManyCommittersSize() {
        return manyCommittersSize;
    }

    public void setManyCommittersSize(Integer manyCommittersSize) {
        this.manyCommittersSize = manyCommittersSize;
    }

    public Integer getFewCommittersSize() {
        return fewCommittersSize;
    }

    public void setFewCommittersSize(Integer fewCommittersSize) {
        this.fewCommittersSize = fewCommittersSize;
    }

    public Integer getMediumChangeSize() {
        return mediumChangeSize;
    }

    public void setMediumChangeSize(Integer mediumChangeSize) {
        this.mediumChangeSize = mediumChangeSize;
    }

    public Integer getMajorChangeSize() {
        return majorChangeSize;
    }

    public void setMajorChangeSize(Integer majorChangeSize) {
        this.majorChangeSize = majorChangeSize;
    }

    public Integer getPeriodOfTime() {
        return periodOfTime;
    }

    public void setPeriodOfTime(Integer periodOfTime) {
        this.periodOfTime = periodOfTime;
    }
}

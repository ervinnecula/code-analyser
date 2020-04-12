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

    @Column(name = "many_commiters_size")
    private Integer manyCommitersSize;

    @Column(name = "few_commiters_size")
    private Integer fewCommitersSize;

    @Column(name = "small_change_size")
    private Integer smallChangeSize;

    @Column(name = "medium_change_size")
    private Integer mediumChangeSize;

    @Column(name = "major_change_size")
    private Integer majorChangeSize;

    @Column(name = "period_of_time")
    private Integer periodOfTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLargeFileSize() {
        return largeFileSize;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Integer getManyCommitersSize() {
        return manyCommitersSize;
    }

    public void setManyCommitersSize(Integer manyCommitersSize) {
        this.manyCommitersSize = manyCommitersSize;
    }

    public Integer getFewCommitersSize() {
        return fewCommitersSize;
    }

    public void setFewCommitersSize(Integer fewCommitersSize) {
        this.fewCommitersSize = fewCommitersSize;
    }

    public Integer getSmallChangeSize() {
        return smallChangeSize;
    }

    public void setSmallChangeSize(Integer smallChangeSize) {
        this.smallChangeSize = smallChangeSize;
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

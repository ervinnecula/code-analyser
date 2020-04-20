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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getManyCommittersSize() {
        return manyCommittersSize;
    }

    public Integer getFewCommittersSize() {
        return fewCommittersSize;
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
}

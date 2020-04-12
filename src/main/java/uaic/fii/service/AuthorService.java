package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.AuthorActivityBean;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.DeveloperStatus;
import uaic.fii.bean.OwnerLinesAddedBean;
import uaic.fii.model.Period;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorService {

    private final static Logger logger = LoggerFactory.getLogger(AuthorService.class);

    private final CommitService commitService;

    @Autowired
    public AuthorService(CommitService commitService) {
        this.commitService = commitService;
    }

    public Map<String, AuthorActivityBean> getAuthorActivityList(List<CommitDiffBean> commits) {
        logger.info("AuthorService - getAuthorActivityList() - getting author activity");
        Map<String, AuthorActivityBean> result = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            AuthorActivityBean authorActivity = result.getOrDefault(commit.getCommitterName(), new AuthorActivityBean(0, 0, 0, DeveloperStatus.ACTIVE));
            Period period = commitService.getPeriodOfTimeCommit(commit);
            DeveloperStatus developerStatus = (period == Period.RECENT) ? DeveloperStatus.ACTIVE : DeveloperStatus.INACTIVE;

            AuthorActivityBean updatedAuthorActivity =
                    new AuthorActivityBean(authorActivity.getNumberOfCommits() + 1,
                            authorActivity.getTotalChanges() + commitService.getLoCChangedInCommit(commit),
                            authorActivity.getNetContribution() + commitService.getAddedLinesInCommit(commit) - commitService.getRemovedLinesInCommit(commit),
                            developerStatus
                    );
            result.put(commit.getCommitterName(), updatedAuthorActivity);
        }

        logger.info("AuthorService - getAuthorActivityList() - collected author activity");
        return result;
    }

    public Map<String, Period> getAuthorsAndPeriods(List<CommitDiffBean> commits) {
        logger.info("AuthorService - getAuthorsAndPeriods() - collecting authors and periods inside project");

        Map<String, Period> authorsAndPeriods = new HashMap<>();
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            authorsAndPeriods.put(commit.getCommitterName(), period);
        }

        logger.info("AuthorService - getAuthorsAndPeriods() - collecting authors and periods inside project");
        return authorsAndPeriods;
    }

    public Map<String, OwnerLinesAddedBean> getFileOwners(List<CommitDiffBean> commits) {
        logger.info("AuthorService - getFileOwners() - collecting file owners");

        Map<String, OwnerLinesAddedBean> fileOwners = new HashMap<>();
        OwnerLinesAddedBean currentFileOwner;
        String filePath;
        String potentialOwner;

        for (CommitDiffBean commit : commits) {
            int linesAddedInFile = 0;
            for (DiffBean diff : commit.getDiffs()) {
                if (!diff.getFilePath().equals("/dev/null")) {
                    for (Edit edit : diff.getEdits()) {
                        linesAddedInFile += edit.getLengthB();
                        linesAddedInFile -= edit.getLengthA();
                    }
                }
                filePath = diff.getFilePath();
                fileOwners.putIfAbsent(filePath, new OwnerLinesAddedBean(commit.getCommitterName(), linesAddedInFile));
                currentFileOwner = fileOwners.get(filePath);
                potentialOwner = commit.getCommitterName();

                if (!potentialOwner.equals(currentFileOwner.getOwner()) && linesAddedInFile > currentFileOwner.getLinesAdded()) {
                    fileOwners.put(filePath, new OwnerLinesAddedBean(potentialOwner, linesAddedInFile));
                }
            }
        }
        logger.info("AuthorService - getFileOwners() - found source file owners");
        return fileOwners;
    }

}

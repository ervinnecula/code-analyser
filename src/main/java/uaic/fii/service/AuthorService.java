package uaic.fii.service;

import org.eclipse.jgit.diff.Edit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.AuthorActivity;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.model.DeveloperStatus;
import uaic.fii.model.OwnerLinesAdded;
import uaic.fii.model.Period;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AuthorService {

    @Autowired
    private CommitService commitService;

    public Map<String, AuthorActivity> getAuthorActivityList(List<CommitDiffBean> commits) {
        Map<String, AuthorActivity> result = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            AuthorActivity authorActivity = result.getOrDefault(commit.getCommitterName(), new AuthorActivity(0, 0, 0, DeveloperStatus.ACTIVE));
            Period period = commitService.getPeriodOfTimeCommit(commit);
            DeveloperStatus developerStatus = (period == Period.RECENT) ? DeveloperStatus.ACTIVE : DeveloperStatus.INACTIVE;

            AuthorActivity updatedAuthorActivity =
                    new AuthorActivity(authorActivity.getNumberOfCommits() + 1,
                            authorActivity.getTotalChanges() + commitService.getLoCChangedInCommit(commit),
                            authorActivity.getNetContribution() + commitService.getAddedLinesInCommit(commit) - commitService.getRemovedLinesInCommit(commit),
                            developerStatus
                    );
            result.put(commit.getCommitterName(), updatedAuthorActivity);
        }

        return result;
    }

    public Map<String, Period> getAuthorsAndPeriods(List<CommitDiffBean> commits) {
        Map<String, Period> authorsAndPeriods = new HashMap<>();
        for (CommitDiffBean commit : commits) {
            Period period = commitService.getPeriodOfTimeCommit(commit);
            authorsAndPeriods.put(commit.getCommitterName(), period);
        }
        return authorsAndPeriods;
    }

    public Map<String, OwnerLinesAdded> getFileOwners(List<CommitDiffBean> commits) {
        Map<String, OwnerLinesAdded> fileOwners = new HashMap<>();
        OwnerLinesAdded currentFileOwner;
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
                fileOwners.putIfAbsent(filePath, new OwnerLinesAdded(commit.getCommitterName(), linesAddedInFile));
                currentFileOwner = fileOwners.get(filePath);
                potentialOwner = commit.getCommitterName();

                if (!potentialOwner.equals(currentFileOwner.getOwner()) && linesAddedInFile > currentFileOwner.getLinesAdded()) {
                    fileOwners.put(filePath, new OwnerLinesAdded(potentialOwner, linesAddedInFile));
                }
            }
        }
        return fileOwners;
    }

}

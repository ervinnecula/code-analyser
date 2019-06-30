package uaic.fii.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uaic.fii.bean.AuthorActivity;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.model.DeveloperStatus;
import uaic.fii.model.Period;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AuthorService {

    @Autowired
    private CommitService commitService;

    public Map<String, AuthorActivity> getAuthorActivityList (List<CommitDiffBean> commits) {
        Map<String, AuthorActivity> result = new HashMap<>();

        for (CommitDiffBean commit : commits) {
            AuthorActivity authorActivity = result.getOrDefault(commit.getCommiterName(), new AuthorActivity(0, 0, 0, DeveloperStatus.ACTIVE));
            Period period = commitService.getPeriodOfTimeCommit(commit);
            DeveloperStatus developerStatus = (period == Period.RECENT) ? DeveloperStatus.ACTIVE : DeveloperStatus.INACTIVE;

            AuthorActivity updatedAuthorActivity =
                    new AuthorActivity(authorActivity.getNumberOfCommits() + 1,
                            authorActivity.getTotalChanges() + commitService.getLoCChangedInCommit(commit),
                            authorActivity.getNetContribution() + commitService.getAddedLinesInCommit(commit) - commitService.getRemovedLinesInCommit(commit),
                            developerStatus
                    );
            result.put(commit.getCommiterName(),  updatedAuthorActivity);
        }

        return result;
    }

}

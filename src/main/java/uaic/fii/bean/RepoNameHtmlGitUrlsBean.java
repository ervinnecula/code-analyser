package uaic.fii.bean;

public class RepoNameHtmlGitUrlsBean {
    private String repoName;
    private String repoHtmlUrl;
    private String repoGitUrl;
    private String repoLanguage;

    public RepoNameHtmlGitUrlsBean(String repoName, String repoHtmlUrl, String repoGitUrl, String repoLanguage, boolean wasClonned) {
        this.repoName = repoName;
        this.repoHtmlUrl = repoHtmlUrl;
        this.repoGitUrl = repoGitUrl;
        this.repoLanguage = repoLanguage;
    }

    public RepoNameHtmlGitUrlsBean() {}

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoHtmlUrl() {
        return repoHtmlUrl;
    }

    public void setRepoHtmlUrl(String repoHtmlUrl) {
        this.repoHtmlUrl = repoHtmlUrl;
    }

    public String getRepoGitUrl() {
        return repoGitUrl;
    }

    public void setRepoGitUrl(String repoGitUrl) {
        this.repoGitUrl = repoGitUrl;
    }

    public String getRepoLanguage() {
        return repoLanguage;
    }

    public void setRepoLanguage(String repoLanguage) {
        this.repoLanguage = repoLanguage;
    }

}

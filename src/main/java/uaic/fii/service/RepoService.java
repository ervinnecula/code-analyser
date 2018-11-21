package uaic.fii.service;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.LanguageRegistry;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uaic.fii.bean.CommitDiffBean;
import uaic.fii.bean.DiffBean;
import uaic.fii.bean.RepoNameHtmlGitUrlsBean;
import uaic.fii.bean.RuleViolationBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Service
public class RepoService {

    final static Logger logger = LoggerFactory.getLogger(RepoService.class);

    public String getPathToCloneDir() {
        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = classLoader.getResource("application.properties").getPath();
        int lastIndexOfSlash = classLoader.getResource("application.properties").getPath().lastIndexOf('/');
        return filePath.substring(0, lastIndexOfSlash);
    }

    public int shortAnalyzeClonedProject(String path) throws PMDException, IOException {
        return analyzeClonedProject(path).size();
    }

    public List<RuleViolationBean> analyzeClonedProject(String path) throws PMDException, IOException {
        String fileContent;
        RuleContext context;
        Rule rule;
        File javaFile;
        List<RuleViolationBean> ruleViolationBeans = new ArrayList<>();
        Iterator<File> files = getJavaFilesInDirectory(path);

        while (files.hasNext()) {
            javaFile = files.next();
            context = getPMDcontext(javaFile.getName());
            fileContent = new String(readAllBytes(javaFile.toPath()));
            new SourceCodeProcessor(getPMDConfig()).processSourceCode(
                    new StringReader(fileContent), getPMDRuleSets(), context);

            for (RuleViolation ruleViolation : context.getReport()) {
                rule = ruleViolation.getRule();
                ruleViolationBeans.add(new RuleViolationBean(rule.getMessage(), rule.getDescription(), rule.getExternalInfoUrl(),
                        rule.getPriority().toString(), javaFile.getName(), ruleViolation.getMethodName(),
                        ruleViolation.getClassName(), ruleViolation.getBeginLine(), ruleViolation.getEndLine()));
            }
        }

        return ruleViolationBeans;
    }

    public void cloneRepo(RepoNameHtmlGitUrlsBean repoBean, File resourceFolder) throws IOException, GitAPIException {
        Git git;
        GitCommand<?> command;
        if (resourceFolder.exists()) {
            git = Git.open(resourceFolder);
            command = git.pull();
            logger.debug(format("MainController - cloneRepo() - Local directory already exists. Pulling latest changes to %s", resourceFolder));
        } else {
            command = Git.cloneRepository().setURI(repoBean.getRepoGitUrl()).setDirectory(resourceFolder);

            logger.debug(format("MainController - cloneRepo() - Cloning repo: %s", repoBean.getRepoName()));

        }
        command.call();
    }

    public List<CommitDiffBean> getCommitsAndDiffs(File resourceFolder) throws IOException {
        Git git = Git.open(resourceFolder);
        List<CommitDiffBean> commits = new ArrayList<>();

        try (Repository repository = git.getRepository()) {
            Collection<Ref> allRefs = repository.getAllRefs().values();

            try (RevWalk revWalk = new RevWalk(repository)) {
                for (Ref ref : allRefs) {
                    revWalk.markStart(revWalk.parseCommit(ref.getObjectId()));
                }
                for (RevCommit commit : revWalk) {
                    AbstractTreeIterator tree, parentTree;
                    ObjectReader reader = repository.newObjectReader();

                    if (commit.getParentCount() != 0) {
                        parentTree = new CanonicalTreeParser(null, reader, commit.getParent(0).getTree());
                    } else {
                        parentTree = new EmptyTreeIterator();
                    }
                    tree = new CanonicalTreeParser(null, reader, commit.getTree());

                    CommitDiffBean commitDiffBean = new CommitDiffBean(commit.getName(),
                            commit.getCommitterIdent().getWhen(),
                            getDiffsBetweenCommits(tree, parentTree, repository),
                            commit.getAuthorIdent().getName());

                    commits.add(commitDiffBean);
                }
            }
        }
        return commits;
    }

    private List<DiffBean> getDiffsBetweenCommits(AbstractTreeIterator tree, AbstractTreeIterator parentTree, Repository repository) throws IOException {
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        List<DiffBean> diffBeans = new ArrayList<>();
        DiffBean diffBean;

        df.setRepository(repository);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

        List<DiffEntry> diffs = df.scan(parentTree, tree);

        for (DiffEntry diff : diffs) {
            FileHeader fh = df.toFileHeader(diff);
            diffBean = new DiffBean(diff.getChangeType().name().toLowerCase(), diff.getNewPath(), fh.toEditList());
            diffBeans.add(diffBean);
        }
        return diffBeans;
    }

    private Iterator<File> getJavaFilesInDirectory(String pathToDir) {
        final File directory = new File(pathToDir);
        final Iterator<File> files = FileUtils.iterateFiles(directory, new String[]{"java"}, true);

        return files;
    }

    private RuleContext getPMDcontext(String fileName) {
        final RuleContext context = new RuleContext();
        context.setSourceCodeFilename(fileName);
        context.setReport(new Report());

        return context;
    }

    private PMDConfiguration getPMDConfig() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setDefaultLanguageVersion(LanguageRegistry.findLanguageVersionByTerseName("java 1.8"));

        return configuration;
    }

    private RuleSets getPMDRuleSets() {
        RuleSet basicRuleSet = null;
        try {
            basicRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml");
        } catch (RuleSetNotFoundException e) {
            logger.error(String.format("RepoService - getPMDRuleSets(). Could not find ruleset file : %s", e));
        }

        return new RuleSets(basicRuleSet);
    }
}

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
import uaic.fii.model.StaticDetectionKind;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static uaic.fii.model.StaticDetectionKind.BASIC;
import static uaic.fii.model.StaticDetectionKind.CODESIZE;
import static uaic.fii.model.StaticDetectionKind.COUPLING;
import static uaic.fii.model.StaticDetectionKind.DESIGN;
import static uaic.fii.model.StaticDetectionKind.OPTIMIZATION;

@Service
public class RepoService {

    private final static Logger logger = LoggerFactory.getLogger(RepoService.class);

    private List<RuleViolationBean> ruleViolationList;

    private Map<String, List<RuleViolationBean>> ruleViolations;

    public String getPathToCloneDir() {
        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = classLoader.getResource("application.properties").getPath();
        int lastIndexOfSlash = classLoader.getResource("application.properties").getPath().lastIndexOf('/');
        return filePath.substring(0, lastIndexOfSlash);
    }

    public void analyzeClonedProject(String path) throws PMDException, IOException {
        String fileContent;
        ruleViolations = new HashMap<>();
        RuleContext context;
        Rule rule;
        File javaFile;
        Iterator<File> files = getJavaFilesInDirectory(path);

        while (files.hasNext()) {
            javaFile = files.next();
            context = getPMDcontext(javaFile.getName());
            fileContent = new String(readAllBytes(javaFile.toPath()));
            new SourceCodeProcessor(getPMDConfig()).processSourceCode(
                    new StringReader(fileContent), getPMDRuleSets(), context);

            for (RuleViolation ruleViolation : context.getReport()) {
                rule = ruleViolation.getRule();
                ruleViolationList = fitToSpecificRuleViolationsKind(rule, javaFile, ruleViolations, ruleViolation, BASIC);
                ruleViolations.put(rule.getRuleSetName(), ruleViolationList);

            }
        }
    }

    public void cloneOrPullRepo(RepoNameHtmlGitUrlsBean repoBean, File resourceFolder) throws IOException, GitAPIException {
        Git git;
        GitCommand<?> command;
        if (resourceFolder.exists()) {
            git = Git.open(resourceFolder);
            command = git.pull();
            logger.info(format("RepoService - cloneOrPullRepo() - Local directory already exists. Pulling latest changes to %s", resourceFolder));
        } else {
            command = Git.cloneRepository().setURI(repoBean.getRepoGitUrl()).setDirectory(resourceFolder);
            logger.info(format("RepoService - cloneOrPullRepo() - Cloning repo: %s", repoBean.getRepoName()));

        }
        command.call();
    }

    public List<CommitDiffBean> getCommitsAndDiffs(File resourceFolder) throws IOException {
        logger.info("RepoService - getCommitsAndDiffs() - getting diffs for each commit");
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
        logger.info("RepoService - getCommitsAndDiffs() - loaded diffs for existing commits");
        return commits;
    }

    private List<RuleViolationBean> fitToSpecificRuleViolationsKind(Rule rule, File javaFile, Map<String, List<RuleViolationBean>> ruleViolationBeans, RuleViolation ruleViolation, StaticDetectionKind staticDetectionKind) {
        List<RuleViolationBean> ruleViolationList = ruleViolationBeans.getOrDefault(staticDetectionKind.getDetectedKind(), new ArrayList<>());
        ruleViolationList.add(new RuleViolationBean(rule.getMessage(), rule.getDescription(), rule.getExternalInfoUrl(),
                rule.getPriority().toString(), javaFile.getName(), ruleViolation.getMethodName(),
                ruleViolation.getClassName(), ruleViolation.getBeginLine(), ruleViolation.getEndLine()));
        return ruleViolationList;
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

        return FileUtils.iterateFiles(directory, new String[]{"java"}, true);
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
        RuleSets ruleSets = new RuleSets();
        RuleSet basicRuleSet = null;
        RuleSet couplingRuleSet = null;
        RuleSet optimizationsRuleSet = null;
        RuleSet codesizeRuleSet = null;
        RuleSet designRuleSet = null;

        try {
            basicRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/basic.xml");
            couplingRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/coupling.xml");
            optimizationsRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/optimizations.xml");
            codesizeRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/codesize.xml");
            designRuleSet = new RuleSetFactory().createRuleSet("rulesets/java/codesize.xml");
        } catch (RuleSetNotFoundException e) {
            logger.error(String.format("RepoService - getPMDRuleSets(). Could not find ruleset file : %s", e));
        }
        ruleSets.addRuleSet(basicRuleSet);
        ruleSets.addRuleSet(couplingRuleSet);
        ruleSets.addRuleSet(optimizationsRuleSet);
        ruleSets.addRuleSet(codesizeRuleSet);
        ruleSets.addRuleSet(designRuleSet);

        return ruleSets;
    }

    public Map<String, List<RuleViolationBean>> getRuleViolations() {
        return ruleViolations;
    }
}

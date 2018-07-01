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
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
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
                        rule.getPriority().toString(), files.next().getName(), ruleViolation.getMethodName(),
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

    public void getLinesAddedPerCommit(File resourceFolder) throws IOException {
        Git git = Git.open(resourceFolder);

        try (Repository repository = git.getRepository()) {
            Collection<Ref> allRefs = repository.getAllRefs().values();

            try (RevWalk revWalk = new RevWalk( repository )) {
                for( Ref ref : allRefs ) {
                    revWalk.markStart( revWalk.parseCommit( ref.getObjectId() ));
                }
                for( RevCommit commit : revWalk ) {
                    AbstractTreeIterator tree, parentTree;
                    ObjectReader reader = repository.newObjectReader();

                    if(commit.getParentCount() != 0) {
                        parentTree = new CanonicalTreeParser(null, reader, commit.getParent(0).getTree());
                    } else {
                        parentTree = new EmptyTreeIterator();
                    }
                    tree = new CanonicalTreeParser( null, reader, commit.getTree() );

                    CommitDiffBean commitDiffBean = new CommitDiffBean();
                    commitDiffBean.setCommitHash(commit.toString());
                    commitDiffBean.setCommitDate(commit.getCommitterIdent().getWhen());

                    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                    df.setRepository(repository);
                    df.setDiffComparator(RawTextComparator.DEFAULT);
                    df.setDetectRenames(true);

                    List<DiffEntry> diffs = df.scan(parentTree, tree);
                    DiffBean diffBean = new DiffBean();
                    for (DiffEntry diff : diffs) {
                        diffBean.setChangeType(diff.getChangeType().name().toLowerCase());
                        diffBean.setFilePath(diff.getNewPath());
                        FileHeader fh = df.toFileHeader(diff);
                        List<? extends HunkHeader> hunks = fh.getHunks();
                        List<EditList> edits = new ArrayList<>();
                        EditList editList;
                        for(HunkHeader hunk: hunks) {
                            editList = hunk.toEditList();
                            edits.add(editList);
                        }
                        diffBean.setEdits(edits);
                    }

                }
            }
        }
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
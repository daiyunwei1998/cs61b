package gitlet;

//todo delete import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.writeContents;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_DIR = join(GITLET_DIR, "stagingArea");
    public static final File ADD_DIR = Utils.join(STAGING_DIR, "toAdd");
    public static final File REMOVE_DIR = Utils.join(STAGING_DIR, "toRemove");
    public static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    public static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    public static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    public static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    public static final File ADD_INDEX = Utils.join(ADD_DIR, "INDEX");
    public static final File REMOVE_INDEX = Utils.join(REMOVE_DIR, "INDEX");

    private static class Index implements Serializable {
        private HashMap<String, String> entries;


        private Index() {
            this.entries = new HashMap<String, String>();
        }

        private static Index fromFile(File fileName) {
            return Utils.readObject(fileName,Index.class);
        }

        private void toFile(File fileName) {
            writeObject(fileName, this);

        }
        private void addEntry(String fileName, String blobName) {
            this.entries.put(fileName, blobName);
        }
        private void removeEntry(String fileName) {
            this.entries.remove(fileName);
        }
        private String get(String fileName) {
            return this.entries.get(fileName);
        }

        private HashMap<String, String> getEntries(){
            return this.entries;
        }
        private int size() {
            return this.entries.size();
        }
        private boolean isEmpty() {
            return this.entries.isEmpty();
        }

    }

    /* TODO: fill in the rest of this class. */
    public static void init() {
        // Check if .gitlet exist, if not, mkdir()
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();

        // Create sub-directories
        STAGING_DIR.mkdir();
        ADD_DIR.mkdir();
        REMOVE_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        BLOBS_DIR.mkdir();

        // Create HEAD & master branch file
        try {
            File master = Utils.join(BRANCHES_DIR, "master");
            master.createNewFile();
            HEAD.createNewFile();
            Utils.writeContents(master, "");
            updateHEADBranch("master");

        } catch (IOException e) {
            // Handle potential IOException (e.g., permission issues)
            e.printStackTrace();
        }

        // Create index for staging area (add and remove)
        Index AddIndex = new Index();
        Index RemoveIndex = new Index();
        AddIndex.toFile(ADD_INDEX);
        RemoveIndex.toFile(REMOVE_INDEX);

        // initial commit
        // todo delete Commit initial = new Commit("initial commit", null);
        Repository.commit("initial commit");

        //TODO It will have a single branch: master, which initially points to this initial commit,
        // and master will be the current branch.
    }

    public static void add(String fileName) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        File f = new File(fileName);

        //check if input file exist
        if (!f.exists()) {
            System.out.println("File to add does not exist.");
            return;
        }

        // get INDEX file
        Index addIndex = Index.fromFile(ADD_INDEX);

        // make a new blob
        blob b = new blob(f);

        // get hash (filename of the blob)
        File saveToFile = Utils.join(ADD_DIR, b.getSHA1());


        /** check current version */
        // todo test this
        Commit headCommit = getHEADCommit();
        if (Objects.equals(headCommit.getFileVersion(fileName), b.getSHA1())) {
          /*   if file is the same as in current commit
             remove it from toAdd (if any), do nothing*/
            if (addIndex.getEntries().containsKey(fileName)) {
                addIndex.removeEntry(fileName);
            }
            if (!addIndex.getEntries().containsValue(b.getSHA1()) &&
            saveToFile.exists()) {
                // check if other file has the same content
                saveToFile.delete();
                // remove blob from staging area
            }
            return;
        } else {
            // first time addedd or different version
            b.toFile(saveToFile);
            addIndex.addEntry(f.getName(), b.getSHA1());
            addIndex.toFile(ADD_INDEX);
        }
    }

    public static void updateHEADCommit(String commitHash) {
        String headBranch = Utils.readContentsAsString(HEAD);
        File branchFile = Utils.join(BRANCHES_DIR, headBranch);
        Utils.writeContents(branchFile, commitHash);
    }
    public static void updateHEADBranch(String branch) {
        Utils.writeContents(HEAD, branch);
    }

    public static String getHEADBranch() {
        return Utils.readContentsAsString(HEAD);
    }
    public static void setHeadBranch(String branchName) {
        writeContents(HEAD, branchName);
    }

    public static void setBranchHead(String branchName, String commitID) {
        writeContents(Utils.join(BRANCHES_DIR, branchName), commitID);
    }
    public static String getHEADCommitID() {
        String headBranch = Utils.readContentsAsString(HEAD);
        File branchFile = Utils.join(BRANCHES_DIR, headBranch);
        String commitID = Utils.readContentsAsString(branchFile);
        return commitID;
    }
    public static Commit getHEADCommit() {
        String headBranch = Utils.readContentsAsString(HEAD);
        File branchFile = Utils.join(BRANCHES_DIR, headBranch);
        String commitID = Utils.readContentsAsString(branchFile);
        if (commitID.isEmpty()) {
            return null;
        }
        File headCommit = Utils.join(Repository.COMMITS_DIR,commitID);
        return readObject(headCommit,Commit.class);
    }

    public static HashSet<String> listBranch() {
        HashSet<String> branches = new HashSet<>();
        return branches;
    }

    public static void commit(String message) {
        Commit HEADCommit = getHEADCommit();

        // make a new commit object
        Commit newCommit = new Commit(message, HEADCommit);
        if (HEADCommit != null) {
            // read indexes
            Index addIndex = Index.fromFile(ADD_INDEX);
            Index removeIndex = Index.fromFile(REMOVE_INDEX);

            // check if nothing changes
            if (addIndex.size() ==0 & removeIndex.size() == 0) {
                System.out.println("No changes added to the commit.");
                return;
            }

            // add files
            Set<String> filesToAdd = new HashSet<>();

            Iterator<String> it = addIndex.getEntries().keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                filesToAdd.add(addIndex.get(key));
                newCommit.addFile(key,addIndex.get(key));
                it.remove();
            }
            addIndex.toFile(ADD_INDEX);

            for (String blobName:filesToAdd) {
                File oldFile = Utils.join(ADD_DIR, blobName);
                File newFile = Utils.join(BLOBS_DIR,blobName);
                boolean status = oldFile.renameTo(newFile);
                if (!status) {
                    System.out.println("Commiting staged files unsuccessfully");
                }
            }

            // remove files
            for (String fileName:removeIndex.getEntries().keySet()) {
                // untrack from commit
                newCommit.removeFIle(fileName);
                // remove from removeIndex
                removeIndex.removeEntry(fileName);
            }
            removeIndex.toFile(REMOVE_INDEX);

        }

        // save new commit
        newCommit.toFile();
        // update HEAD
        updateHEADCommit(newCommit.getSHA1()); //todo behavior is changed
    }

    public static void rm(String fileName) {
        // todo remove
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        // get INDEX file
        Index addIndex = Index.fromFile(ADD_INDEX);
        Index removeIndex = Index.fromFile(REMOVE_INDEX);

        // unstage file if needed
        if (addIndex.getEntries().containsKey(fileName)) {
            // get file hash
            File f = Utils.join(CWD, fileName);

            // make a new blob
            File fStaged = Utils.join(ADD_DIR, sha1(readContentsAsString(f)));
            blob b = new blob(fStaged);

            addIndex.removeEntry(fileName);
            addIndex.toFile(ADD_INDEX);
            if (!addIndex.getEntries().containsValue(b.getFileName()) &&
                    fStaged.exists()) {
                fStaged.delete();
                // remove blob from staging area
            }
            return;
        }

        // if the file is tracked
        Commit headCommit = getHEADCommit();
        if (headCommit.containsFile(fileName)) {
            removeIndex.addEntry(fileName, sha1(fileName));
            File f = Utils.join(CWD, fileName);
            f.delete();
            removeIndex.toFile(REMOVE_INDEX);
            return;
        }

        // if neither
        System.out.println("No reason to remove the file.");

    }

    public static void log() {
        Commit c = Repository.getHEADCommit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

        while (!"".equals(c.getParentID())) {
            String formattedDate = dateFormat.format(c.getTimestamp());
            System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                    c.getSHA1(),
                    formattedDate,
                    c.getMessage());

            File commitFile = Utils.join(Repository.COMMITS_DIR, c.getParentID());

            try {
                c = Commit.fromFile(commitFile);
            } catch (Exception e) {
                // Handle exceptions during commit reading
                e.printStackTrace();
                break;
            }
        }

        // Print information for the initial commit
        String formattedDate = dateFormat.format(c.getTimestamp());
        System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                c.getSHA1(),
                formattedDate,
                c.getMessage());
    }

    public static void globalLog() {
        File[] commitFiles = COMMITS_DIR.listFiles();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

        /* Though it will never be null since there are always an initial commit*/
        assert commitFiles != null;
        for (File commitFile:commitFiles) {
            Commit c = Commit.fromFile(commitFile);
            String formattedDate = dateFormat.format(c.getTimestamp());
            System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                    c.getSHA1(),
                    formattedDate,
                    c.getMessage());
        }
    }

    public static void find(String message) {
        File[] commitFiles = COMMITS_DIR.listFiles();
        assert commitFiles != null;
        boolean found = false;
        for (File commitFile:commitFiles) {
            Commit c = Commit.fromFile(commitFile);
            if (Objects.equals(c.getMessage(), message)) {
                System.out.println(c.getSHA1());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {

        System.out.println("=== Branches ===");
        String headBranch = getHEADBranch();
        for (File branch: Objects.requireNonNull(BRANCHES_DIR.listFiles())) {
            if (branch.getName().equals(headBranch)) {
                System.out.println("*"+branch.getName());
            } else {
                System.out.println(branch.getName());
            }
        }
        System.out.println();

        /*list the staged files*/
        System.out.println("=== Staged Files ===");
          // read the index
        Index AddIndex = Index.fromFile(ADD_INDEX);
        TreeSet<String> sortedFileNames = new TreeSet<>(AddIndex.getEntries().keySet());
        for (String fileName : sortedFileNames) {
            System.out.println(fileName);
        }
        System.out.println();

        /*list the removed files*/
        System.out.println("=== Removed Files ===");
        Index RemoveIndex = Index.fromFile(REMOVE_INDEX);
        TreeSet<String> sortedFileNamesRemove = new TreeSet<>(RemoveIndex.getEntries().keySet());
        for (String fileName : sortedFileNamesRemove) {
            System.out.println(fileName);
        }
        System.out.println();
        // TODO list the modified files
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        // TODO list the untracked files
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutFile(String fileName) {
    /*    Takes the version of the file as it exists in the head commit and puts
        it in the working directory, overwriting the version of the file that’s
        already there if there is one. The new version of the file is not staged.*/

        // Check if filename exists in HEAD commit
        String HEADCommitID = readContentsAsString(HEAD);
        Commit HEADCommit = Commit.fromFile(Utils.join(COMMITS_DIR,HEADCommitID));
        if (!HEADCommit.containsFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        // read the blob
        blob b = HEADCommit.getBlob(fileName);
        // make new file
        File f = Utils.join(CWD,fileName);
        // overwrite with snapshot version
        writeContents(f,b.getContent());
    }

    public static void checkout(String commitID, String fileName) {
    /*    Takes the version of the file as it exists in the commit with the given id,
    and puts it in the working directory, overwriting the version of the file that’s
    already there if there is one. The new version of the file is not staged.*/

        File commitFile = null;

        // Check if commits id is real
        if (commitID.length() >= 40) {
            commitFile = Utils.join(COMMITS_DIR,commitID);
            if (!commitFile.exists()) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        // abbreviate commits
        if (commitID.length() < 40) {
            File[] filesToCheck = COMMITS_DIR.listFiles();
            for (File f:filesToCheck) {
                if (f.getName().startsWith(commitID)) {
                    commitFile = f;
                }
            }
            if (commitFile == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        // Check if filename exists in commit
        Commit c = Commit.fromFile(commitFile);
        if (!c.containsFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        // read the blob
        blob b = c.getBlob(fileName);
        // make new file
        File f = Utils.join(CWD,fileName);
        // overwrite with snapshot version
        writeContents(f,b.getContent());
    }

    public static void checkoutBranch(String branchName) {
        if (branchName.equals(getHEADBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        if (!listBranch().contains(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        //failure case 3
        HashMap<String, String> files = getHEADCommit().getTree();

        for (File f:CWD.listFiles()) {
            if (!files.keySet().contains(f.getName())) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        // set the head branch
        setHeadBranch(branchName);
        // get the lists of files at branch head
        files = getHEADCommit().getTree();

        for (File f:CWD.listFiles()) {
            if (!files.keySet().contains(f.getName())) {
                f.delete();
            } else {
                blob b = blob.readBlob( Utils.join(BLOBS_DIR, files.get(f.getName())));
                //overwrites cwd file with head commit of that branch
                b.toOriginalFile(Utils.join(CWD, f.getName()));
            }
        }

        clearStagingArea();
    }
    public static void clearStagingArea() {
        // clear the staging area
        Index addIndex = Index.fromFile(ADD_INDEX);
        HashMap<String, String> toAdd = addIndex.getEntries();
        for (String fileName: toAdd.keySet()) {
            File f = Utils.join(ADD_DIR, toAdd.get(fileName));
            f.delete();
            addIndex.removeEntry(fileName);
        }
        addIndex.toFile(ADD_INDEX);

        Index removeIndex = Index.fromFile(REMOVE_INDEX);
        HashMap<String, String> toRemove = removeIndex.getEntries();
        for (String fileName: toRemove.keySet()) {
            removeIndex.removeEntry(fileName);
        }
        removeIndex.toFile(REMOVE_INDEX);
    }

    public static void branch(String branchName) {
        for (File branch:BRANCHES_DIR.listFiles()) {
            if (branch.equals(branchName)) {
                System.out.println("A branch with that name already exists.");
                return;
            }
        }
        File branchFile = Utils.join(BRANCHES_DIR,branchName);
        String headCommit = getHEADCommitID();
        try {
            branchFile.createNewFile();
            writeContents(branchFile, headCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeBranch(String branchName) {
        if (branchName.equals(getHEADBranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File branchFile = Utils.join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else {
            branchFile.delete();
        }
    }

    public static void reset(String commitID) {
        // Check if commits id is real
        if (commitID.length() >= 40) {
            File commitFile = Utils.join(COMMITS_DIR,commitID);
            if (!commitFile.exists()) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        // abbreviate commits
        if (commitID.length() < 40) {
            File commitFile = null;
            File[] filesToCheck = COMMITS_DIR.listFiles();
            for (File f:filesToCheck) {
                if (f.getName().startsWith(commitID)) {
                    commitFile = f;
                }
            }
            if (commitFile == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        // check untracked file
        File commitFile = Utils.join(COMMITS_DIR, getHEADCommitID());
        Commit c = Commit.fromFile(commitFile);
        HashMap<String, String> tree =  c.getTree();

        for (File f:CWD.listFiles()) {
            if (!tree.keySet().contains(f.getName())) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        // move the branch head to that commit
        setBranchHead(getHEADBranch(), commitID);

        // checkout files
        commitFile = Utils.join(COMMITS_DIR, commitID);
        c = Commit.fromFile(commitFile);
        tree =  c.getTree();
        for (File file: CWD.listFiles()) {
            if (!tree.containsKey(file)) {
                file.delete();
            } else {
                // check out
                checkout(commitID, file.getName());
            }
        }
    }

    public static void main(String[] args) {

    }
}

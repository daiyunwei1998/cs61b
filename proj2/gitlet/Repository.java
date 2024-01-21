package gitlet;

//todo delete import edu.princeton.cs.algs4.StdOut;

import java.io.*;
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
    public static final File TRACKED = Utils.join(GITLET_DIR, "INDEX");

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
            // returns the version (sha1) of file staged
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
        Index Tracked = new Index();
        AddIndex.toFile(ADD_INDEX);
        RemoveIndex.toFile(REMOVE_INDEX);
        Tracked.toFile(TRACKED);

        // initial commit
        // todo delete Commit initial = new Commit("initial commit", null);
        Repository.commit("initial commit");

        //TODO It will have a single branch: master, which initially points to this initial commit,
        // and master will be the current branch.
    }

    public static boolean isTracked(String fileName) {
        Index tracked = Index.fromFile(TRACKED);
        return tracked.getEntries().containsKey(fileName);
    }
    public static void add(String fileName) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        // check if in remove area
        Index removeIndex = Index.fromFile(REMOVE_INDEX);
        if (removeIndex.getEntries().containsKey(fileName)) {
            removeIndex.removeEntry(fileName);
            removeIndex.toFile(REMOVE_INDEX);
            return;
        }

        File f = new File(fileName);

        //check if input file exist
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        // get INDEX file
        Index addIndex = Index.fromFile(ADD_INDEX);

        // make a new blob
        Blob b = new Blob(f);

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
        File branchFile = Utils.join(BRANCHES_DIR, getHEADBranch());
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
    public static String getBranchHead(String branchName) {
        // returns the commit id of that branch's current 'head'
        return readContentsAsString(Utils.join(BRANCHES_DIR, branchName));
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
    public static String getFullID(String commitID) {
        if (commitID.length() >= 40) {
            throw new IllegalArgumentException("Not a abbreviated commid ID");
        }

        File commitFile = null;
        File[] filesToCheck = COMMITS_DIR.listFiles();
        for (File f:filesToCheck) {
            if (f.getName().startsWith(commitID)) {
                commitFile = f;
            }
        }
        if (commitFile == null) {
            System.out.println("No commit with that id exists.");
            return null;
        } else {
            return commitFile.getName();
        }

    }

    public static Set<String> getUntracked() {
        Set<String> CWDFileSet = new HashSet<>();
        FileFilter filter = file -> file.isFile();
        for (File f:CWD.listFiles(filter)) {
            CWDFileSet.add(f.getName());
        }

        Set<String> headFileSet = getHEADCommit().getTree().keySet();

        return difference(CWDFileSet, headFileSet);
    }
    public static void commit(String message) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }

        Commit HEADCommit = getHEADCommit();

        // make a new commit object
        Commit newCommit = new Commit(message, HEADCommit);
        if (HEADCommit != null) {
            // read indexes
            Index addIndex = Index.fromFile(ADD_INDEX);
            Index removeIndex = Index.fromFile(REMOVE_INDEX);
            Index tracked = Index.fromFile(TRACKED);

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
                tracked.addEntry(key, ""); // don't have to store version info
                it.remove();
            }
            addIndex.toFile(ADD_INDEX);
            tracked.toFile(TRACKED);

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

    public static void commitMerge(Commit firstParent, String firstParentBranch,
                                   Commit secondParent, String secondParentBranch) {

        // make a new commit object
        Commit newCommit = new MergedCommit(firstParent, firstParentBranch, secondParent, secondParentBranch);

        // update staging area
        // read indexes
        Index addIndex = Index.fromFile(ADD_INDEX);
        Index removeIndex = Index.fromFile(REMOVE_INDEX);
        Index tracked = Index.fromFile(TRACKED);

        // check if nothing changes
        if (addIndex.size() ==0 & removeIndex.size() == 0) {
            //System.out.println("No changes added to the commit.");
            //return; //todo get it back
        }

        // add files
        Set<String> filesToAdd = new HashSet<>();

        Iterator<String> it = addIndex.getEntries().keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            filesToAdd.add(addIndex.get(key));
            newCommit.addFile(key,addIndex.get(key));
            tracked.addEntry(key, ""); // don't have to store version info
            it.remove();
        }
        addIndex.toFile(ADD_INDEX);
        tracked.toFile(TRACKED);

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
            Blob b = new Blob(fStaged);

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
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        Commit c = Repository.getHEADCommit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

        while (!"".equals(c.getParentID())) {
            String formattedDate = dateFormat.format(c.getTimestamp());
            if(c instanceof MergedCommit) {
                System.out.printf("===\ncommit %s\nMerge: %.7s %.7s\nDate: %s\n%s\n\n",
                        c.getSHA1(),
                        ((MergedCommit) c).getFirstParentID(),
                        ((MergedCommit) c).getSecondParentID(),
                        formattedDate,
                        c.getMessage());
            } else {
                System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                        c.getSHA1(),
                        formattedDate,
                        c.getMessage());
            }

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
        if(c instanceof MergedCommit) {
            System.out.printf("===\ncommit %s\nMerge: %s %s\nDate: %s\n%s\n\n",
                    c.getSHA1(),
                    ((MergedCommit) c).getFirstParentID(),
                    ((MergedCommit) c).getSecondParentID(),
                    formattedDate,
                    c.getMessage());
        } else {
            System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                    c.getSHA1(),
                    formattedDate,
                    c.getMessage());
        }
    }

    public static void globalLog() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        File[] commitFiles = COMMITS_DIR.listFiles();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

        /* Though it will never be null since there are always an initial commit*/
        assert commitFiles != null;
        for (File commitFile:commitFiles) {
            Commit c = Commit.fromFile(commitFile);
            String formattedDate = dateFormat.format(c.getTimestamp());
            if(c instanceof MergedCommit) {
                System.out.printf("===\ncommit %s\nMerge: %.7s %.7s\nDate: %s\n%s\n\n",
                        c.getSHA1(),
                        ((MergedCommit) c).getFirstParentID(),
                        ((MergedCommit) c).getSecondParentID(),
                        formattedDate,
                        c.getMessage());
            } else {
                System.out.printf("===\ncommit %s\nDate: %s\n%s\n\n",
                        c.getSHA1(),
                        formattedDate,
                        c.getMessage());
            }
        }
    }

    public static void find(String message) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
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

    public static HashMap<String, String> modified() {
        HashMap<String, String> result = new HashMap<>();
        Index addIndex = Index.fromFile(ADD_INDEX);
        Index removeIndex = Index.fromFile(REMOVE_INDEX);

        for (String fileName: addIndex.getEntries().keySet()) {
            // if deleted in CWD
            if (!Utils.join(CWD, fileName).exists()) {
                result.put(fileName, "(deleted)");
            }

            /*return the hash1 code of given file in commit*/
            File currentVersion = Utils.join(CWD, fileName);
            if (currentVersion.exists()) {
                String version = sha1(readContentsAsString(currentVersion));
                // if staged for addition but version doesn't match
                if (!addIndex.get(fileName).equals(version)) {
                    result.put(fileName, "(modified)");
                }
            }
        }

        HashMap<String, String> commitFiles = getHEADCommit().getTree();
        Set<String> CWDFileSet = new HashSet<>();
        FileFilter filter = file -> file.isFile();
        for (File f:CWD.listFiles(filter)) {
            CWDFileSet.add(f.getName());
        }

        // tracked in the current commit
        for (String fileName: commitFiles.keySet()) {
            // if not staged for rm, tracked in commit and deleted from CWD
            if (!removeIndex.getEntries().containsKey(fileName) && !Utils.join(CWD, fileName).exists()) {
                result.put(fileName, "(deleted)");
            }

            // if changed in CWD
            File currentVersion = Utils.join(CWD, fileName);
            if (currentVersion.exists()) {
                String version = sha1(readContentsAsString(currentVersion));
                if (!commitFiles.get(fileName).equals(version)) {
                    result.put(fileName, "(modified)");
                }
            }

        }
        return result;
    }

    public static void status() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

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


        // get modified set of files
        HashMap<String, String> modifiedFiles = modified();

        /*list the staged files*/
        System.out.println("=== Staged Files ===");
          // read the index
        Index AddIndex = Index.fromFile(ADD_INDEX);
        TreeSet<String> sortedFileNames = new TreeSet<>(AddIndex.getEntries().keySet());
        for (String fileName : sortedFileNames) {
            if (!modifiedFiles.containsKey(fileName)) {
                System.out.println(fileName);
            }
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
        System.out.println("=== Modifications Not Staged For Commit ===");

        for (String f: modifiedFiles.keySet()) {
            System.out.println(f+" "+modifiedFiles.get(f));
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        Index addIndex = Index.fromFile(ADD_INDEX);
        for (String untrackedFile:difference(getUntracked(),addIndex.getEntries().keySet())) {
            if (!modifiedFiles.containsKey(untrackedFile)) {
                System.out.println(untrackedFile);
            }
        }
        System.out.println();
    }

    public static void checkoutFile(String fileName) {
    /*    Takes the version of the file as it exists in the head commit and puts
        it in the working directory, overwriting the version of the file that’s
        already there if there is one. The new version of the file is not staged.*/

        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        // Check if filename exists in HEAD commit
        Commit HEADCommit = getHEADCommit();
        if (!HEADCommit.containsFile(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        // read the blob
        Blob b = HEADCommit.getBlob(fileName);
        // make new file
        File f = Utils.join(CWD,fileName);
        // overwrite with snapshot version
        writeContents(f,b.getContent());
    }

    public static void checkout(String commitID, String fileName) {
    /*    Takes the version of the file as it exists in the commit with the given id,
    and puts it in the working directory, overwriting the version of the file that’s
    already there if there is one. The new version of the file is not staged.*/
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

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
        Blob b = c.getBlob(fileName);
        // make new file
        File f = Utils.join(CWD,fileName);
        // overwrite with snapshot version
        writeContents(f,b.getContent());
    }

    public static  <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        HashSet<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    public static  <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        HashSet<T> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }


    public static void checkoutBranch(String branchName) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        if (branchName.equals(getHEADBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

      if (!branchExist(branchName)) {
          System.out.println("No such branch exists.");
          return;
      }


      Set<String> CWDFileSet = new HashSet<>();
      FileFilter filter = file -> file.isFile();
      for (File f:CWD.listFiles(filter)) {
          CWDFileSet.add(f.getName());
      }

      Set<String> headFileSet = getHEADCommit().getTree().keySet();
      Commit targetCommit = Commit.fromFile(Utils.join(COMMITS_DIR,getBranchHead(branchName)));
      Set<String> commitFileSet = targetCommit.getTree().keySet();
      Set<String> filesToDelete = difference(CWDFileSet, commitFileSet);

      // warn users about untracked files being overwritten
      Set<String> untracked = difference(CWDFileSet, headFileSet);
      if (!intersection(commitFileSet,untracked).isEmpty()) {
          System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
          return;
      }

      // delete files
      for (String fileName:filesToDelete) {
            File fileToDelete = Utils.join(CWD, fileName);
            fileToDelete.delete();
      }

      // overwrite files
        for (String fileName:commitFileSet) {
            Blob blobFile = Blob.readBlob(Utils.join(BLOBS_DIR, targetCommit.getTree().get(fileName)));
            File saveToFile = Utils.join(CWD, fileName);
            blobFile.toOriginalFile(saveToFile);
        }
        updateHEADBranch(branchName);
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
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an in4itialized Gitlet directory.");
            return;
        }

        if (branchExist(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
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

    public static boolean branchExist(String branchName) {
        // check if branch exist
        boolean match = false;
        for(File f:BRANCHES_DIR.listFiles()) {
            if (branchName.equals(f.getName())) {
                match = true;
            }
        }
        return match;
    }
    public static void removeBranch(String branchName) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

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
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

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
           commitID = getFullID(commitID);
           if (commitID == null) {
               System.out.println("No commit with that id exists.");
               return;
           }
        }

        // check untracked file
        Set<String> CWDFileSet = new HashSet<>();
        FileFilter filter = file -> file.isFile();
        for (File f:CWD.listFiles(filter)) {
            CWDFileSet.add(f.getName());
        }
        Set<String> headFileSet = getHEADCommit().getTree().keySet();
        Set<String> commitFileSet = Commit.fromFile(Utils.join(COMMITS_DIR, commitID)).getTree().keySet();

        // warn users about untracked files being overwritten
        Set<String> untracked = difference(CWDFileSet, headFileSet);
        if (!intersection(commitFileSet,untracked).isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }

        // move the branch head to that commit
        setBranchHead(getHEADBranch(), commitID);

        // checkout files
        Commit c = Commit.fromFile(Utils.join(COMMITS_DIR, commitID));
        HashMap<String, String> tree =  c.getTree();
        Set<String> filesToDelete = difference(CWDFileSet, tree.keySet());

        for (String fileName: filesToDelete) {
            Utils.join(CWD, fileName).delete();
        }

        for (String fileName: tree.keySet()) {
            checkout(commitID, fileName);
        }

        // clear the staging area
        clearStagingArea();
    }

    public static void merge(String otherBranch) {
        // check if merge is necessary
        if (UncommittedExist()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (getHEADBranch().equals(otherBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        if (!branchExist(otherBranch)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String LCA = LatestCommonAncestor(getHEADBranch(),otherBranch);
        if (getBranchHead(otherBranch).equals(LCA)) {
            System.out.println("Given branch is an ancestor of the current branch.");
        }

        // check if untracked file exist
        Set<String> CWDFileSet = new HashSet<>();
        FileFilter filter = file -> file.isFile();
        for (File f:CWD.listFiles(filter)) {
            CWDFileSet.add(f.getName());
        }

        Commit firstParent = getHEADCommit();
        String firstParentBranch = getHEADBranch();
        Commit secondParent = Commit.fromFile(Utils.join(COMMITS_DIR, getBranchHead(otherBranch)));
        Commit splitPoint = Commit.fromFile(Utils.join(COMMITS_DIR, LCA));
        boolean conflicted = false;

        Set<String> untracked = difference(CWDFileSet, firstParent.getTree().keySet());
        if (!untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        // case: fast-forward current branch
        if (LCA.equals(getHEADBranch())) {
            checkoutBranch(otherBranch);
            System.out.println("Current branch fast-forwarded.");
        }


        for (String fileName: splitPoint.getTree().keySet()) {
            // Condition: deleted
                // if deleted in both branch
            if (!firstParent.containsFile(fileName) && !secondParent.containsFile(fileName) ) {
                continue;
            }
                // deleted in either
            if (!firstParent.containsFile(fileName)) {
                // deleted in current branch
                if (versionChanged(fileName, secondParent, splitPoint)) {
                    // if modified in other branch
                    mergeConflict(fileName, firstParent, secondParent);
                    conflicted = true;
                    continue;
                } else {
                    // if not modified in other branch, remain absent
                    continue;
                }
            } else if (!secondParent.containsFile(fileName)) {
                // deleted in other branch
                if (versionChanged(fileName, firstParent, splitPoint)) {
                    // if modified in current branch
                    mergeConflict(fileName, firstParent, secondParent);
                    conflicted = true;
                    continue;
                } else {
                    // if not modified in current branch, remove
                    rm(fileName);
                    continue;
                }
            }

            // if both modified
            if (versionChanged(fileName, firstParent, splitPoint) &&
            versionChanged(fileName, secondParent, splitPoint)) {
                // in the same way
                if (Objects.equals(firstParent.getFileVersion(fileName), secondParent.getFileVersion(fileName))) {
                    continue;
                }
                // not in the same way
                if (!Objects.equals(firstParent.getFileVersion(fileName), secondParent.getFileVersion(fileName))) {
                    mergeConflict(fileName, firstParent, secondParent);
                    conflicted = true;
                    continue;
                }
            }

            // if either modified
            if (versionChanged(fileName, firstParent, splitPoint)) {
                // if changed in current branch
                continue;
            } else if (versionChanged(fileName, secondParent, splitPoint)) {
                // if changed in given branch
                checkout(secondParent.getSHA1(), fileName);
                add(fileName);
                continue;
            }
        } // end of for loop

        Set<String> newlyAddedInCurrent = difference(firstParent.getTree().keySet(),splitPoint.getTree().keySet());
        Set<String> newlyAddedInOther = difference(secondParent.getTree().keySet(),splitPoint.getTree().keySet());
        // newly added in both branch
        Set<String> newlyAddedInBoth = intersection(newlyAddedInCurrent, newlyAddedInOther);
        for (String fileName:newlyAddedInBoth) {
            // if same content
            if (firstParent.getFileVersion(fileName).equals(secondParent.getFileVersion(fileName))) {
                continue;
            } else {
                // if not same content
                mergeConflict(fileName, firstParent, secondParent);
                conflicted = true;
                continue;
            }
        }
        // newly added in current branch: do nothing
        // newly added in given branch
        for (String fileName:difference(newlyAddedInOther, newlyAddedInBoth)) {
            checkout(secondParent.getSHA1(), fileName);
            add(fileName);
        }

        // commit
        commitMerge(firstParent, firstParentBranch, secondParent, otherBranch);

        // if any file has a merge conflict
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }

    }

    private static void mergeConflict(String fileName, Commit firstParent, Commit secondParent) {
        StringBuilder output = new StringBuilder("<<<<<<< HEAD\n");
        output.append(Blob.readBlob(Utils.join(BLOBS_DIR, firstParent.getFileVersion(fileName))).getContent());
        output.append("=======\n");
        output.append(Blob.readBlob(Utils.join(BLOBS_DIR, secondParent.getFileVersion(fileName))).getContent());
        output.append(">>>>>>>");
        File newFile = Utils.join(CWD, fileName);
        writeContents(newFile, output.toString());
        add(fileName);
    }

    private static boolean UncommittedExist() {
        // check if any uncommitted changes
        Index addIndex = Index.fromFile(ADD_INDEX);
        Index removeIndex = Index.fromFile(REMOVE_INDEX);

        if (addIndex.isEmpty() && removeIndex.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }

    private static boolean versionChanged(String fileName, Commit commit1, Commit commit2) {
        /*returns true if the file version is different in the commits specified
        * note that if absent in either commits will return true */

        if (!commit1.containsFile(fileName) && !commit2.containsFile(fileName)) {
            return false;
        }
        if (!commit1.containsFile(fileName) || commit2.containsFile(fileName)) {
            return true;
        }

        String version1 = commit1.getVersion(fileName);
        String version2 = commit2.getVersion(fileName);
        if (version1.equals(version2)) {
            return false;
        } else {
            return true;
        }
    }

    private static String LatestCommonAncestor(String branchA, String branchB) {
        // get latest common ancestor
        // algorithmn: Leetcode 160. Intersection of Two Linked Lists
        String CommitA = getBranchHead(branchA);
        String CommitB = getBranchHead(branchB);

        while(!CommitA.equals(CommitB)) {
            CommitA = Commit.fromFile(Utils.join(COMMITS_DIR, CommitA)).getParentID();
            CommitB = Commit.fromFile(Utils.join(COMMITS_DIR, CommitB)).getParentID();
            if (CommitA == null) {
                CommitA = getBranchHead(branchB);
            }
            if (CommitB == null) {
                CommitB = getBranchHead(branchA);
            }
        }
        return CommitA;
    }

    public static void main(String[] args) {
        File f = Utils.join("C:\\Users\\daiyu\\Desktop\\test-gitlet\\.gitlet\\commits\\475d8e9d89b1432fcc6f9aee0ea8df78b2d9b7d0");
        Commit c = Commit.fromFile(f);
        for (String s:c.getTree().keySet()) {
            System.out.println(s);
        }
    }
}

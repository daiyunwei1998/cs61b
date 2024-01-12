package gitlet;

//todo delete import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;

import static gitlet.Utils.*;

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
    public static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    public static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    public static final File ADD_INDEX = Utils.join(ADD_DIR, "INDEX");
    public static final File REMOVE_INDEX = Utils.join(REMOVE_DIR, "INDEX");

    private static class Index implements Serializable {
        private PriorityQueue<Entry> entries;

        private static class Entry {
            private String fileName;
            private String blobName;
            private Entry(String fileName, String blobName) {
                this.fileName = fileName;
                this.blobName = blobName;
            }

            private String getFileName() {
                return this.fileName;
            }

            private String getBlobName() {
                return this.blobName;
            }
        }

        private Index() {
            this.entries = new PriorityQueue<Entry>();
        }

        private static Index fromFile(File fileName) {
            return Utils.readObject(fileName,Index.class);
        }

        private void toFile(File fileName) {
            writeObject(fileName, this);

        }
        private void addEntry(String filename, String blobName) {
            this.entries.add(new Entry(filename, blobName));
        }
        private Entry getEntry() {
            return this.entries.remove();
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
        BLOBS_DIR.mkdir();

        // Create HEAD file
        try {
            // Create a new file
            HEAD.createNewFile();
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
        Commit initial = new Commit("initial commit", null);
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
        Index AddIndex = Index.fromFile(ADD_INDEX);

        // make a new blob
        blob b = new blob(f);

        // get hash (filename of the blob)
        File saveToFile = Utils.join(ADD_DIR, b.getSHA1());

        /** check current version */
        Commit headCommit = getHEADCommit();
        if (Objects.equals(headCommit.getFileID(saveToFile), b.getSHA1())) {
          /*   if file is the same as in current commit
             remove it from toAdd (if any), do nothing*/
            if (saveToFile.exists()) {
                saveToFile.delete();
            }
            return;
        } else {
            // first time addedd or different version
            b.toFile(saveToFile);
            AddIndex.addEntry(f.getName(), b.getFileName());
        }
    }

    public static void updateHEAD(String commitHash) {
        Utils.writeContents(HEAD, commitHash);
    }
    public static String getHEADID() {
        return Utils.readContentsAsString(HEAD);
    }
    public static Commit getHEADCommit() {
        if (getHEADID().isEmpty()) {
            return null;
        }
        File headCommit = Utils.join(Repository.COMMITS_DIR,getHEADID());
        return readObject(headCommit,Commit.class);
    }

    public static void commit(String message) {
        Commit HEADCommit = getHEADCommit();

        // make a new commit object
        Commit newCommit = new Commit(message, HEADCommit);
        if (HEADCommit != null) {
            // read indexes
            Index AddIndex = Index.fromFile(ADD_INDEX);
            Index RemoveIndex = Index.fromFile(REMOVE_INDEX);

            // check if nothing changes
            if (AddIndex.size() ==0 & RemoveIndex.size() == 0) {
                System.out.println("No changes added to the commit.");
            }

            // add files
            while (!AddIndex.isEmpty()) {
                Index.Entry e = AddIndex.getEntry();
                newCommit.addFile(e.getFileName(), e.getBlobName());
                File oldFile = Utils.join(ADD_DIR, e.getBlobName());
                File newFile = Utils.join(BLOBS_DIR,e.getBlobName());
                boolean status = oldFile.renameTo(newFile);
                if (!status) {
                    System.out.println("Commiting staged files unsuccessfully");
                }
            }


            for (File file : filesToAdd) {
                blob b = blob.readBlob(file);
                newCommit.addFile(b);
                File newPath = Utils.join(BLOBS_DIR,b.getSHA1());
                b.toFile(newPath);
                file.delete();
            }

            // remove files
            for (File file : filesToRemove) {
                blob b =  blob.readBlob(file);
                newCommit.removeFIle(b);
                File newPath = Utils.join(BLOBS_DIR,b.getSHA1());
                b.toFile(newPath);
                file.delete();
            }
        }

        // save new commit
        newCommit.toFile();
        // update HEAD
        updateHEAD(newCommit.getSHA1());
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
        /* Printer PQ : creating a PQ that
        * prints the added contents in lexicographic order*/
        PriorityQueue<String> Printer = new PriorityQueue<>();

        // TODO list the branches
        System.out.println("=== Branches ===");
        System.out.println();
        // list the staged files
        System.out.println("=== Staged Files ===");
        File[] stagedFiles = ADD_DIR.listFiles();
        if (stagedFiles != null) {
            for (File f:stagedFiles) {
                blob b = blob.readBlob(f);
                Printer.add(b.getFileName());
            }
        }
        while (!Printer.isEmpty()) {
            String info = Printer.remove();
            System.out.println(info);
        }
        System.out.println();
        // list the removed files
        System.out.println("=== Removed Files ===");
        File[] removedFiles = REMOVE_DIR.listFiles();
        if (removedFiles != null) {
            for (File f:removedFiles) {
                blob b = blob.readBlob(f);
                Printer.add(b.getFileName());
            }
        }
        while (!Printer.isEmpty()) {
            String info = Printer.remove();
            System.out.println(info);
        }
        System.out.println();
        // TODO list the modified files
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        // TODO list the untracked files
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkout(String fileName) {
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

    public static void main(String[] args) {

    }
}

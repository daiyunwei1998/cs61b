package gitlet;

//todo delete import edu.princeton.cs.algs4.StdOut;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

        // initial commit
        Commit initial = new Commit("initial commit", null);

        //change HEAD to this initial commit
        updateHEAD(initial.getSHA1());

        //TODO It will have a single branch: master, which initially points to this initial commit, and master will be the current branch.
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

        // make a new blob
        blob b = new blob(f);
        // get hash (filename of the blob)
        File saveToFile = Utils.join(ADD_DIR, b.getSHA1());

        /** check current version */
        Commit headCommit = getHEADCommit();
        if (headCommit.getFileID(saveToFile) == b.getSHA1()) {
            // if file is the same as in current commit
            // remove it from toAdd (if any), do nothing
            if (saveToFile.exists()) {
                saveToFile.delete();
            }
            return;
        } else {
            // first time addedd or different version
            b.toFile(saveToFile);
        }
    }

    public static void updateHEAD(String commitHash) {
        Utils.writeContents(HEAD, commitHash);
    }
    public static String getHEADID() {
        return Utils.readContentsAsString(HEAD);
    }
    public static Commit getHEADCommit() {
        File headCommit = Utils.join(Repository.COMMITS_DIR,getHEADID());
        return readObject(headCommit,Commit.class);
    }

    public static void commit(String message) {
        // make a new commit object
        Commit newCommit = new Commit(message, getHEADCommit());

        File[] filesToAdd = ADD_DIR.listFiles();
        File[] filesToRemove = REMOVE_DIR.listFiles();

        if (filesToAdd.length ==0 & filesToRemove.length == 0) {
            System.out.println("No changes added to the commit.");
        }

        // add files
        for (File file : filesToAdd) {
            blob b = new blob(file);
            newCommit.addFIle(b);
            file.delete();
        }

        // remove files
        for (File file : filesToRemove) {
            blob b = new blob(file);
            newCommit.removeFIle(b);
            file.delete();
        }
        // save new commit
        newCommit.toFile();
        // update HEAD
        updateHEAD(newCommit.getSHA1());
    }

    public static void log() {
        Commit c = Repository.getHEADCommit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

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


    public static void main(String[] args) {
        init();
        log();
    }
}

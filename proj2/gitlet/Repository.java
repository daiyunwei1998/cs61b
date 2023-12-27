package gitlet;

import java.io.File;
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

    /* TODO: fill in the rest of this class. */
    public static void init() {
        // check if .gitlet exist, if not, mkdir()
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();

        // create sub-directory
        STAGING_DIR.mkdir();
        ADD_DIR.mkdir();
        REMOVE_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();

        // fresh commit
        Commit initial = new Commit("initial commit", null);

        //TODO It will have a single branch: master, which initially points to this initial commit, and master will be the current branch.
    }

    public static void add(String fileName) {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        File f = new File(fileName);

        //check if file exist
        if (!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        blob b = new blob(f);
        b.toFile(ADD_DIR);
    }
}

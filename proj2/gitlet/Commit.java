package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    // every commit point to its parent
    private Commit parent;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, Commit parent) {
        if (!Utils.join(Repository.CWD, ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        this.message = message;
        this.parent = parent;
        if (this.parent == null) {
            this.timestamp = new Date(0);
        } else {
            this.timestamp = new Date();
        }
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public Commit getParent() {
        return this.parent;
    }

    public static void main(String[] args) {
        //test only
        Commit c1 = new Commit("test",null);
        Commit c2 = new Commit("test",c1);
    }
}

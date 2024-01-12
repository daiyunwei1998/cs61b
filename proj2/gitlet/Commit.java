package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.readObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 */
public class Commit implements Serializable {
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

    /** every commit point to its parent
     use string to avoid serializing pointers */
    private String parentID;

    /** the hash of its tree object */
    private HashMap<String, String> tree;

    private String sha1;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, Commit parent) {
        if (!Utils.join(Repository.CWD, ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        this.message = message;
        this.tree = new HashMap<>();

        if (parent == null) {
            // initial commit
            this.parentID = "";
            this.timestamp = new Date(0);
        } else {
            this.parentID = parent.getSHA1();
            this.timestamp = new Date();
            // copy the parent snapshot
            this.tree = parent.tree;
        }
        this.updateSHA1();
    }

    public void updateSHA1() {
        StringBuilder treeContent = new StringBuilder();
        for (Map.Entry<String, String> entry : tree.entrySet()) {
            treeContent.append(entry.getKey())
                    .append(entry.getValue());
        }
        // Concatenate relevant information (message, timestamp, parent SHA-1)
        String info = this.message +
                this.timestamp.toString() +
                (this.parentID != null ? this.parentID: "") +
                treeContent.toString();
        this.sha1 = Utils.sha1(info);
    }

    public String getSHA1() {
        return this.sha1;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getParentID() {
        return this.parentID;
    }

    public void addFile(String fileName, String blobName) {
        // update tree object
        tree.put(fileName, blobName);
        this.updateSHA1();
    }

    public void removeFIle(String fileName, String blobName) {
        // remove an object from tree
        tree.put(fileName, blobName);
        this.updateSHA1();
    }

    public boolean containsFile(String fileName) {
        return tree.containsKey(fileName);
    }
    public blob getBlob(String fileName) {
        /* given filename returns the blob file*/
        if (!containsFile(fileName)) {
            return null;
        }
        String blobID = tree.get(fileName);
        File blobFile = Utils.join(Repository.BLOBS_DIR,blobID);
        return blob.readBlob(blobFile);
    }

    public void toFile() {
        // make a commit object
        File newCommitObject = Utils.join(Repository.COMMITS_DIR, this.getSHA1());

        try {
            // Create a new file
            newCommitObject.createNewFile();
            Utils.writeObject(newCommitObject, this);
        } catch (IOException e) {
            // Handle potential IOException (e.g., permission issues)
            e.printStackTrace();
        }
    }

    public static Commit fromFile(File commitFile) {
        Commit c = readObject(commitFile, Commit.class);
        return c;
    }

    /** get hash of a file in tree*/
    public String getFileID(File file) {
        String fileName = file.getName();
        return tree.get(fileName);
    }
    public static void main(String[] args) {
    }
}

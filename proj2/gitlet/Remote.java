package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Remote{
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REMOTE = Utils.join(GITLET_DIR, "REMOTE");
    public static void addRemote(String remoteName, String remoteDir) {
        HashMap<String, String> remoteMap = readObject(REMOTE, HashMap.class);
        if (remoteMap.containsKey(remoteName)) {
            remoteMap.put(remoteName, remoteDir.replaceAll("/", File.separator));
        } else {
            System.out.println("A remote with that name already exists.");
            return;
        }
        writeObject(REMOTE, remoteMap);
    }

    public static void removeRemote(String remoteName) {
        HashMap<String, String> remoteMap = readObject(REMOTE, HashMap.class);
        if (remoteMap.containsKey(remoteName)) {
            remoteMap.remove(remoteName);
        } else {
            System.out.println("A remote with that name does not exist.");
            return;
        }
        writeObject(REMOTE, remoteMap);
    }

    private static String getRemoteDir(String remoteName) {
        HashMap<String, String> remoteMap = readObject(REMOTE, HashMap.class);
        if (remoteMap.containsKey(remoteName)) {
            return remoteMap.get(remoteName);
        } else {
            System.out.println("A remote with that name does not exist.");
            return null;
        }

    }
    public static void push(String remoteName, String branchName) {
        String remoteDirString = getRemoteDir(remoteName);
        if (remoteDirString == null) {
            System.out.println("Remote directory not found.");
            return;
        }

        // check if remote head of branch in local current branch history
        String remoteHeadID = getRemoteBranchHead(remoteName, branchName);
        if (isInHistory(remoteHeadID)) {
            // append commits (brute force)
            File remoteBlobDir = Utils.join(remoteDirString, "blobs");
            for (File blob: BLOBS_DIR.listFiles()) {
                File newLocation = Utils.join(remoteBlobDir,blob.getName());
                boolean status = blob.renameTo(newLocation);
                /*if (!status) {
                    System.out.println("Commiting staged files unsuccessfully");
                }*/
            }
            File remoteCommitDir = Utils.join(remoteDirString, "commits");
            for (File commit: COMMITS_DIR.listFiles()) {
                File newLocation = Utils.join(remoteCommitDir,commit.getName());
                boolean status = commit.renameTo(newLocation);
                /*if (!status) {
                    System.out.println("Commiting staged files unsuccessfully");
                }*/
            }
            setRemoteBranchHead(remoteName, branchName, getHEADCommitID());
        } else {
            System.out.println("Please pull down remote changes before pushing.");
        }

    }

    private static boolean isInHistory(String commitID) {
        // BFS: Initialize the fringe
        Queue<String> fringe = new LinkedList<String>();
        HashSet<String> table = new HashSet<>(); // search records

        // BFS level-order transversal
        fringe.offer(getHEADCommitID());
        while (!fringe.isEmpty()) {
            String localCommitID = fringe.peek();
            Commit c = Commit.fromFile(Utils.join(COMMITS_DIR, commitID));
            if (!table.contains(c.getSHA1())) {
                // if not transversed
                if (commitID.equals(localCommitID)) {return true;}
                table.add(c.getSHA1());
                if (c instanceof MergedCommit) {
                    fringe.offer(((MergedCommit) c).getFirstParentID());
                    fringe.offer(((MergedCommit) c).getSecondParentID());
                } else {
                    if (!c.getParentID().equals("")) {
                        fringe.offer(c.getParentID());
                    }
                }
            }
            fringe.poll(); // remove first
        }
        return false;
    }
    public static String getRemoteBranchHead(String remoteName, String branchName) {
        File remoteBranchDir = Utils.join(getRemoteDir(remoteName), "branches");
        // returns the commit id of that branch's current 'head'
        return readContentsAsString(Utils.join(remoteBranchDir, branchName));
    }

    public static void setRemoteBranchHead(String remoteName, String branchName, String commitID) {
        writeContents(Utils.join(getRemoteDir(remoteName), "branches",branchName), commitID);
    }
}

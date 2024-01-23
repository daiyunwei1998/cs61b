package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static gitlet.Utils.*;

public class Remote extends Repository{
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File REMOTE = Utils.join(GITLET_DIR, "REMOTE");
    public static void addRemote(String remoteName, String remoteDir) {
        Index remoteIndex = Repository.Index.fromFile(REMOTE_INDEX);
        if (!remoteIndex.getEntries().containsKey(remoteName)) {
            remoteIndex.addEntry(remoteName, remoteDir.replaceAll("/", File.separator));
        } else {
            System.out.println("A remote with that name already exists.");
            return;
        }
        remoteIndex.toFile(REMOTE_INDEX);
    }

    public static void removeRemote(String remoteName) {
        Index remoteIndex = Repository.Index.fromFile(REMOTE_INDEX);
        if (remoteIndex.getEntries().containsKey(remoteName)) {
            remoteIndex.removeEntry(remoteName);
        } else {
            System.out.println("A remote with that name does not exist.");
            return;
        }
        remoteIndex.toFile(REMOTE_INDEX);
    }

    private static String getRemoteDir(String remoteName) {
        Index remoteIndex = Repository.Index.fromFile(REMOTE_INDEX);
        if (remoteIndex.getEntries().containsKey(remoteName)) {
            return remoteIndex.get(remoteName);
        } else {
            return null;
        }

    }
    public static void push(String remoteName, String branchName) {
        String remoteDirString = getRemoteDir(remoteName);
        if (remoteDirString == null) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteDir = new File(remoteDirString);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        // check if remote branch exist
        if (!remoteBranchExist(remoteName, branchName)) {
            branchRemote(remoteName, branchName);
        }

        // check if remote head of branch in local current branch history
        String remoteHeadID = getRemoteBranchHead(remoteName, branchName);
        if (isInHistory(remoteHeadID)) {
            // append commits (brute force)
            File remoteBlobDir = Utils.join(remoteDirString, "blobs");
            for (File blob: Objects.requireNonNull(BLOBS_DIR.listFiles())) {
                File newLocation = Utils.join(remoteBlobDir,blob.getName());
                copyFile(blob, newLocation);
            }
            File remoteCommitDir = Utils.join(remoteDirString, "commits");
            for (File commit: Objects.requireNonNull(COMMITS_DIR.listFiles())) {
                File newLocation = Utils.join(remoteCommitDir,commit.getName());
                copyFile(commit, newLocation);
            }
            setRemoteBranchHead(remoteName, branchName, getHEADCommitID());
        } else {
            System.out.println("Please pull down remote changes before pushing.");
        }

    }
    public static void copyFile(File source, File target) {
        Path sourceDir = Paths.get(source.getPath());
        Path targetDir = Paths.get(target.getPath());
        try {
            Files.copy(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void fetch(String remoteName, String remoteBranchName) {
        // check if remote .gitlet exist
        String remoteDirString = getRemoteDir(remoteName);
        if (remoteDirString == null) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteDir = new File(remoteDirString);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        // check if remote branch exist
        if (!remoteBranchExist(remoteName, remoteBranchName)) {
            System.out.println("That remote does not have that branch.");
            return;
        }

        // check branch folder
        if (!Utils.join(BRANCHES_DIR, remoteName).exists()) {
           Utils.join(BRANCHES_DIR, remoteName).mkdir();
        }
        if (!Utils.join(BRANCHES_DIR, remoteName, remoteBranchName).exists()) {
            try {
                Utils.join(BRANCHES_DIR, remoteName, remoteBranchName).createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // move all blobs and commits
        File remoteBlobDir = Utils.join(remoteDirString,"blobs");
        for (File blob: Objects.requireNonNull(remoteBlobDir.listFiles())) {
            File newLocation = Utils.join(BLOBS_DIR,blob.getName());
            copyFile(blob, newLocation);
        }
        File remoteCommitDir = Utils.join(remoteDirString, "commits");
        for (File commit: Objects.requireNonNull(remoteCommitDir.listFiles())) {
            File newLocation = Utils.join(COMMITS_DIR,commit.getName());
            copyFile(commit, newLocation);

        }
        writeContents(Utils.join(BRANCHES_DIR, remoteName, remoteBranchName), getRemoteBranchHead(remoteName, remoteBranchName));
    }

    public static boolean remoteBranchExist(String remoteName, String branchName) {
        // check if branch exist
        return Utils.join(getRemoteDir(remoteName), "branches", branchName).exists();
    }

    private static boolean isInHistory(String commitID) {
        // BFS: Initialize the fringe
        Queue<String> fringe = new LinkedList<String>();
        HashSet<String> table = new HashSet<>(); // search records

        // BFS level-order transversal
        fringe.offer(getHEADCommitID());
        while (!fringe.isEmpty()) {
            String localCommitID = fringe.peek();
            Commit c = Commit.fromFile(Utils.join(COMMITS_DIR, localCommitID));
            if (!table.contains(c.getSHA1())) {
                // if not transversed
                if (commitID.equals(localCommitID)) {return true;}
                table.add(c.getSHA1());
                if (c instanceof MergedCommit) {
                    fringe.offer(((MergedCommit) c).getFirstParentID());
                    fringe.offer(((MergedCommit) c).getSecondParentID());
                } else {
                    if (!c.getParentID().isEmpty()) {
                        fringe.offer(c.getParentID());
                    }
                }
            }
            fringe.poll(); // remove first
        }
        return false;
    }

    public static void branchRemote(String remoteName, String branchName) {
        // check if remote .gitlet exist
        String remoteDirString = getRemoteDir(remoteName);
        if (remoteDirString == null) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteDir = new File(remoteDirString);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }

        if (remoteBranchExist(remoteName, branchName)) {
            System.out.println("A branch with that name already exists in remote.");
            return;
        }

        File branchFile = Utils.join(remoteDirString, "branches", branchName);
        String headCommit = getRemoteHead(remoteName);
        try {
            branchFile.createNewFile();
            writeContents(branchFile, headCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getRemoteBranchHead(String remoteName, String branchName) {
        // returns the commit id of that branch's current 'head'
        return readContentsAsString(Utils.join(getRemoteDir(remoteName), "branches", branchName));
    }

    public static String getRemoteHead(String remoteName) {
        // returns the commit id of remote's current 'head'
        String headBranch = Utils.readContentsAsString(Utils.join(getRemoteDir(remoteName), "HEAD"));
        File branchFile = Utils.join(Utils.join(getRemoteDir(remoteName), "branches"), headBranch);
        String commitID = Utils.readContentsAsString(branchFile);
        return commitID;
    }

    public static void setRemoteBranchHead(String remoteName, String branchName, String commitID) {
        writeContents(Utils.join(getRemoteDir(remoteName), "branches",branchName), commitID);
    }
}

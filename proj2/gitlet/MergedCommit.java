package gitlet;

public class MergedCommit extends Commit {
    private String firstParentID;
    private String firstParentBranch;
    private String secondParentID;
    private String secondParentBranch;
    public MergedCommit(Commit firstParent, String firstParentBranch,
                        Commit secondParent, String secondParentBranch) {
        super("Merged " + secondParentBranch
                + " into " + firstParentBranch + ".", firstParent);
        this.firstParentID = firstParent.getSHA1();
        this.secondParentID = secondParent.getSHA1();
        this.firstParentBranch = firstParentBranch;
        this.secondParentBranch = secondParentBranch;
    }

    public String getFirstParentID() {
        return firstParentID;
    }

    public String getFirstParentBranch() {
        return firstParentBranch;
    }

    public String getSecondParentID() {
        return secondParentID;
    }

    public String getSecondParentBranch() {
        return secondParentBranch;
    }
}

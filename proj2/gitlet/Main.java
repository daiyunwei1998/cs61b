package gitlet;
import java.util.Objects;


/** Driver class for Gitlet, a subset of the Git version-control system.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                if (args.length == 2) {
                    Repository.add(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "commit":
                if (args.length >= 2) {
                    Repository.commit(args[1]);
                } else {
                    System.out.println("Please enter a commit message.");
                }
                break;
            case "rm":
                if (args.length == 2) {
                    Repository.rm(args[1]);
                } else {
                    System.out.println(args[1]);
                    System.out.println("Incorrect operands.");
                }
                break;
            case "log":
                if (args.length == 1) {
                    Repository.log();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "global-log":
                Repository.globalLog();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.find(args[1]);
                }
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if (args.length == 3 && Objects.equals(args[1], "--")) {
                    // case 1
                    String filename = args[2];
                    Repository.checkoutFile(filename);
                } else if (args.length == 4 && Objects.equals(args[2], "--")) {
                    // case 2
                    String commitID = args[1];
                    String fileName = args[3];
                    Repository.checkout(commitID, fileName);

                } else if (args.length == 2) {
                    // case 1
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }

                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.branch(args[1]);
                }
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.removeBranch(args[1]);
                }
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.reset(args[1]);
                }
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.merge(args[1]);
                }
                break;
            case "add-remote":
                if (args.length != 3) {
                    System.out.println("Incorrect operands.");
                } else {
                    Remote.addRemote(args[1], args[2]);
                }
                break;
            case "rm-remote":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else {
                    Remote.removeRemote(args[1]);
                }
                break;
            case "push":
                if (args.length != 3) {
                    System.out.println("Incorrect operands.");
                } else {
                    Remote.push(args[1], args[2]);
                }
                break;
            case "fetch":
                if (args.length != 3) {
                    System.out.println("Incorrect operands.");
                } else {
                    Remote.fetch(args[1], args[2]);
                }
                break;
            case "pull":
                if (args.length != 3) {
                    System.out.println("Incorrect operands.");
                } else {
                    Remote.pull(args[1], args[2]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}

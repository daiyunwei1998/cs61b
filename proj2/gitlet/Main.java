package gitlet;

import java.io.File;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
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
        // TODO: what if args is empty?
        // TODO : all methods should check if input args if valid (in terms of number)
        // TODO : check if in git repo
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
            case "add":
                if (args.length == 2) {
                    Repository.add(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                }


                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
            case "commit":

                // TODO: add commit command
                break;
            case "log":
                //TODO : log command
                break;
            case "global-log":
                //TODO global - log
                break;
            case "find":
                //TODO find
                break;
            case "status":
                //TODO status
                break;
            case "checkout":
                //TODO checkout
                break;
            case "branch":
                //TODO branch
                break;
            case "rm-branch":
                //TODO rm-branch
                break;
            case "reset":
                //TODO reset
                break;
            case "merge":
                //TODO merge
                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}

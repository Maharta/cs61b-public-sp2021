package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        // populate staging area map if gitlet directory detected
        if (Repository.GITLET_DIR.exists()) {
            Repository.populateStagingAreaMap();
        }
        try {
            validateOperands(args);
            switch (args[0]) {
                case "init":
                    Gitlet.handleInit();
                    break;
                case "add":
                    validateGitletRepository();
                    Gitlet.handleAdd(args[1]);
                    break;
                case "commit":
                    validateGitletRepository();
                    Gitlet.handleCommit(args[1]);
                    break;
                case "rm":
                    validateGitletRepository();
                    Gitlet.handleRm(args[1]);
                    break;
                case "log":
                    validateGitletRepository();
                    Gitlet.handleLog();
                    break;
                case "global-log":
                    validateGitletRepository();
                    Gitlet.handleGlobalLog();
                    break;
                case "find":
                    validateGitletRepository();
                    Gitlet.handleFind(args[1]);
                    break;
                case "status":
                    validateGitletRepository();
                    Gitlet.handleStatus();
                    break;
                case "checkout":
                    validateGitletRepository();
                    Gitlet.handleCheckout(args);
                    break;
                case "branch":
                    validateGitletRepository();
                    Gitlet.handleBranch(args[1]);
                    break;
                case "rm-branch":
                    validateGitletRepository();
                    Gitlet.handleRmBranch(args[1]);
                    break;
                case "reset":
                    validateGitletRepository();
                    Gitlet.handleReset(args[1]);
                    break;
                case "merge":
                    validateGitletRepository();
                    Gitlet.handleMerge(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    break;
            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

    private static void validateOperands(String[] args) {
        if (args.length == 0) {
            throw Utils.error("Please enter a command.");
        }
        switch (args[0]) {
            case "commit":
                if (args.length != 2 || args[1].isBlank()) {
                    throw Utils.error("Please enter a commit message.");
                }
                break;
            case "add":
            case "rm":
            case "find":
            case "branch":
            case "rm-branch":
            case "reset":
            case "merge":
                if (args.length != 2 || args[1].isBlank()) {
                    throw Utils.error("Incorrect operands.");
                }
                break;
            case "checkout":
                if (args.length < 2) {
                    throw Utils.error("Incorrect operands.");
                }

        }
    }

    private static void validateGitletRepository() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}

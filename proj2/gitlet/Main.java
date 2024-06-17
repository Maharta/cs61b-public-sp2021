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
        // TODO: what if args is empty?
        String firstArg = "";
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        } else {
            firstArg = args[0];
        }
        switch (firstArg) {
            case "init":
                Repository.setupPersistence();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateGitletRepository();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
            // TODO: FILL THE REST IN
        }
    }

    private static void validateGitletRepository() {
        if (Repository.GITLET_DIR.exists()) {
            return;
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}

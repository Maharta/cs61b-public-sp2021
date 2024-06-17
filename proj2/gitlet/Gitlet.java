package gitlet;

public class Gitlet {

    public static void handleInit() {
        if (Repository.GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        Repository.setupPersistence();

    }
}

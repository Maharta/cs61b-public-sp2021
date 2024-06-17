package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    public static final File BRANCH_DIR = Utils.join(GITLET_DIR, "refs", "branches");

    public static final File COMMIT_DIR = Utils.join(GITLET_DIR, "commits");
    public static final File BLOB_DIR = Utils.join(GITLET_DIR, "blobs");

    /* TODO: fill in the rest of this class. */
    public static void setupPersistence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        if (!BRANCH_DIR.exists()) {
            BRANCH_DIR.mkdir();
        }
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
        if (!BLOB_DIR.exists()) {
            BLOB_DIR.mkdir();
        }
        Commit commit = new Commit(Utils.getUnixEpoch(), new HashMap<>(), new ArrayList<>(), "initial commit");
        String sha1 = Utils.sha1(commit.toString());
        Utils.writeObject(Utils.join(COMMIT_DIR, sha1), commit);
        Utils.writeContents(Utils.join(BRANCH_DIR, "master"), sha1);

    }


}

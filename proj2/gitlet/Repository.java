package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;


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
     * <p>
     * <p>
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    static HashMap<String, String> stagingAreaMap;
    static HashMap<String, String> stagingRemovalMap;
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
    public static final File STAGING_BLOB_DIR = Utils.join(BLOB_DIR, "staging");

    public static String MASTER_BRANCH = "master";

    /* TODO: fill in the rest of this class. */
    public static void setupPersistence() {
        if (!GITLET_DIR.exists()) {
            boolean success = GITLET_DIR.mkdir();
            if (!success) throw Utils.error("Failed to make GITLET_DIR");
        }
        if (!BRANCH_DIR.exists()) {
            boolean success = BRANCH_DIR.mkdirs();
            if (!success) throw Utils.error("Failed to make BRANCH_DIR");
        }
        if (!COMMIT_DIR.exists()) {
            boolean success = COMMIT_DIR.mkdir();
            if (!success) throw Utils.error("Failed to make COMMIT_DIR");
        }
        if (!BLOB_DIR.exists()) {
            boolean success = BLOB_DIR.mkdir();
            if (!success) throw Utils.error("Failed to make BLOB_DIR");
        }
        if (!STAGING_BLOB_DIR.exists()) {
            boolean success = STAGING_BLOB_DIR.mkdir();
            if (!success) throw Utils.error("Failed to make STAGING_BLOB_DIR");
        }

        Commit commit = new Commit(Utils.getUnixEpoch(), new HashMap<>(), null, "initial commit");
        String sha1 = Utils.sha1(commit.toString());
        persistCommit(commit, sha1, MASTER_BRANCH);
        // make HEAD persistent pointer and point it to refs/branches/master
        Utils.writeContents(Utils.join(GITLET_DIR, "HEAD"), "ref:refs/branches/master");
        // make STAGING file to keep track of what files are in the staging area.
        Utils.writeObject(Utils.join(GITLET_DIR, "STAGING"), new HashMap<>());
        // make RM file to keep track of what files are staged for removal
        Utils.writeObject(Utils.join(GITLET_DIR, "RM"), new HashMap<>());
    }


    public static void populateStagingAreaMap() {
        stagingAreaMap = Utils.readObject(Utils.join(GITLET_DIR, "STAGING"), HashMap.class);
        stagingRemovalMap = Utils.readObject(Utils.join(GITLET_DIR, "RM"), HashMap.class);
    }

    public static void persistStagingAreaMap() {
        Utils.writeObject(Utils.join(GITLET_DIR, "STAGING"), stagingAreaMap);
        Utils.writeObject(Utils.join(GITLET_DIR, "RM"), stagingRemovalMap);
    }

    public static void persistCommit(Commit commit, String branch) {
        String sha1 = Utils.sha1(commit.toString());
        Utils.writeObject(Utils.join(COMMIT_DIR, sha1), commit);
        // update branch pointer
        Utils.writeContents(Utils.join(BRANCH_DIR, branch), sha1);
    }

    public static void persistCommit(Commit commit, String commitHash, String branch) {
        Utils.writeObject(Utils.join(COMMIT_DIR, commitHash), commit);
        // update branch pointer
        Utils.writeContents(Utils.join(BRANCH_DIR, branch), commitHash);
    }

    public static void removeFilesFromStagingArea() {
        for (File file : Objects.requireNonNull(STAGING_BLOB_DIR.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
}

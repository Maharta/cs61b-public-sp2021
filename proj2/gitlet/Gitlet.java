package gitlet;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static gitlet.Repository.*;

public class Gitlet {

    public static void handleInit() {
        if (Repository.GITLET_DIR.exists()) {
            throw Utils.error("A Gitlet version-control system already exists in the current directory.");
        }
        Repository.setupPersistence();
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command).
     * For this reason, adding a file is also called staging the file for addition.
     * Staging an already-staged file overwrites the previous entry in the staging area with the
     * new contents. The staging area should be somewhere in .gitlet. If the current working version
     * of the file is identical to the version in the current commit, do not stage it to be added,
     * and remove it from the staging area if it is already there (as can happen when a file is changed,
     * added, and then changed back to its original version). The file will no longer be staged for
     * removal (see gitlet rm), if it was at the time of the command.
     */
    public static void handleAdd(String fileName) {
        File file = Utils.join(CWD, fileName);
        if (!file.exists()) {
            throw Utils.error("File does not exist.");
        }
        Commit curr = getCurrentCommit();
        Map<String, String> commitFileSha1Map = curr.fileBlobsha1Map;
        String fileContent = Utils.readContentsAsString(file);
        String sha1 = Utils.sha1(fileContent);


        if (stagingAreaMap.get(fileName) == null) {
            // file not staged yet
            if (Objects.equals(commitFileSha1Map.get(fileName), sha1)) {
                // working directory identical to current commit
                return;
            }
            Utils.writeContents(Utils.join(STAGING_BLOB_DIR, sha1), fileContent);
            stagingAreaMap.put(fileName, sha1);
            persistStagingAreaMap();
        } else {
            // staged file identical to file added
            if (Objects.equals(stagingAreaMap.get(fileName), sha1)) {
                return;
            }
            if (Objects.equals(commitFileSha1Map.get(fileName), sha1)) {
                // staged, but added back and contents are the same as curr commit
                stagingAreaMap.remove(fileName);
                persistStagingAreaMap();
            } else {
                // staged, but different contents.
                Utils.writeContents(Utils.join(STAGING_BLOB_DIR, sha1), fileContent);
                stagingAreaMap.put(fileName, sha1);
                persistStagingAreaMap();
            }
        }
    }

    private static Commit getCurrentCommit() {
        String HEAD = Utils.readContentsAsString(Utils.join(CWD, ".gitlet", "HEAD"));
        String commitHash = "";
        if (HEAD.startsWith("ref:")) {
            String branchPath = HEAD.split(":")[1];
            commitHash = Utils.readContentsAsString(Utils.join(GITLET_DIR, branchPath));
        } else {
            commitHash = HEAD;
        }
        return Utils.readObject(Utils.join(COMMIT_DIR, commitHash), Commit.class);
    }

}

package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;


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
        File file = Utils.join(Repository.CWD, fileName);
        if (!file.exists()) {
            throw Utils.error("File does not exist.");
        }
        Commit curr = getCurrentCommit();
        Map<String, String> commitFileSha1Map = curr.fileBlobsha1Map;
        String fileContent = Utils.readContentsAsString(file);
        String sha1 = Utils.sha1(fileContent);

        if (Repository.stagingAreaMap.get(fileName) == null) {
            // file not staged yet
            if (Objects.equals(commitFileSha1Map.get(fileName), sha1)) {
                // working directory identical to current commit
                return;
            }
            Utils.writeContents(Utils.join(Repository.STAGING_BLOB_DIR, sha1), fileContent);
            Repository.stagingAreaMap.put(fileName, sha1);
            Repository.persistStagingAreaMap();
        } else {
            // staged file identical to file added
            if (Objects.equals(Repository.stagingAreaMap.get(fileName), sha1)) {
                return;
            }
            if (Objects.equals(commitFileSha1Map.get(fileName), sha1)) {
                // staged, but added back and contents are the same as curr commit
                Repository.stagingAreaMap.remove(fileName);
                Repository.persistStagingAreaMap();
            } else {
                // staged, but different contents.
                Utils.writeContents(Utils.join(Repository.STAGING_BLOB_DIR, sha1), fileContent);
                Repository.stagingAreaMap.put(fileName, sha1);
                Repository.persistStagingAreaMap();
            }
        }
    }

    /**
     * Handles commit given by user.
     * Invariants:
     * 1. files that is in the staging area is different from the file on the current commit.
     * 2. commitMessage is not empty
     */
    public static void handleCommit(String commitMessage) {
        if (Repository.stagingAreaMap.isEmpty() && Repository.stagingRemovalMap.isEmpty()) {
            throw Utils.error("No changes added to the commit");
        }
        // get current commit from the branch
        Commit current = getCurrentCommit();
        //modify currentCommit to newCommit, link newCommit parent to current
        Commit newCommit = newCommit(current, commitMessage);
        // persist new Commit
        Repository.persistCommit(newCommit, getCurrentBranch());

        // delete all the unnecessary files left in the staging-blob
        Repository.removeFilesFromStagingArea();

        // clean staging area and persist the changes
        Repository.stagingAreaMap.clear();
        Repository.stagingRemovalMap.clear();
        Repository.persistStagingAreaMap();
    }

    private static Commit newCommit(Commit currentCommit, String message) {
        String parentSha1 = Utils.sha1(currentCommit.toString());

        Repository.stagingAreaMap.forEach((key, value) -> {
            // update commit fileBlobSha1Map with the staged files.
            currentCommit.fileBlobsha1Map.put(key, value);
            // move the file from the staging-blob to blob
            try {
                Files.move(Paths.get(Repository.STAGING_BLOB_DIR.toString(), value),
                        Paths.get(Repository.BLOB_DIR.toString(), value),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw Utils.error("Error moving file from staging-blob area to blob area.");
            }
        });

        Repository.stagingRemovalMap.forEach((key, value) -> {
            // update commit fileBlobSha1Map with rm-ed files.
            currentCommit.fileBlobsha1Map.remove(key);
        });

        currentCommit.date = new Date();
        currentCommit.parentCommits = Map.of("first", parentSha1);
        currentCommit.message = message;
        return currentCommit;
    }

    public static void handleRm(String fileName) {
        Commit commit = getCurrentCommit();
        if (Repository.stagingAreaMap.get(fileName) == null && commit.fileBlobsha1Map.get(fileName) == null) {
            throw Utils.error("No reason to remove the file.");
        }

        if (Repository.stagingAreaMap.get(fileName) != null) {
            Repository.stagingAreaMap.remove(fileName);
            Repository.persistStagingAreaMap();
            return;
        }

        if (commit.fileBlobsha1Map.get(fileName) != null) {
            Repository.stagingRemovalMap.put(fileName, null);
            Repository.persistStagingAreaMap();
            if (Utils.join(Repository.CWD, fileName).exists()) {
                File file = new File(fileName);
                file.delete();
            }
        }
    }

    private static Commit getCurrentCommit() {
        return Utils.readObject(Utils.join(Repository.COMMIT_DIR, getCurrentCommitHash()), Commit.class);
    }

    private static String getCurrentCommitHash() {
        String HEAD = Utils.readContentsAsString(Utils.join(Repository.CWD, ".gitlet", "HEAD"));
        String commitHash = "";
        if (HEAD.startsWith("ref:")) {
            String branchPath = HEAD.split(":")[1];
            commitHash = Utils.readContentsAsString(Utils.join(Repository.GITLET_DIR, branchPath));
        } else {
            commitHash = HEAD;
        }

        return commitHash;
    }

    private static String getCurrentBranch() {
        String HEAD = Utils.readContentsAsString(Utils.join(Repository.CWD, ".gitlet", "HEAD"));
        String branchPath = HEAD.split(":")[1];
        return Utils.getLastSegment(branchPath);
    }

    /**
     * Invariants: a merge commit has "second" key on its parentCommitsMap
     */
    public static void handleLog() {
        Commit commit = getCurrentCommit();
        StringBuilder sb = new StringBuilder();

        String pattern = Utils.getDateFormatPattern();
        DateFormat formatter = new SimpleDateFormat(pattern);
        while (commit != null) {

            sb.append("===").append("\n");
            String sha1 = Utils.sha1(commit.toString());
            sb.append("commit ").append(sha1).append("\n");

            if (commit.parentCommits != null && commit.parentCommits.containsKey("second")) {
                // case for merge commits
                sb.append("Merge: ")
                        .append(commit.parentCommits.get("first").substring(0, 4))
                        .append(commit.parentCommits.get("second").substring(0, 4))
                        .append("\n");
            }

            String date = formatter.format(commit.date);
            sb.append("Date: ").append(date).append("\n");
            sb.append(commit.message).append("\n\n");

            if (commit.parentCommits == null) {
                commit = null;
            } else {
                commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR,
                        commit.parentCommits.get("first")), Commit.class);
            }
        }

        System.out.println(sb);
    }

}

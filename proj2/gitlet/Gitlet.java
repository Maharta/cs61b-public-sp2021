package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


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

        if (Repository.stagingRemovalMap.containsKey(fileName)) {
            // file on removal
            Repository.stagingRemovalMap.remove(fileName);
            Repository.persistStagingAreaMap();
        } else if (Repository.stagingAreaMap.get(fileName) == null) {
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
        //modify currentCommit to newCommit, link newCommit parent to current sha1 hash
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
            Repository.stagingRemovalMap.put(fileName, "");
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
        String commitHash;
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
        while (commit != null) {
            printLogFromCommit(commit);
            if (commit.parentCommits == null) {
                commit = null;
            } else {
                commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR,
                        commit.parentCommits.get("first")), Commit.class);
            }
        }

    }

    public static void handleGlobalLog() {
        List<String> commitFileNames = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        assert commitFileNames != null;
        for (String s : commitFileNames) {
            Commit commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR, s), Commit.class);
            printLogFromCommit(commit);
        }
    }

    public static void handleFind(String commitMessage) {
        List<String> commitFileNames = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        assert commitFileNames != null;
        for (String s : commitFileNames) {
            Commit commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR, s), Commit.class);
            if (Objects.equals(commit.message, commitMessage)) {
                System.out.println(s);
            }
        }
    }


    private static void printLogFromCommit(Commit commit) {
        StringBuilder sb = new StringBuilder();

        String pattern = Utils.getDateFormatPattern();
        DateFormat formatter = new SimpleDateFormat(pattern);

        sb.append("===").append("\n");
        String sha1 = Utils.sha1(commit.toString());
        sb.append("commit ").append(sha1).append("\n");

        if (commit.parentCommits != null && commit.parentCommits.containsKey("second")) {
            // case for merge commits
            sb.append("Merge: ")
                    .append(commit.parentCommits.get("first"), 0, 4)
                    .append(commit.parentCommits.get("second"), 0, 4)
                    .append("\n");
        }

        String date = formatter.format(commit.date);
        sb.append("Date: ").append(date).append("\n");
        sb.append(commit.message).append("\n\n");

        System.out.print(sb);
    }


    public static void handleStatus() {
        // branches part
        System.out.println("=== Branches ===");
        List<String> branches = Utils.plainFilenamesIn(Repository.BRANCH_DIR);
        String currentBranch = getCurrentBranch();
        assert branches != null;
        for (String branch : branches) {
            if (Objects.equals(branch, currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        // Staged files
        System.out.println("=== Staged Files ===");
        TreeMap<String, String> sortedStagedAreaMap = new TreeMap<>(Repository.stagingAreaMap);
        for (String s : sortedStagedAreaMap.keySet()) {
            System.out.println(s);
        }
        System.out.println();
        // Removed files
        System.out.println("=== Removed Files ===");
        TreeMap<String, String> sortedStagedRemovalMap = new TreeMap<>(Repository.stagingRemovalMap);
        for (String s : sortedStagedRemovalMap.keySet()) {
            System.out.println(s);
        }
        System.out.println();

        List<String> fileNamesInDir = Utils.plainFilenamesIn(Repository.GITLET_DIR.getParentFile());

        // Modifications not staged for commit (hard one)
        System.out.println("=== Modifications Not Staged For Commit ===");
        TreeMap<String, String> modifiedButNotStagedMap = generateModifiedButNotStagedMap(fileNamesInDir);
        for (Map.Entry<String, String> modifiedEntry : modifiedButNotStagedMap.entrySet()) {
            System.out.println(modifiedEntry.getKey() + " " + modifiedEntry.getValue());
        }
        System.out.println();
        // Untracked files
        System.out.println("=== Untracked Files ===");
        TreeSet<String> untrackedFilesSet = generateUntrackedFilesMap(fileNamesInDir);
        for (String s : untrackedFilesSet) {
            System.out.println(s);
        }
        System.out.println();
    }

    /**
     * A file in the working directory is “modified but not staged” if it is
     * <ul>
     *  <li>Tracked in the current commit, changed in the working directory, but not staged; or
     *  <li>Staged for addition, but with different contents than in the working directory; or</li>
     *  <li>Staged for addition, but deleted in the working directory; or</li>
     *  <li>Not staged for removal, but tracked in the current commit and deleted from the working directory.</li>
     * </ul>
     */
    private static TreeMap<String, String> generateModifiedButNotStagedMap(List<String> fileNamesInDir) {
        TreeMap<String, String> modifiedMap = new TreeMap<>();

        assert fileNamesInDir != null;
        Set<String> fileNamesSet = new HashSet<>(fileNamesInDir);

        Commit currentCommit = getCurrentCommit();

        // files tracked in current commit
        currentCommit.fileBlobsha1Map.forEach(
                (key, value) -> {
                    // deleted from working directory
                    if (!fileNamesSet.contains(key)) {
                        // but not staged from removal
                        if (!Repository.stagingRemovalMap.containsKey(key)) {
                            modifiedMap.put(key, "(deleted1)");
                        }
                    } else if (!Repository.stagingAreaMap.containsKey(key)
                            && !Repository.stagingRemovalMap.containsKey(key)) {
                        String currentFileContents = Utils.readContentsAsString(new File(key));
                        String sha1 = Utils.sha1(currentFileContents);
                        // in working directory, tracked in commit but changed and not staged
                        if (!Objects.equals(value, sha1)) {
                            modifiedMap.put(key, "(modified1)");
                        }
                    }
                }
        );


        Repository.stagingAreaMap.forEach((key, value) -> {
            if (!fileNamesSet.contains(key)) {
                modifiedMap.put(key, "(deleted2)");
            } else {
                String currentFileContents = Utils.readContentsAsString(new File(key));
                String sha1 = Utils.sha1(currentFileContents);

                if (!Objects.equals(sha1, value)) {
                    modifiedMap.put(key, "(modified2)");
                }
            }
        });
        return modifiedMap;
    }

    private static TreeSet<String> generateUntrackedFilesMap(List<String> fileNamesInDir) {
        TreeSet<String> untrackedFilesSet = new TreeSet<>();
        Commit currCommit = getCurrentCommit();
        Map<String, String> fileTrackedMap = currCommit.fileBlobsha1Map;
        fileNamesInDir.forEach(fileName -> {
            if (fileTrackedMap.get(fileName) == null && Repository.stagingAreaMap.get(fileName) == null) {
                untrackedFilesSet.add(fileName);
            }
            if (Repository.stagingRemovalMap.containsKey(fileName)) {
                untrackedFilesSet.add(fileName);
            }
        });

        return untrackedFilesSet;
    }

    public static void handleCheckout(String[] args) {
        if (args.length == 2) {
            String branchName = args[1];
            String currentBranch = getCurrentBranch();

            if (Objects.equals(branchName, currentBranch)) {
                throw Utils.error("No need to checkout the current branch.");
            }

            Set<String> existingBranch = new HashSet<>(Objects.requireNonNull(Utils.plainFilenamesIn(Repository.BRANCH_DIR)));
            if (!existingBranch.contains(branchName)) {
                throw Utils.error("No such branch exists.");
            }

            List<String> files = Utils.plainFilenamesIn(Repository.GITLET_DIR.getParentFile());
            assert files != null;
            TreeSet<String> untrackedFiles = generateUntrackedFilesMap(files);

            if (!untrackedFiles.isEmpty()) {
                throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
            }

            // all validations done
            Commit branchHead = Utils.readObject(Utils.join(Repository.BRANCH_DIR, branchName), Commit.class);

            branchHead.fileBlobsha1Map.forEach(
                    (fileName, blob) -> {
                        String blobContents = Utils.readContentsAsString(Utils.join(Repository.BLOB_DIR, blob));
                        Utils.writeContents(Utils.join(Repository.CWD), fileName, blobContents);
                    }
            );
        }
        // checkout by filename only
        else if (args[1].equals("--")) {
            if (args.length != 3) {
                throw Utils.error("File name not specified.");
            }
            String fileName = args[2];
            Commit curr = getCurrentCommit();

            Map<String, String> commitFileMap = curr.fileBlobsha1Map;

            if (!commitFileMap.containsKey(fileName)) {
                throw Utils.error("File does not exist in that commit.");
            }

            String checkedFileContents = Utils.readContentsAsString(Utils.join(Repository.BLOB_DIR, commitFileMap.get(fileName)));
            Utils.writeContents(Utils.join(Repository.CWD, fileName), checkedFileContents);
        }
        // checkout by filename and commit id
        else {
            String thirdArg = args[2];
            if (!Objects.equals(thirdArg, "--")) {
                throw Utils.error("Invalid operands.");
            }
            String commitId = args[1];
            String fileName = args[3];

            List<String> commitFiles = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
            assert commitFiles != null;
            Set<String> commitFilesSet = new HashSet<>(commitFiles);

            // case if user inputted less than 40 sha 1 hash
            if (commitId.length() != 40) {
                for (String commitFile : commitFiles) {
                    if (Objects.equals(commitFile.substring(0, commitId.length()), commitId)) {
                        commitId = commitFile;
                        break;
                    }
                }
            }

            if (!commitFilesSet.contains(commitId)) {
                throw Utils.error("No commit with that id exists.");
            }

            Commit commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR, commitId), Commit.class);
            Map<String, String> commitFileMap = commit.fileBlobsha1Map;

            if (!commitFileMap.containsKey(fileName)) {
                throw Utils.error("File does not exist in that commit.");
            }

            String checkedFileContents = Utils.readContentsAsString(Utils.join(Repository.BLOB_DIR, commitFileMap.get(fileName)));
            Utils.writeContents(Utils.join(Repository.CWD, fileName), checkedFileContents);
        }
    }

    public static void handleBranch(String branchName) {
        boolean branchExist = isBranchExist(branchName);
        if (branchExist) {
            throw Utils.error("A branch with that name already exists.");
        }

        Commit headCommit = getCurrentCommit();
        String sha1 = Utils.sha1(headCommit.toString());

        // create new branch on file system
        Utils.writeContents(Utils.join(Repository.BRANCH_DIR, branchName), sha1);
    }

    public static void handleRmBranch(String branchName) {
        boolean branchExist = isBranchExist(branchName);
        if (!branchExist) {
            throw Utils.error("A branch with that name does not exist.");
        }
        String currentBranch = getCurrentBranch();
        if (Objects.equals(currentBranch, branchName)) {
            throw Utils.error("Cannot remove the current branch.");
        }

        File branchPtrFile = Utils.join(Repository.BRANCH_DIR, branchName);
        branchPtrFile.delete();
    }

    private static boolean isBranchExist(String branchName) {
        List<String> branchesFile = Utils.plainFilenamesIn(Repository.BRANCH_DIR);
        assert branchesFile != null;
        for (String branch : branchesFile) {
            if (Objects.equals(branch, branchName)) {
                return true;
            }
        }
        return false;
    }
}

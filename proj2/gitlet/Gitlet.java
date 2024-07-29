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

    /**
     * Get the name of the current branch you are currently in
     */
    private static String getCurrentBranch() {
        String HEAD = Utils.readContentsAsString(Utils.join(Repository.CWD, ".gitlet", "HEAD"));
        String branchPath = HEAD.split(":")[1];
        return Utils.getLastSegment(branchPath);
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

        boolean successfulFind = false;
        for (String s : commitFileNames) {
            Commit commit = Utils.readObject(Utils.join(Repository.COMMIT_DIR, s), Commit.class);
            if (Objects.equals(commit.message, commitMessage)) {
                System.out.println(s);
                successfulFind = true;
            }
        }
        if (!successfulFind) {
            throw Utils.error("Found no commit with that message.");
        }
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
        TreeSet<String> untrackedFilesSet = generateUntrackedFilesSet(fileNamesInDir);
        for (String s : untrackedFilesSet) {
            System.out.println(s);
        }
        System.out.println();
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

            List<String> files = Utils.plainFilenamesIn(Repository.CWD);
            assert files != null;
            TreeSet<String> untrackedFiles = generateUntrackedFilesSet(files);

            if (!untrackedFiles.isEmpty()) {
                throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
            }
            // all validations done

            // delete all files in the current branch
            for (String file : files) {
                new File(file).delete();
            }

            // clear stagingArea;
            Repository.stagingAreaMap.clear();
            Repository.stagingRemovalMap.clear();
            Repository.persistStagingAreaMap();
            Repository.removeFilesFromStagingArea();

            // write all files from the checked out branch
            String branchHeadSha1 = Utils.readContentsAsString(Utils.join(Repository.BRANCH_DIR, branchName));
            Commit branchHead = Utils.readObject(Utils.join(Repository.COMMIT_DIR, branchHeadSha1), Commit.class);

            branchHead.fileBlobsha1Map.forEach(
                    (fileName, blob) -> {
                        String blobContents = Utils.readContentsAsString(Utils.join(Repository.BLOB_DIR, blob));
                        Utils.writeContents(Utils.join(Repository.CWD, fileName), blobContents);
                    }
            );

            // update HEAD pointer.
            Utils.writeContents(Utils.join(Repository.GITLET_DIR, "HEAD"), "ref:refs/branches/" + branchName);
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
                throw Utils.error("Incorrect operands.");
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

    /**
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     * The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared.
     * The command is essentially checkout of an arbitrary commit that also changes the current branch head.
     */
    public static void handleReset(String commitId) {
        List<String> fileNames = Utils.plainFilenamesIn(Repository.CWD);
        assert fileNames != null;
        Set<String> untrackedFiles = generateUntrackedFilesSet(fileNames);

        if (!untrackedFiles.isEmpty()) {
            throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        List<String> commitIds = Utils.plainFilenamesIn(Repository.COMMIT_DIR);
        assert commitIds != null;
        Set<String> commitIdSet = new HashSet<>(commitIds);

        if (commitId.length() != 40) {
            for (String id : commitIds) {
                if (id.startsWith(commitId)) {
                    commitId = id;
                    break;
                }
            }
        }

        if (!commitIdSet.contains(commitId)) {
            throw Utils.error("No commit with that id exists.");
        }

        String currentBranch = getCurrentBranch();
        Utils.writeContents(Utils.join(Repository.BRANCH_DIR, currentBranch), commitId);

        Commit commitToReset = Utils.readObject(Utils.join(Repository.COMMIT_DIR, commitId), Commit.class);
        Map<String, String> commitFilesMap = commitToReset.fileBlobsha1Map;

        List<String> files = Utils.plainFilenamesIn(Repository.CWD);

        for (String file : files) {
            File fileToDelete = Utils.join(Repository.CWD, file);
            fileToDelete.delete();
        }

        commitFilesMap.forEach((key, value) -> {
            String contents = Utils.readContentsAsString(Utils.join(Repository.BLOB_DIR, value));
            Utils.writeContents(Utils.join(Repository.CWD, key), contents);
        });

        // clear staging area
        Repository.stagingAreaMap.clear();
        Repository.stagingRemovalMap.clear();
        Repository.persistStagingAreaMap();
        Repository.removeFilesFromStagingArea();
    }

    private static TreeSet<String> generateUntrackedFilesSet(List<String> fileNamesInDir) {
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

    public static void handleMerge(String branchName) {
        if (!Repository.stagingAreaMap.isEmpty() || !Repository.stagingRemovalMap.isEmpty()) {
            throw Utils.error("You have uncommited changes.");
        }
        if (!isBranchExist(branchName)) {
            throw Utils.error("A branch with that name does not exist.");
        }
        if (Objects.equals(branchName, getCurrentBranch())) {
            throw Utils.error("Cannot merge a branch with itself.");
        }

        List<String> fileNamesInDir = Utils.plainFilenamesIn(Repository.CWD);
        assert fileNamesInDir != null;

        if (!generateUntrackedFilesSet(fileNamesInDir).isEmpty()) {
            throw Utils.error("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        Commit splitPoint = getSplitPointWithOtherBranch(branchName);

        Commit thisBranch = getCurrentCommit();

        String otherSha1 = Utils.readContentsAsString(Utils.join(Repository.BRANCH_DIR, branchName));
        Commit otherBranch = Utils.readObject(
                Utils.join(Repository.COMMIT_DIR, otherSha1), Commit.class
        );

        if (Objects.equals(otherBranch, splitPoint)) {
            Utils.printThenExit("Given branch is an ancestor of the current branch.");
        }

        if (Objects.equals(thisBranch, splitPoint)) {
            handleCheckout(new String[]{"checkout", branchName});
            Utils.printThenExit("Current branch fast-forwarded.");
        }

        Map<String, String> fileToTrackMap = generateFileToTrackForMerging(splitPoint, thisBranch, otherBranch);
        modifyFileToTrackBasedOnMergingRules(fileToTrackMap, splitPoint, thisBranch, otherBranch);

        System.out.println(fileToTrackMap);
    }

    private static void modifyFileToTrackBasedOnMergingRules(Map<String, String> fileToTrackMap, Commit splitPoint, Commit thisBranch, Commit otherBranch) {
        fileToTrackMap.forEach((file, blobSha1) -> {
            String splitPointBlob = splitPoint.fileBlobsha1Map.get(file);
            String thisBranchBlob = thisBranch.fileBlobsha1Map.get(file);
            String otherBranchBlob = otherBranch.fileBlobsha1Map.get(file);

            // rule 1 && rule 6
            if (splitPointBlob != null && Objects.equals(splitPointBlob, thisBranchBlob) && !Objects.equals(thisBranchBlob, otherBranchBlob)) {
                // rule 1 - Modified in other but not HEAD -> Other
                if (otherBranchBlob != null) {
                    fileToTrackMap.put(file, otherBranchBlob);
                }
                // rule 6 - Head unmodified, but not present in other -> Remove
                else {
                    fileToTrackMap.put(file, "DELETE");
                }
            }
            // rule 2 && rule 7
            else if (splitPointBlob != null && Objects.equals(splitPointBlob, otherBranchBlob) && !Objects.equals(otherBranchBlob, thisBranchBlob)) {
                // rule 2 - Modified in HEAD but not other -> HEAD
                if (thisBranchBlob != null) {
                    fileToTrackMap.put(file, thisBranchBlob);
                }
                // rule 7 - Other unmodified, but not present in HEAD -> Remain absent
                else {
                    fileToTrackMap.remove(file);
                }
            }
            // rule 4 && rule 5
            else if (splitPointBlob == null) {
                // rule 4 - Not in split nor other, but in HEAD -> HEAD
                if (otherBranchBlob == null) {
                    fileToTrackMap.put(file, thisBranchBlob);
                }
                // rule 5 - Not in split nor head, but in other -> other
                else {
                    fileToTrackMap.put(file, otherBranchBlob);
                }
            }
            // rule 3 - Branching A & B
            else {
                // modified in both, but same way
                if (Objects.equals(thisBranchBlob, otherBranchBlob)) {
                    fileToTrackMap.remove(file);
                }
                // modified in both, but different way
                else {
                    fileToTrackMap.put(file, "CONFLICT");
                }
            }
        });
    }

    private static Map<String, String> generateFileToTrackForMerging(Commit splitPoint, Commit thisBranch, Commit otherBranch) {
        Map<String, String> filetoTrackMap = new HashMap<>();
        splitPoint.fileBlobsha1Map.forEach((key, value) -> filetoTrackMap.put(key, ""));
        thisBranch.fileBlobsha1Map.forEach((key, value) -> filetoTrackMap.put(key, ""));
        otherBranch.fileBlobsha1Map.forEach((key, value) -> filetoTrackMap.put(key, ""));
        return filetoTrackMap;
    }


    /**
     * A file in the working directory is "modified but not staged" if it is
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
                            modifiedMap.put(key, "(deleted)");
                        }
                    } else if (!Repository.stagingAreaMap.containsKey(key)
                            && !Repository.stagingRemovalMap.containsKey(key)) {
                        String currentFileContents = Utils.readContentsAsString(new File(key));
                        String sha1 = Utils.sha1(currentFileContents);
                        // in working directory, tracked in commit but changed and not staged
                        if (!Objects.equals(value, sha1)) {
                            modifiedMap.put(key, "(modified)");
                        }
                    }
                }
        );


        Repository.stagingAreaMap.forEach((key, value) -> {
            if (!fileNamesSet.contains(key)) {
                modifiedMap.put(key, "(deleted)");
            } else {
                String currentFileContents = Utils.readContentsAsString(new File(key));
                String sha1 = Utils.sha1(currentFileContents);

                if (!Objects.equals(sha1, value)) {
                    modifiedMap.put(key, "(modified)");
                }
            }
        });
        return modifiedMap;
    }

    private static Commit getSplitPointWithOtherBranch(String otherBranch) {
        Commit commit = getCurrentCommit();

        Set<Commit> currentBranchCommits = new HashSet<>();

        // fill currentBranch commit set history
        while (commit != null) {
            currentBranchCommits.add(commit);

            if (commit.parentCommits == null) {
                break;
            }

            String firstParentSha1 = commit.parentCommits.get("first");
            String secondParentSha1 = commit.parentCommits.get("second");

            // merge commit case
            if (firstParentSha1 != null && secondParentSha1 != null) {
                Commit firstParent = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, firstParentSha1), Commit.class);
                Commit secondParent = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, secondParentSha1), Commit.class);
                currentBranchCommits.add(firstParent);
                currentBranchCommits.add(secondParent);

                if (!firstParent.parentCommits.containsKey("first")) {
                    commit = null;
                    continue;
                }

                commit = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR
                                , firstParent.parentCommits.get("first")),
                        Commit.class);

            } else if (firstParentSha1 != null) {
                commit = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, firstParentSha1), Commit.class);
            } else {
                commit = null;
            }
        }

        String otherSha1 = Utils.readContentsAsString(
                Utils.join(Repository.BRANCH_DIR, otherBranch)
        );

        Commit other = Utils.readObject(Utils.join(Repository.COMMIT_DIR, otherSha1),
                Commit.class);

        // traverse other branch, if we get a commit that is on the other branch we gucci
        while (other != null && !currentBranchCommits.contains(other)) {
            String firstParentSha1 = other.parentCommits.get("first");
            String secondParentSha1 = other.parentCommits.get("second");

            if (other.parentCommits == null) {
                break;
            }

            if (firstParentSha1 != null && secondParentSha1 != null) {
                Commit firstParent = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, firstParentSha1), Commit.class);
                Commit secondParent = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, secondParentSha1), Commit.class);

                if (currentBranchCommits.contains(firstParent)) {
                    other = firstParent;
                    break;
                }

                if (currentBranchCommits.contains(secondParent)) {
                    other = secondParent;
                    break;
                }
            }

            if (firstParentSha1 != null) {
                other = Utils.readObject(
                        Utils.join(Repository.COMMIT_DIR, firstParentSha1), Commit.class);
            } else {
                other = null;
            }
        }

        return other;
    }

}

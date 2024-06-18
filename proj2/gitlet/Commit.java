package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */


    public String date;
    public String author;
    public List<String> parentCommits;

    /**
     * contains mapping between fileName and sha1 string of the blob
     */
    public Map<String, String> fileBlobsha1Map;
    public String message;

    public Commit(String date, String author, List<String> parentCommits, Map<String, String> fileBlobsha1Map, String message) {
        this.date = date;
        this.author = author;
        this.parentCommits = parentCommits;
        this.fileBlobsha1Map = fileBlobsha1Map;
        this.message = message;
    }

    public Commit(String date, Map<String, String> fileBlobsha1Map, List<String> parentCommits, String message) {
        this.date = date;
        this.fileBlobsha1Map = fileBlobsha1Map;
        this.parentCommits = parentCommits;
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(author);
        for (String parentCommit : parentCommits) {
            sb.append(parentCommit);
        }
        for (Map.Entry<String, String> stringStringEntry : fileBlobsha1Map.entrySet()) {
            sb.append(stringStringEntry.getKey()).append(stringStringEntry.getValue());
        }
        sb.append(message);

        return sb.toString();
    }
}

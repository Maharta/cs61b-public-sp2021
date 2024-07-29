package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * <p>
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */


    public Date date;
    public String author;
    public Map<String, String> parentCommits;

    /**
     * contains mapping between fileName and sha1 string of the blob
     */
    public Map<String, String> fileBlobsha1Map;
    public String message;

    public Commit(Date date, String author, Map<String, String> parentCommits, Map<String, String> fileBlobsha1Map, String message) {
        this.date = date;
        this.author = author;
        this.parentCommits = parentCommits;
        this.fileBlobsha1Map = fileBlobsha1Map;
        this.message = message;
    }

    public Commit(Date date, Map<String, String> fileBlobsha1Map, Map<String, String> parentCommits, String message) {
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
        sb.append(parentCommits);
        for (Map.Entry<String, String> stringStringEntry : fileBlobsha1Map.entrySet()) {
            sb.append(stringStringEntry.getKey()).append(stringStringEntry.getValue());
        }
        sb.append(message);

        return sb.toString();
    }

    @Override
    public void dump() {
        System.out.println(parentCommits);
        System.out.println(message);
        System.out.println(fileBlobsha1Map);
        System.out.println(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(date, commit.date)
                && Objects.equals(author, commit.author)
                && Objects.equals(parentCommits, commit.parentCommits)
                && Objects.equals(fileBlobsha1Map, commit.fileBlobsha1Map)
                && Objects.equals(message, commit.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, author, parentCommits, fileBlobsha1Map, message);
    }
}

package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
    public String parentCommit;

    /**
     * contains mapping between fileName and sha1 string of the blob
     */
    public Map<String, String> fileBlobsha1Map;
    public String message;

    public Commit(Date date, String author, String parentCommit, Map<String, String> fileBlobsha1Map, String message) {
        this.date = date;
        this.author = author;
        this.parentCommit = parentCommit;
        this.fileBlobsha1Map = fileBlobsha1Map;
        this.message = message;
    }

    public Commit(Date date, Map<String, String> fileBlobsha1Map, String parentCommit, String message) {
        this.date = date;
        this.fileBlobsha1Map = fileBlobsha1Map;
        this.parentCommit = parentCommit;
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(date);
        sb.append(author);
        sb.append(parentCommit);
        for (Map.Entry<String, String> stringStringEntry : fileBlobsha1Map.entrySet()) {
            sb.append(stringStringEntry.getKey()).append(stringStringEntry.getValue());
        }
        sb.append(message);

        return sb.toString();
    }

    @Override
    public void dump() {
        System.out.println(parentCommit);
        System.out.println(message);
        System.out.println(fileBlobsha1Map);
        System.out.println(date);
    }
}

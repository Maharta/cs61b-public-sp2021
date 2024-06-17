package gitlet;

import java.io.Serializable;

public class Pointer implements Serializable {
    public String pointsTo;
    public String type;

    public Pointer(String pointsTo, String type) {
        this.pointsTo = pointsTo;
        this.type = type;
    }
}

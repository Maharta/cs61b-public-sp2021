package deque;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer x, Integer y) {
        return x.compareTo(y);
    }
}

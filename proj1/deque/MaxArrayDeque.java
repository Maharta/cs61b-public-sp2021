package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> c;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.c = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        Iterator<T> dequeIterator = iterator();
        T maxValue = dequeIterator.next();
        while (dequeIterator.hasNext()) {
            T x = dequeIterator.next();
            int result = c.compare(maxValue, x);
            if (result < 0) {
                maxValue = x;
            }
        }
        return maxValue;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        Iterator<T> dequeIterator = iterator();
        T maxValue = dequeIterator.next();
        while (dequeIterator.hasNext()) {
            T x = dequeIterator.next();
            int result = c.compare(maxValue, x);
            if (result < 0) {
                maxValue = x;
            }
        }
        return maxValue;
    }

}

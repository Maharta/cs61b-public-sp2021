package deque;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


public class MaxArrayDequeTest {

    @Test
    public void testMaxMethodConstructorComparator() {
        MaxArrayDeque<Integer> maxArrayDeque = MaxArrayDeque.of(new IntegerComparator(), 2, 3, 10, 200, 5, 1, 1000, 213, 2);

        int max = maxArrayDeque.max();

        assertEquals(1000, max);
    }

    /**
     * Testing with custom comparator where the smallest integer become the biggest
     */
    @Test
    public void textMaxMethodCustomComparator() {
        MaxArrayDeque<Integer> maxArrayDeque = MaxArrayDeque.of(new IntegerComparator(), -1, 3, 10, 200, 5, 1, 1000, 213, 2);

        Comparator<Integer> customComparator = (o1, o2) -> o2 - o1;
        // max should be the smallest integer
        int max = maxArrayDeque.max(customComparator);

        assertEquals(max, -1);

    }
}

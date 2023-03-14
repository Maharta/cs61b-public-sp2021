package deque;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Before;
import org.junit.Test;

public class LinkedListDequeTimeTest {
    private AList<Integer> Ns;
    private AList<Double> times;
    private AList<Integer> opCounts;

    @Before
    public void initializeList() {
        Ns = new AList<>();
        times = new AList<>();
        opCounts = new AList<>();
    }

    /**
     * Add and remove operations should take constant time and doesn't grow
     * with the size of the list
     */
    @Test
    public void addAndRemoveTimeTest() {
        int size = 1000;
        for (int i = 0; i < 8; i++) {
            LinkedListDeque<Integer> deque = new LinkedListDeque<>();
            Ns.addLast(size);
            opCounts.addLast(size);
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < size; j++) {
                int randomNumber = StdRandom.uniform(0, 2);
                if (randomNumber == 0) {
                    deque.addLast(j);
                } else {
                    deque.addFirst(j);
                }
            }
            times.addLast(sw.elapsedTime());
            size *= 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }

    /**
     * get operation should take time proportional to the number of items in the list.
     */
    @Test
    public void testGetTime() {
        int size = 16000;
        for (int i = 0; i < 4; i++) {
            LinkedListDeque<Integer> deque = new LinkedListDeque<>();
            Ns.addLast(size);
            opCounts.addLast(8000);
            for (int j = 0; j < size; j++) {
                deque.addLast(j);
            }

            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < 8000; j++) {
                int randomNumber = StdRandom.uniform(0, size / 2);
                int number = deque.get(randomNumber);
            }
            times.addLast(sw.elapsedTime());
            size *= 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }
}

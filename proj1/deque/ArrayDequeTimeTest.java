package deque;

import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

public class ArrayDequeTimeTest {
    @Test
    /** The construction of the ArrayDeque should be mostly linear, except during resizing operations. */
    public void timeForConstructingTest() {
        int limit = 1000;
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            ArrayDeque<Integer> testDeque = new ArrayDeque<>();
            Ns.addLast(limit);
            opCounts.addLast(limit);
            int j = 0;
            Stopwatch sw = new Stopwatch();
            while (j < limit) {
                testDeque.addLast(j);
                j++;
            }
            times.addLast(sw.elapsedTime());
            limit = limit * 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }

    @Test
    /* Time to remove from the front should be linear regardless of size*/
    public void timeRemoveFirst() {
        int size = 1000;
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            ArrayDeque<Integer> testDeque = new ArrayDeque<>();
            Ns.addLast(size);
            opCounts.addLast(size);
            int j = 0;
            while (j < size) {
                testDeque.addLast(j);
                j++;
            }
            Stopwatch sw = new Stopwatch();
            while (j > 0) {
                testDeque.removeFirst();
                j--;
            }
            times.addLast(sw.elapsedTime());
            size = size * 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }

    @Test
    public void timeRemoveLast() {
        int size = 1000;
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            ArrayDeque<Integer> testDeque = new ArrayDeque<>();
            Ns.addLast(size);
            opCounts.addLast(size);
            int j = 0;
            while (j < size) {
                testDeque.addLast(j);
                j++;
            }
            Stopwatch sw = new Stopwatch();
            while (j > 0) {
                testDeque.removeLast();
                j--;
            }
            times.addLast(sw.elapsedTime());
            size = size * 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }

    /**
     * get operation must take constant time regardless of deque size
     */
    @Test
    public void timeGet() {
        int size = 1000;
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            ArrayDeque<Integer> testDeque = new ArrayDeque<>();
            Ns.addLast(size);
            opCounts.addLast(size);
            int j = 0;
            while (j < size) {
                testDeque.addLast(j);
                j++;
            }
            Stopwatch sw = new Stopwatch();
            testDeque.get(0);
            testDeque.get(testDeque.size() - 1);
            testDeque.get(testDeque.size() / 2);
            times.addLast(sw.elapsedTime());
            size = size * 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }


    /**
     * getting size  must take constant time regardless of deque size
     */
    @Test
    public void timeSize() {
        int size = 1000;
        AList<Integer> Ns = new AList<>();
        AList<Integer> opCounts = new AList<>();
        AList<Double> times = new AList<>();

        for (int i = 0; i < 8; i++) {
            ArrayDeque<Integer> testDeque = new ArrayDeque<>();
            Ns.addLast(size);
            opCounts.addLast(size);
            int j = 0;
            while (j < size) {
                testDeque.addLast(j);
                j++;
            }
            Stopwatch sw = new Stopwatch();
            testDeque.size();
            times.addLast(sw.elapsedTime());
            size = size * 2;
        }
        TimeList.printTimingTable(Ns, times, opCounts);
    }
}

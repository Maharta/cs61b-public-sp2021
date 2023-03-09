package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testAddAndRemove() {
        AListNoResizing<Integer> simpleList = new AListNoResizing<>();
        BuggyAList<Integer> buggyList = new BuggyAList<>();
        for (int i = 0; i < 3; i++) {
            simpleList.addLast(i);
            buggyList.addLast(i);
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(simpleList.get(i), buggyList.get(i));
        }
        for (int i = 0; i < 2; i++) {
            simpleList.removeLast();
            buggyList.removeLast();
            assertEquals(simpleList.getLast(), buggyList.getLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyList.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int buggyListSize = buggyList.size();
                assertEquals(size, buggyListSize);
            } else if (operationNumber == 2 && L.size() > 0) {
                // getLast
                int lastItem = L.getLast();
                int lastInBuggy = buggyList.getLast();
                assertEquals(lastItem, lastInBuggy);
            } else if (operationNumber == 3 && L.size() > 0) {
                // remove last
                int removedItem = L.removeLast();
                int removedInBuggy = buggyList.removeLast();
                assertEquals(removedItem, removedInBuggy);
            }
        }
    }
}

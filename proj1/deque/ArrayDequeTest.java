package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    /**
     * Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     * && is the "and" operation.
     */
    @Test
    public void addIsEmptySizeTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());
        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("lld1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }

    /**
     * Adds an item, then removes an item, and ensures that dll is empty afterwards.
     */
    @Test
    public void addRemoveTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertEquals("ad1 should contain 1 item", 1, ad1.size());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    /**
     * Test removing from an empty deck
     */
    @Test
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    /**
     * Check if you can create ArrayDeques with different parameterized types
     */
    @Test
    public void multipleParamTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<Double> ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();
    }

    /**
     * check if null is return when removing from an empty LinkedListDeque.
     */
    @Test
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertNull("Should return null when removeFirst is called on an empty Deque,", ad1.removeFirst());
        assertNull("Should return null when removeLast is called on an empty Deque,", ad1.removeLast());

    }

    /**
     * Add large number of elements to deque; check if order is correct.
     */
    @Test
    public void bigArrayDequeTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }

    }

    /**
     * The amount of memory that the deque.items use should be proportional
     * to the actual size of the deque for items.length of size 16 or bigger.
     */
    @Test
    public void proportionalTest() {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        int dequeSize = 10000;
        for (int i = 0; i < dequeSize; i++) {
            deque.addLast(i);
        }
        for (int i = 0; i < dequeSize - 55; i++) {
            deque.removeFirst();
        }
        double percentageUsed = (double) deque.size() / deque.getTotalArrayLength();
        assertTrue("Should at least use 25% of the total items length for array of size 16 or bigger",
                percentageUsed >= 0.25);
    }

    /**
     * Test deep equal method with both Deques implementation.
     */
    @Test
    public void deepEqualTest() {

        ArrayDeque<String> deque1 = ArrayDeque.of("a", "ab", "abc");
        ArrayDeque<String> deque2 = ArrayDeque.of("a", "ab", "abc");

        LinkedListDeque<String> llDeque = LinkedListDeque.of("a", "ab", "abc");

        assertEquals(deque1, deque2);
        assertEquals(deque1, llDeque);
    }

    @Test
    public void emptyEqualTest() {
        ArrayDeque<Integer> deque1 = new ArrayDeque<>();
        ArrayDeque<Integer> deque2 = new ArrayDeque<>();

        LinkedListDeque<Integer> llDeque1 = new LinkedListDeque<>();

        assertEquals(deque1, deque2);
        assertEquals(deque1, llDeque1);
    }
}




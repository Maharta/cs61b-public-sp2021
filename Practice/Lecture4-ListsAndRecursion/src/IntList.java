public class IntList {
    public int first;
    public IntList next;

    public IntList(int first, IntList next) {
        this.first = first;
        this.next = next;
    }

    /**
     * Returns an IntList identical to L, but with
     * each element incremented by x. L is not allowed
     * to change.
     */
    public static IntList incrList(IntList L, int x) {
        /* Your code here. */
        if (L == null) {
            return null;
        }
        IntList Q = new IntList(L.first + x, null);
        Q.next = incrList(L.next, x);
        return Q;
    }

    /**
     * Returns an IntList identical to L, but with
     * each element incremented by x. Not allowed to use
     * the 'new' keyword.
     */
    public static IntList dincrList(IntList L, int x) {
        /* Your code here. */
        if (L == null) {
            return null;
        }
        L.first = L.first + x;
        dincrList(L.next, x);
        return L;
    }

    public static void main(String[] args) {
        IntList L = new IntList(5, null);
        L.next = new IntList(7, null);
        L.next.next = new IntList(9, null);

        System.out.println(L.size());
        System.out.println(L.iterativeSize());
        System.out.println(L);

        // Test your answers by uncommenting. Or copy and paste the
        // code for incrList and dincrList into IntList.java and
        // run it in the visualizer.
        // System.out.println(L.get(1));
        // IntList returned = incrList(L, 3);
//        System.out.println(returned);
    }

    /**
     * Return the size of the list using recursion
     */
    public int size() {
        if (this.next == null) {
            return 1;
        }
        return 1 + this.next.size();
    }

    /**
     * Return the size of the list using iteration
     */
    public int iterativeSize() {
        IntList p = this;
        int totalSize = 1;
        while (p.next != null) {
            p = p.next;
            totalSize++;
        }
        return totalSize;
    }

    /**
     * Return the ith item in this list
     */
    public int get(int idx) {
        if (idx == 0) {
            return this.first;
        }
        return this.next.get(idx - 1);
    }
}

public class IntList {
    public int first;
    public IntList rest;

    public IntList(int first, IntList rest) {
        this.first = first;
        this.rest = rest;
    }

    public static void main(String[] args) {
        IntList L = new IntList(5, null);
        L = IntList.addFirst(5, L);
        System.out.println(L.first);
        L.addAdjacent();
        System.out.println(L.first);
    }

    /**
     * We want to add a method to IntList so that if 2 numbers in a row are the same, we add them together and
     * make one large node. For example:
     * 1 → 1 → 2 → 3 becomes 2 → 2 → 3 which becomes 4 → 3
     */
    public void addAdjacent() {
        addAdjacent(this);
    }

    private static void addAdjacent(IntList L) {
        if (L == null || L.rest == null) {
            return;
        }
        if (L.first == L.rest.first) {
            L.first = L.first * 2;
            L.rest = L.rest.rest;
            addAdjacent(L);
        } else {
            addAdjacent(L.rest);
        }
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
        Q.rest = incrList(L.rest, x);
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
        dincrList(L.rest, x);
        return L;
    }

    public static IntList addFirst(int x, IntList L) {
        L = new IntList(x, L);
        return L;
    }


    /**
     * Return the size of the list using recursion
     */
    public int size() {
        if (this.rest == null) {
            return 1;
        }
        return 1 + this.rest.size();
    }

    /**
     * Return the size of the list using iteration
     */
    public int iterativeSize() {
        IntList p = this;
        int totalSize = 1;
        while (p.rest != null) {
            p = p.rest;
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
        return this.rest.get(idx - 1);
    }
}

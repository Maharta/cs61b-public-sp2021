public class IntList {
    public int first;
    public IntList rest;

    public IntList(int first, IntList rest) {
        this.first = first;
        this.rest = rest;
    }

    public static void main(String[] args) {
        IntList L = new IntList(2, null);

        System.out.println(L);

        L.addSquared(5);
        System.out.println(L);
    }

    public void addLast(int x) {
        IntList p = this;
        while (p.rest != null) {
            p = p.rest;
        }
        p.rest = new IntList(x, null);
    }

    public String toString() {
        if (this.rest == null) {
            return first + "-->" + " null";
        }
        return first + "-->" + rest;
    }

    /**
     * We want to add a method to IntList so that if 2 numbers in a row are the same, we add them together and
     * make one large node. For example:
     * 1 → 1 → 2 → 3 becomes 2 → 2 → 3 which becomes 4 → 3
     */
    public void addAdjacent() {
        addAdjacent(this);
    }

    private void addAdjacent(IntList L) {
        if (L.rest == null) {
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


    public void addFirst(int x) {
        IntList oldList = new IntList(this.first, this.rest);
        IntList newList = new IntList(x, oldList);
        this.first = newList.first;
        this.rest = newList.rest;
    }

    /**
     * Modify the Intlist class so that every time you add a value you “square” the IntList. For example, upon the insertion of 5,
     * the below IntList would transform from:
     * 1 => 2 to 1 => 1 => 2 => 4 => 5
     * and if 7 was added to the latter IntList, it would become
     * 1 => 1 => 1 => 1 => 2 => 4 => 4 => 16 => 5 => 25 => 7
     * Additionally, you are provided the constraint that you can only access the size() function one time during the entire process of adding a node.
     */
    public void addSquared(int x) {
        IntList squared = addSquared(x, this);
        this.first = squared.first;
        this.rest = squared.rest;
    }

    private IntList addSquared(int x, IntList L) {
        if (L.rest == null) {
            return new IntList(L.first, new IntList(L.first * 2, new IntList(x, null)));
        }

        return new IntList(L.first, new IntList(L.first * 2, addSquared(x, L.rest)));
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

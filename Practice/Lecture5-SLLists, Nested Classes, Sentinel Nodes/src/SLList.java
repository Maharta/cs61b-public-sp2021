/**
 * Invariants An invariant is a fact about a data structure that is guaranteed to be true (assuming there are no bugs in your code). This gives us a convenient checklist every time we add a feature to our data structure. Users are also guaranteed certain properties that they trust will be maintained. For example, an SLList with a sentinel node has at least the following invariants:
 * <p>
 * The sentinel reference always points to a sentinel node.
 * The front item (if it exists), is always at sentinel.next.item.
 * The size variable is always the total number of items that have been added.
 */
public class SLList {
    private static class IntNode {
        private final int value;
        private IntNode next;

        public IntNode(int i, IntNode next) {
            this.value = i;
            this.next = next;
        }
    }

    private final IntNode sentinel;
    private int size;

    public SLList() {
        sentinel = new IntNode(-1, null);
        size = 0;
    }

    public SLList(int x) {
        sentinel = new IntNode(-1, null);
        sentinel.next = new IntNode(x, null);
        size = 1;
    }

    public SLList(int[] nodeValues) {
        sentinel = new IntNode(-1, null);
        size = 0;
        for (int value : nodeValues) {
            addLast(value);
        }
    }


    /**
     * Return the first node value of the SLList
     */
    public int getFirstValue() {
        return sentinel.next.value;
    }

    public IntNode getFirstNode() {
        return sentinel.next;
    }

    public int size() {
        return size;
    }

    /**
     * Add x to the front of the list
     */
    public void addFirst(int x) {
        sentinel.next = new IntNode(x, sentinel.next);
        size++;
    }

    public void deleteFirst() {
        sentinel.next = getFirstNode().next;
        size--;
    }

    /**
     * Add x to the back of the list
     */
    public void addLast(int x) {
        size++;
        IntNode pointer = sentinel;
        while (pointer.next != null) {
            pointer = pointer.next;
        }
        pointer.next = new IntNode(x, null);
    }

    private static void addLastRecursive(int x, IntNode p) {
        if (p.next == null) {
            p.next = new IntNode(x, null);
            return;
        }
        addLastRecursive(x, p.next);
    }

    public void addLastRecursive(int x) {
        size++;
        addLastRecursive(x, sentinel);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        IntNode pointer = sentinel.next;
        while (pointer != null) {
            builder.append(pointer.value);
            builder.append(" --> ");
            pointer = pointer.next;
        }
        builder.append("null");
        return builder.toString();
    }

    public static void main(String[] args) {
        SLList list = new SLList(new int[]{7, 9, 8});
        System.out.println(list.size());
    }


}

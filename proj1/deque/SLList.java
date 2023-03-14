package deque;

public class SLList<T> {
    private class Node {
        T value;
        Node next;

        public Node(T value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    private final Node sentinel;
    private int size;

    public SLList() {
        size = 0;
        sentinel = new Node(null, null);
    }

    public void addFirst(T value) {
        sentinel.next = new Node(value, sentinel.next);
        size++;
    }

    public T getFirstValue() {
        return sentinel.next.value;
    }

    public void addLast(T value) {
        addLast(value, sentinel);
        size++;
    }

    private void addLast(T value, Node node) {
        if (node.next == null) {
            node.next = new Node(value, null);
            return;
        }
        addLast(value, node.next);
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        Node ptr = sentinel.next;
        StringBuilder sb = new StringBuilder();
        while (ptr != null) {
            sb.append(ptr.value);
            sb.append(" --> ");
            ptr = ptr.next;
        }
        sb.append("null");
        return sb.toString();
    }


    public static void main(String[] args) {
        SLList<Integer> list = new SLList<>();
        list.addFirst(10);
        System.out.println(list.size());
        list.addLast(20);
        System.out.println(list.getFirstValue());
        System.out.println(list);
    }

}

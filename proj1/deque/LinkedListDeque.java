package deque;

import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T> {

    private final Node sentinel;
    private int size;


    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public static <T> LinkedListDeque<T> of(T... args) {
        LinkedListDeque<T> deque = new LinkedListDeque<>();
        for (T x : args) {
            deque.addLast(x);
        }
        return deque;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    public void addFirst(T x) {
        Node newNode = new Node(sentinel, x, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;
    }

    public void addLast(T x) {
        Node newNode = new Node(sentinel.prev, x, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    public T removeFirst() {
        Node currentFirst = sentinel.next;
        sentinel.next = currentFirst.next;
        sentinel.next.prev = sentinel;
        if (size > 0) {
            size--;
        }
        return currentFirst.value;
    }

    public T removeLast() {
        Node currentLast = sentinel.prev;
        sentinel.prev = currentLast.prev;
        sentinel.prev.next = sentinel;
        if (size > 0) {
            size--;
        }
        return currentLast.value;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("index is out of bounds.");
        }
        Node ptr;
        if (index < size / 2) {
            ptr = sentinel.next;
            while (index != 0) {
                ptr = ptr.next;
                index--;
            }
        } else {
            ptr = sentinel.prev;
            while ((size - 1 - index) != 0) {
                ptr = ptr.prev;
                index++;
            }
        }
        return ptr.value;
    }

    /**
     * Get the value at index with recursion.
     */
    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("index is out of bounds.");
        }
        if (index < size / 2) {
            return getRecursive(index, sentinel.next, "beginning");
        } else {
            return getRecursive(size - 1 - index, sentinel.prev, "end");
        }
    }

    /**
     * Get the value at index with recursion.
     * <p>
     * Will get passed either the first node or the last node for the second argument.
     * This depends on whether index is closer to the start or the end of the list.
     * <p>
     * Will also get passed "beginning" or "end" String for the last argument.
     * This will help the helper function whether to traverse to the front or to the back.
     * <p>
     * index will determine how many times is traversing needed for the node pointer.
     */
    private T getRecursive(int index, Node node, String startFrom) {
        if (index == 0) {
            return node.value;
        }
        if (Objects.equals(startFrom, "beginning")) {
            return getRecursive(index - 1, node.next, startFrom);
        } else {
            return getRecursive(index - 1, node.prev, startFrom);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deque<?>)) {
            return false;
        }

        if (((Deque<?>) o).size() != this.size) {
            return false;
        }

        // if we are here, both size is the same, so checking one is sufficient;
        if (isEmpty()) {
            return true;
        }

        Iterator<T> thisIterator = iterator();
        Iterator<?> otherDequeIterator = ((Deque<?>) o).iterator();

        while (thisIterator.hasNext()) {
            if (!thisIterator.next().equals(otherDequeIterator.next())) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node ptr = sentinel.next;
        while (ptr != sentinel) {
            System.out.print(ptr.value + " ");
            ptr = ptr.next;
        }
        System.out.print('\n');
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        int index;
        Node ptr;

        LinkedListDequeIterator() {
            index = 0;
            ptr = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T value = ptr.value;
            ptr = ptr.next;
            index++;
            return value;
        }
    }

    private class Node {
        T value;
        Node next;
        Node prev;

        Node(Node prev, T value, Node next) {
            this.prev = prev;
            this.value = value;
            this.next = next;
        }

    }

}

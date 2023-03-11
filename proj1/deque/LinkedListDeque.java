package deque;

import java.util.Objects;

public class LinkedListDeque<T> {

    private class Node {
        T value;
        Node next;
        Node prev;

        public Node(Node prev, T value, Node next) {
            this.prev = prev;
            this.value = value;
            this.next = next;
        }

    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<?> other = (LinkedListDeque<?>) o;
        if (other.size != this.size) {
            return false;
        }
        Node thisPtr = sentinel.next;
        Node otherPtr = (Node) other.sentinel.next;
        while (thisPtr != sentinel) {
            if (thisPtr.value != otherPtr.value) {
                return false;
            }
            thisPtr = thisPtr.next;
            otherPtr = otherPtr.next;
        }
        return true;
    }

    public boolean isEmpty() {
        return size == 0;
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

    public static void main(String[] args) {
        LinkedListDeque<Integer> list = new LinkedListDeque<>();
        System.out.println(list.isEmpty());
        list.addLast(10);
        list.addFirst(5);
        list.addLast(99);
        list.addFirst(7);
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(3));
        System.out.println(list.getRecursive(0));
        System.out.println(list.getRecursive(1));
        System.out.println(list.getRecursive(2));
        System.out.println(list.getRecursive(3));
        System.out.println(list.isEmpty());
        list.removeLast();
        list.removeFirst();
        list.printDeque();

        LinkedListDeque<String> otherList = new LinkedListDeque<>();
        otherList.addFirst("blabal");
        otherList.addFirst("blabal");
        System.out.println(list.equals(otherList));

        LinkedListDeque<Integer> anotherList = new LinkedListDeque<>();
        anotherList.addFirst(10);
        anotherList.addFirst(5);
        System.out.println(anotherList.equals(list));
    }

}

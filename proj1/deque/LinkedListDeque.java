package deque;

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
    Node sentinel;

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
            throw new IndexOutOfBoundsException("index out of bounds.");
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

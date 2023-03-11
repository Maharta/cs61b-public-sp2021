package deque;

public class ArrayDeque<T> {
    private int nextFirst;
    private int nextLast;
    private T[] items;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = 4;
        nextLast = 5;
        size = 0;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size++;
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        int firstIndex = (nextFirst + 1) % items.length;
        for (int i = 0; i < size; i++) {
            newArray[i] = items[firstIndex];
            if (firstIndex + 1 == items.length) {
                firstIndex = 0;
            } else {
                firstIndex++;
            }
        }
        items = newArray;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    public T get(int index) {
        int currentFirst = nextFirst + 1 % items.length;
        int indexToGet = (currentFirst + index) % items.length;
        return items[indexToGet];
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int currentLast = (nextLast - 1 + items.length) % items.length;
        T removed = items[currentLast];
        items[currentLast] = null;

        nextLast = currentLast;
        size--;

        if (size < items.length / 4 && items.length >= 16) {
            resize(items.length / 2);
        }
        return removed;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int currentFirst = (nextFirst + 1) % items.length;
        T removed = items[currentFirst];
        items[currentFirst] = null;

        nextFirst = currentFirst;
        size--;

        if (size < items.length / 4 && items.length >= 16) {
            resize(items.length / 2);
        }
        return removed;
    }


    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void printDeque() {
        if (isEmpty()) {
            System.out.println("ArrayDeque is empty");
        }
        int currentFirst = (nextFirst + 1) % items.length;
        for (int i = 0; i < size; i++) {
            System.out.print(items[currentFirst] + " ");
            currentFirst = (currentFirst + 1) % items.length;
        }
        System.out.print("\n");
    }

    public static void main(String[] args) {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        deque.addFirst(10);
        deque.addLast(5);
        deque.addLast(6);
        deque.addLast(4);
        deque.addFirst(9);
        deque.addLast(20);
        deque.addFirst(2);
        deque.addLast(100);
        deque.addLast(-1);
        deque.printDeque();
        System.out.println(deque.get(5));
        System.out.println(deque.get(0));
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
        deque.removeLast();
    }
}

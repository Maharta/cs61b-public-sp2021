package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        if (((Deque<?>) o).size() != size) {
            return false;
        }

        if (o instanceof ArrayDeque) {
            ArrayDeque<?> otherDeque = (ArrayDeque<?>) o;
            int otherDeqIdx = (otherDeque.nextFirst + 1) % otherDeque.items.length;
            for (T x : this) {
                if (x == null && otherDeque.get(otherDeqIdx) == null) {
                    continue;
                }
                if (x == null || otherDeque.get(otherDeqIdx) == null) {
                    return false;
                }
                if (x.equals(otherDeque.get(otherDeqIdx))) {
                    return false;
                }
                otherDeqIdx = (otherDeqIdx + 1) % otherDeque.items.length;
            }
        }

        if (o instanceof LinkedListDeque) {
            LinkedListDeque<?> otherDeque = (LinkedListDeque<?>) o;
            int thisDequeIdx = (nextFirst + 1) % items.length;
            for (Object x : otherDeque) {
                if (x == null && items[thisDequeIdx] == null) {
                    continue;
                }
                if (x == null || items[thisDequeIdx] == null) {
                    return false;
                }
                if (!x.equals(items[thisDequeIdx])) {
                    return false;
                }
                thisDequeIdx = (thisDequeIdx + 1) % items.length;
            }
        }
        return true;
    }

    /**
     * Used for testing, do not use.
     */
    public int getTotalArrayLength() {
        return items.length;
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

    public static <K> ArrayDeque<K> of(K... items) {
        ArrayDeque<K> deque = new ArrayDeque<>();
        for (K x : items) {
            deque.addLast(x);
        }
        return deque;
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

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int i = 0;
        int idx = (nextFirst + 1) % items.length;

        @Override
        public boolean hasNext() {
            return i < size;
        }

        @Override
        public T next() {
            T value = items[idx];
            idx = (idx + 1) % items.length;
            i++;
            return value;
        }
    }


}

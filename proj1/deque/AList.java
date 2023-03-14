package deque;

public class AList<T> {
    private int size;
    private T[] items;

    public AList() {
        items = (T[]) new Object[10];
        size = 0;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[size] = item;
        size++;
    }

    public T getLast() {
        return items[size - 1];
    }

    public T get(int idx) {
        if (idx > size - 1) {
            throw new ArrayIndexOutOfBoundsException("index provided is bigger than array size");
        }
        return items[idx];
    }

    public void removeLast() {
        if (size == 0) {
            throw new ArrayIndexOutOfBoundsException("list is already empty");
        }
        items[size] = null;
        size--;
        if (size < items.length / 2 && items.length >= 16) {
            resize(items.length / 2);
        }
    }

    public int size() {
        return size;
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        System.arraycopy(items, 0, newArray, 0, Math.min(capacity, items.length));
        items = newArray;
    }

    public static void main(String[] args) {
        AList<Integer> list = new AList<>();
        list.addLast(10);
        list.addLast(20);
        System.out.println(list.getLast());
        System.out.println(list.size());
        System.out.println(list.get(0));
        list.removeLast();
        list.removeLast();
    }

}

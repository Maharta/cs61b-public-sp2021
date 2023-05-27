/**
 * Invariants:
 * addLast: the next item to be added, will always go on index size.
 * getLast: the item we want to return, will be at position size - 1;
 * size: the number of item in the list should be size;
 * full: if size is the same as items.length, then the array is full and we should "resize" the array
 */
public class AList<T> {
    private int size;
    T[] items;

    public static void main(String[] args) {
        AList<Integer> list = new AList<>();
        list.addLast(10);
        System.out.println(list.size);
        list.addLast(100);
        System.out.println(list.size);
        Integer x = list.removeLast();
        System.out.println(x);
        System.out.println(list.size);
        list.addFirst(5);
        System.out.println(list.get(0));
        System.out.println(list.size);

    }

    public AList() {
        items = (T[]) new Object[10];
        size = 0;
    }

    public int size() {
        return size;
    }

    public T get(int i) {
        return items[i];
    }

    private void resizeArray(int length) {
        T[] newArr = (T[]) new Object[length];
        System.arraycopy(items, 0, newArr, 0, Math.min(length, items.length));
        items = newArr;
    }

    public void addLast(T x) {
        if (size == items.length) {
            resizeArray(items.length * 2);
        }
        items[size] = x;
        size++;
    }

    public void addFirst(T x) {
        if (size == items.length) {
            resizeArray(items.length * 2);
        }
        shiftToRight(items[0], 1);
        items[0] = x;
        size++;
    }

    private void shiftToRight(T prevValue, int nextIndex) {
        if (nextIndex == items.length) {
            resizeArray(items.length * 2);
        }
        if (nextIndex == size) {
            items[nextIndex] = prevValue;
            return;
        }
        T tempValue = items[nextIndex];
        items[nextIndex] = prevValue;
        shiftToRight(tempValue, nextIndex + 1);
    }

    public T getLast() {
        return items[size - 1];
    }

    /**
     * Deletes the last item from the list and then returns it
     */
    public T removeLast() {
        T x = getLast();
        items[size - 1] = null;
        size--;
        if (getUsageRatio() < 0.25 && items.length >= 16) {
            resizeArray(items.length / 2);
        }
        return x;
    }

    private double getUsageRatio() {
        return (double) size / items.length;
    }
}

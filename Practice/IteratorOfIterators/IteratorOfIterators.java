import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorOfIterators implements Iterator<Integer> {
    List<Iterator<Integer>> l;
    int iteratorIdx;

    public IteratorOfIterators(List<Iterator<Integer>> l) {
        this.l = l;
        iteratorIdx = 0;
    }

    @Override
    public boolean hasNext() {
        for (Iterator<Integer> integerIterator : l) {
            if(integerIterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    private void moveIteratorIdx() {
        iteratorIdx = (iteratorIdx + 1) % l.size();
    }

    @Override
    public Integer next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No more items in the iterators");
        }
        while(!l.get(iteratorIdx).hasNext()) {
            moveIteratorIdx();
        }
        Iterator<Integer> iterator = l.get(iteratorIdx);
        Integer item = iterator.next();
        moveIteratorIdx();
        return item;
    }


    public static void main(String[] args) {
        List<Integer> n = List.of(1, 3, 5);
        List<Integer> o = List.of(2, 4, 6, 8);
        List<Integer> blabla = List.of(-99, -2, -1, -5, -32, -3219, -222);
        List<Iterator<Integer>> iterators = List.of(n.iterator(), o.iterator(), blabla.iterator());
        IteratorOfIterators iterator = new IteratorOfIterators(iterators);

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class IteratorOfIterators implements Iterator<Integer> {
    LinkedList<Iterator<Integer>> l = new LinkedList<>();

    public IteratorOfIterators(List<Iterator<Integer>> l) {
        for (Iterator<Integer> iterator : l) {
            if(iterator.hasNext()) {
                this.l.addLast(iterator);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !l.isEmpty();
    }


    @Override
    public Integer next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        Iterator<Integer> currIterator = l.removeFirst();
        Integer currItem = currIterator.next();
        if(currIterator.hasNext()) {
            l.addLast(currIterator);
        }
        return currItem;
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

package Sort;
import org.junit.Assert;
import org.junit.Test;


public class TestSort {

    @Test
    public void testSwap() {
        String[] inputs = {"i", "have", "an", "egg"};
        String[] expected =  {"an", "have", "i", "egg"};
        Sort.swap(inputs, 0, 2);
        Assert.assertArrayEquals(expected, inputs);
    }

    @Test
    public void testSort() {
        String[] inputs = {"i", "have", "an", "egg"};
        String[] expected =  {"an", "egg", "have", "i"};
        Sort.sort(inputs);
        Assert.assertArrayEquals(expected, inputs);
    }
    @Test
    public void testFindSmallest() {
        String[] inputs = {"i", "have", "an", "egg"};
        int expected = 2;
        int actualVal = Sort.findSmallest(inputs, 0);
        Assert.assertEquals(expected, actualVal);
    }
}

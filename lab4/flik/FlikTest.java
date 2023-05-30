package flik;

import static org.junit.Assert.*;
import org.junit.Test;

public class FlikTest {

    @Test
    public void testPrimitiveSameNumber() {
        int a = 128;
        int b = 128;

        assertTrue(Flik.isSameNumber(a, b));
    }

    @Test
    public void testIntegerReferenceSameNumber() {
        Integer a = 128;
        Integer b = 128;

        assertTrue(Flik.isSameNumber(a, b));
    }

}

package game2048;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestIsTileSameValue {
    static Board board;

    @Test
    /** Should return true if tile and nextTile is null */
    public void testEmptyBoard() {
        int[][] rawVals = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        board = new Board(rawVals, 0);
        Tile a = board.tile(0, 0);
        Tile b = board.tile(1, 0);
        assertTrue("NULL and NULL TILE are the same", Model.isTileSameValue(a, b));
    }

    @Test
    /** Should return true for non-null tiles with the same value. */
    public void testSameValueNonNull() {
        int[][] rawVals = new int[][]{
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 4, 4, 0},
        };

        board = new Board(rawVals, 8);
        Tile a = board.tile(0, 0);
        Tile b = board.tile(0, 3);
        assertTrue("Both these tile are the same " + a + " and " + b, Model.isTileSameValue(a, b));
        Tile c = board.tile(1, 0);
        Tile d = board.tile(2, 0);
        assertTrue("Both these tile are also the same " + c + " and " + d, Model.isTileSameValue(a, b));
    }

    @Test
    /** Should return false for non-null with tiles with value. */
    public void testNullAndNonNull() {
        int[][] rawVals = new int[][]{
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 4, 4, 0},
        };

        board = new Board(rawVals, 8);
        Tile a = board.tile(0, 0);
        Tile b = board.tile(0, 2);
        assertFalse("Return false for non null and null tile " + a + " and " + b, Model.isTileSameValue(a, b));
    }

    @Test
    /** Should return false for non-null tiles with different values */
    public void testDifferentValuedTiles() {
        int[][] rawVals = new int[][]{
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {2, 4, 4, 0},
        };

        board = new Board(rawVals, 8);
        Tile a = board.tile(0, 0);
        Tile b = board.tile(1, 0);
        assertFalse("Return false for different valued tile " + a + " and " + b, Model.isTileSameValue(a, b));
    }
}


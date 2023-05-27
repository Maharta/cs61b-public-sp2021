/*
 * After calling fillGrid(LL, UR, S), S should contain
{
{ 0, 11, 12, 13, 14 },
{ 1, 0, 15, 16, 17 },
{ 2, 3, 0, 18, 19 },
{ 4, 5, 6, 0, 20 },
{ 7, 8, 9, 10, 0 }
}
(The last two elements of LL are excess and therefore ignored.)
* */
public class FillGrid {
    public static void main(String[] args) {
        int[] LL = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0};
        int[] UR = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        int[][] S = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };
        fillGrid(LL, UR, S);
    }

    /*
    Fill the lower-left triangle of S with elements of LL and the
    upper-right triangle of S with elements of UR (from left-to
    right, top-to-bottom in each case). Assumes that S is square and
    LL and UR have at least sufficient elements. */
    public static void fillGrid(int[] LL, int[] UR, int[][] S) {
        int n = S.length;
        int kL = 0;
        int kR = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (j > i) {
                    S[i][j] = UR[kR];
                    kR++;
                } else {
                    S[i][j] = LL[kL];
                    kL++;
                }
            }
        }
    }
}

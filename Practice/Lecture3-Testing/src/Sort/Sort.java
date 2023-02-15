package Sort;
public class Sort {
    public static void sort(String[] stringArr) {
        sort(stringArr, 0);
    }

    private static void sort(String[] stringArr, int x) {
        if(x == stringArr.length) {
            return;
        }
        int smallestIndex = findSmallest(stringArr, x);
        // swap it to the front
        swap(stringArr, x, smallestIndex);
        // recursively call until sorted
        sort(stringArr, x + 1);
    }


    /** find the smallest index in arrString starting from startIndex */
    public static int findSmallest(String[] stringArr, int startIndex) {
        int smallestIndex = startIndex;
        for(int i=startIndex; i<stringArr.length; i++) {
            int result = stringArr[i].compareTo(stringArr[smallestIndex]);
            /* According to stackoverflow, compareTo will return -1 if the string that calls it
            * is smaller than the other string */
            if(result < 0) {
                smallestIndex = i;
            }
        }
        return smallestIndex;
    }
    
    public static void swap(String[] stringArr, int x, int y) {
        String temp = stringArr[x];
        stringArr[x] = stringArr[y];
        stringArr[y] = temp;
    }
}

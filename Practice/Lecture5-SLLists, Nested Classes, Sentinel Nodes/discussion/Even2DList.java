public class Even2DList {
    private final int[][] dList;
    private int maxRow;

    public Even2DList(int[][] dList) {
        this.dList = dList;
        for (int[] list : dList) {
            maxRow = Math.max(list.length, maxRow);
        }
    }


}

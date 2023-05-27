//public class Piece {
//    private final int longitude;
//    private final int latitude;
//
//    public Piece(int longitude, int latitude) {
//        this.longitude = longitude;
//        this.latitude = latitude;
//    }
//
//    public int getLongitude() {
//        return longitude;
//    }
//
//    public int getLatitude() {
//        return latitude;
//    }
//
//    public Piece[][] groupByLat(Piece[] p) {
//        int width = 3; // given width of the 2D array
//        Piece[][] latGroup = new Piece[width][width];
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < width; j++) {
//                if (latGroup[j][i] == null) {
//                    latGroup[j][i] = p[i+j];
//                    break;
//                } else if(latGroup[j][i].getLatitude() == p[i+j].getLatitude()) {
//
//                }
//        }
//        return latGroup;
//    }
//}
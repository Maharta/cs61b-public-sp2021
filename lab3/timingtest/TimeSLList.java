package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    private static void fillList(SLList<Integer> L, int n) {
        for (int i = 0; i < n; i++) {
            L.addLast(i);
        }
    }

    public static void timeGetLast() {
        int size = 1000;
        int numberOfGetLastOp = 10000;
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        for (int i = 0; i < 8; i++) {
            Ns.addLast(size);
            opCounts.addLast(numberOfGetLastOp);
            SLList<Integer> L = new SLList<>();
            fillList(L, size);
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < numberOfGetLastOp; j++) {
                L.getLast();
            }
            times.addLast(sw.elapsedTime());
            size*= 2;
        }
        printTimingTable(Ns, times, opCounts);
    }

}

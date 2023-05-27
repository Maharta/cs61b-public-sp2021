package gh2;

import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        List<GuitarString> guitarStringList = new ArrayList<>();
        for (int i = 0; i < keyboard.length(); i++) {
            double hz = 440 * Math.pow(2, (double) (i - 24) / 12);
            guitarStringList.add(new GuitarString(hz));
        }

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                System.out.println(index);
                if (index != -1) {
                    GuitarString keyString = guitarStringList.get(index);
                    keyString.pluck();
                }


            }
            double sample = 0;
            for (GuitarString guitarString : guitarStringList) {
                sample += guitarString.sample();
            }


            StdAudio.play(sample);

            for (GuitarString guitarString : guitarStringList) {
                guitarString.tic();
            }
        }
    }
}

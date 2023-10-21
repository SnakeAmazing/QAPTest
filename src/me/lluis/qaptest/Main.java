package me.lluis.qaptest;

import me.lluis.qaptest.object.Pair;
import me.lluis.qaptest.qap.QAP;
import me.lluis.qaptest.qap.SKQAP;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    public static void main(String[] args) {
        Map<String, Integer> frequencies = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a frequency next to each word");
        System.out.println("Enter an empty line to finish");

        boolean debug = false;
        while (scanner.hasNext() && !debug) {
            if (frequencies.size() > 4600) {
                debug = true;
            }

            String word = scanner.next();

            if (!pattern.matcher(word).find()) continue;

            word = word.toUpperCase();
            int freq = scanner.nextInt();

            if (word.contains("-")) {
                String[] parts = word.split("-");
                for (String part : parts) {
                    frequencies.compute(part, (k, v) -> v == null ? freq : v + freq);
                }
                continue;
            }

            frequencies.compute(word, (k, v) -> v == null ? freq : v + freq);
        }

        System.out.println(frequencies.size());

        SKQAP skqap = new SKQAP(10, 100, 6, 6, frequencies);
        skqap.solve();

        System.out.println("Finished calculating");
    }

    private static void solveQAP() {
        int[][] distances = {
                {0, 2, 3, 1},
                {2, 0, 1, 4},
                {3, 1, 0, 2},
                {1, 4, 2, 0}
        };

        int[][] flows = {
                {0, 1, 2, 3},
                {1, 0, 4, 2},
                {2, 4, 0, 1},
                {3, 2, 1, 0}
        };

        QAP qap = new QAP(distances, flows);
        qap.solve();

        int[] sol = qap.getBestSolution();

        for (int i = 0; i < sol.length; ++i) {
            System.out.print("F" + (sol[i] + 1) + "->L" + (i + 1) + " ");
        }
        System.out.println("\n");
        System.out.println("Min cost = " + qap.getMinCost());
    }
}

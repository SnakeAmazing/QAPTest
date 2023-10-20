package me.lluis.qaptest;

import me.lluis.qaptest.object.Pair;
import me.lluis.qaptest.qap.QAP;
import me.lluis.qaptest.qap.SKQAP;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Map<Map.Entry<Character, Character>, Integer> frequencies = new HashMap<>();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter words to compute frequencies");
        System.out.println("Enter an empty line to finish");

        while (scanner.hasNext()) {
            String word = scanner.next();
            word = word.toUpperCase();

            for (int i = 0; i < word.length() - 1; ++i) {
                char c1 = word.charAt(i);
                char c2 = word.charAt(i + 1);

                Map.Entry<Character, Character> pair = Map.entry(c1, c2);

                frequencies.put(pair, frequencies.getOrDefault(pair, 0) + 1);
            }
        }

        SKQAP skqap = new SKQAP(10, 100, 6, 6, frequencies);
        skqap.solve();
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

package me.lluis.qaptest;

import me.lluis.qaptest.algorithms.BranchAndBound;
import me.lluis.qaptest.input.FileInput;
import me.lluis.qaptest.input.Input;
import me.lluis.qaptest.input.ManualInput;
import me.lluis.qaptest.qap.QAP;
import me.lluis.qaptest.qap.SKQAP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    public static void main(String[] args) {
        solveQAP();
        /*Map<String, Integer> wordFrequencies = new HashMap<>();
        Map<Character, Integer> letterFrequencies = new HashMap<>();

        Input input = new ManualInput(wordFrequencies);
        Input input2 = new FileInput();

        input.computeFrequencies();
        input2.computeFrequencies();

        for (String word : wordFrequencies.keySet()) {
            for (char c : word.toCharArray()) {

                if (letterFrequencies.containsKey(c)) letterFrequencies.put(c, letterFrequencies.get(c) + 1);
                else letterFrequencies.put(c, 1);
            }
        }

        SKQAP skqap = new SKQAP(6, 6, wordFrequencies, letterFrequencies);
        skqap.solve();

        System.out.println("Finished calculating");*/
    }

    private static void solveQAP() {
        int n;
        int[][] distances;
        int[][] flows;
        try (Scanner scanner = new Scanner(new File("resources/chr12b.txt"))) {
            n = scanner.nextInt();

            distances = new int[n][n];
            flows = new int[n][n];

            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    distances[i][j] = scanner.nextInt();
                }
            }

            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n; ++j) {
                    flows[i][j] = scanner.nextInt();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long begin = System.currentTimeMillis();
        BranchAndBound branchAndBound = new BranchAndBound(n, distances, flows);
        branchAndBound.solve();
        long end = System.currentTimeMillis();

        int[] bestAssignment = branchAndBound.getCurrentBestAssignment();
        int cost = branchAndBound.getCurrentBestCost();

        System.out.println("The best solution is: ");
        for (int i = 0; i < n; ++i) {
            System.out.print(bestAssignment[i] + 1 + " ");
        }
        System.out.println("\nWith cost: " + cost);

        System.out.println("Time elapsed: " + (end - begin) + "ms");
    }
}

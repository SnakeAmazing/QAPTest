package me.lluis.qaptest;

import me.lluis.qaptest.algorithms.BranchAndBound;
import me.lluis.qaptest.algorithms.specific.HA;
import me.lluis.qaptest.algorithms.specific.SKBranchAndBound;
import me.lluis.qaptest.input.FileInput;
import me.lluis.qaptest.input.Input;
import me.lluis.qaptest.input.ManualInput;
import me.lluis.qaptest.object.Alphabet;
import me.lluis.qaptest.qap.QAP;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose an option:");
        System.out.println("1. QAP");
        System.out.println("2. SKQAP");
        System.out.println("3. New QAP");
        System.out.println("4. Test Hungarian");
        System.out.println("5. Exit");
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                solveQAP();
                break;
            case 2:
                solveSKQAP();
                break;
            case 3:
                solveQAP2();
                break;
            case 4:
                testHungarian();
                break;
            case 5:
                System.exit(0);
                break;
        }
    }

    private static void testHungarian() {
        int n = 4;
        /*int[][] matrix = new int[][] {
                {21, 34, 31, 43},
                {20, 35, 32, 44},
                {20, 34, 33, 45},
                {21, 34, 31, 43}
        };*/
        int[][] matrix = new int[][] {
                {1, 0, 1, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 0, 1, 0}
        };

        HA ha = new HA(n, matrix);
        ha.solve();
    }

    private static void solveQAP2() {
        int n;
        int[][] distances;
        int[][] flows;
        try (Scanner scanner = new Scanner(new File("resources/chr18.txt"))) {
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

        QAP qap = new QAP(n, distances, flows);
        long begin = System.currentTimeMillis();
        qap.solve();
        long end = System.currentTimeMillis();

        int[] bestAssignment = qap.getCurrentBestAssignment();
        int cost = qap.getCurrentBestCost();

        System.out.println("The best solution is: ");
        for (int i = 0; i < n; ++i) {
            System.out.print(bestAssignment[i] + 1 + " ");
        }
        System.out.println("\nWith cost: " + cost);

        System.out.println("Time elapsed: " + (end - begin) + "ms");
    }

    private static void solveSKQAP() {

        Map<String, Integer> wordFrequencies = new HashMap<>();
        Input input = new ManualInput(wordFrequencies);
        input.computeFrequencies();

        SKBranchAndBound skBranchAndBound = new SKBranchAndBound(4, 8, wordFrequencies);
        long begin = System.currentTimeMillis();
        skBranchAndBound.solve();
        long end = System.currentTimeMillis();

        char[] bestAssignment = skBranchAndBound.getCurrentBestAssignment();
        double cost = skBranchAndBound.getCurrentBestCost();

        System.out.println("The best solution is: ");
        for (char c : bestAssignment) {
            System.out.print(c + " ");
        }
        System.out.println("\nWith cost: " + cost);

        System.out.println("Time elapsed: " + (end - begin) + "ms");
    }

    private static void solveQAP() {
        int n;
        int[][] distances;
        int[][] flows;
        try (Scanner scanner = new Scanner(new File("resources/chr12a.txt"))) {
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

        BranchAndBound branchAndBound = new BranchAndBound(n, distances, flows);
        long begin = System.currentTimeMillis();
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

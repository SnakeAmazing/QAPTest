package me.snakeamazing.qaptest;

import me.snakeamazing.qaptest.algorithms.BranchAndBound;
import me.snakeamazing.qaptest.algorithms.SimulatedAnnealing;
import me.snakeamazing.qaptest.algorithms.specific.HungarianAlgorithm;
import me.snakeamazing.qaptest.algorithms.specific.SKBranchAndBound;
import me.snakeamazing.qaptest.input.Input;
import me.snakeamazing.qaptest.input.ManualInput;
import me.snakeamazing.qaptest.object.Alphabet;
import me.snakeamazing.qaptest.qap.QAP;
import me.snakeamazing.qaptest.util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+$");

    public static void main(String[] args) {
        String test = "hola - adios - á à é è í ï ó ò ú ü ñ ç";
        System.out.println(test);
        test = Normalizer.normalize(test, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("-", "");
        System.out.println(test);

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
                testAnnealing();
                break;
            case 6:
                textToMatrix();
                break;
            case 7:
                System.exit(0);
                break;
        }
    }

    private static void testAnnealing() {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        Input input = new ManualInput(wordFrequencies);
        input.computeFrequencies();

        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(6, 6, wordFrequencies, Alphabet.latinAlphabet());
        simulatedAnnealing.solve();
        System.out.println(Arrays.toString(simulatedAnnealing.getCurrentBestAssignment()));
    }

    private static void testHungarian() {
        int n1 = 5;
        int[][] matrix1 = new int[][] {
                {21, 34, 31, 43},
                {20, 35, 32, 44},
                {20, 34, 33, 45},
                {21, 34, 31, 43}
        };
        int n2 = 4;
        int[][] matrix2 = new int[][] {
                {1, 0, 1, 1},
                {1, 0, 1, 0},
                {0, 1, 0, 1},
                {1, 0, 1, 0}
        };

        int n3 = 10;
        int[][] matrix3 = {
                        {6033, 5262, 4881, 4224,  306, 3678, 1455,  447, 3240, 2601},
                        {12860, 11272, 11751, 10651,  576, 9965, 3935, 1149, 6133, 7801},
                        {6833, 5902, 4126, 3069,  426, 1953, 780,  302, 4475, 596},
                        {7264, 6272, 4331, 3195,  456, 1989, 795,  313, 4789, 541},
                        {3700, 3224, 2917, 2497,  192, 2135, 845,  263, 2031, 1467},
                        {674, 604, 937, 951,  12, 1029, 405,  107, 137, 947},
                        {8190, 7140, 6545, 5635,  420, 4865, 1925,  595, 4445, 3395},
                        {864, 768, 1049, 1033,  24, 1079, 425,  115, 263, 959},
                        {1070, 964, 1612, 1662,  12, 1830, 720,  188, 146, 1712},
                        {6624, 5760, 4935, 4119,  360, 3369, 1335,  429, 3801, 2145}
        };

        int n4 = 4;
        int[][] matrix4 = {
                {27, 44, 82, 69},
                {55, 54, 69, 73},
                {44, 60, 61, 10},
                {92, 30, 4, 43}
        };

        int n5 = 7;
        int[][] matrix5 = {
                {49, 61, 14, 86, 50, 26, 57},
                {10, 33, 12, 18, 77, 74, 71},
                {33, 58, 7, 62, 90, 16, 33},
                {57, 41, 60, 9, 12, 40, 48},
                {49, 94, 62, 70, 98, 38, 64},
                {60, 87, 58, 31, 1, 88, 49},
                {73, 70, 93, 36, 59, 76, 72}
        };

        int n6 = 3;
        int[][] matrix6 = {
                {1, 2, 3},
                {2, 4, 6},
                {3, 6, 9}
        };

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(n6, matrix6);
        hungarianAlgorithm.solve();
        Utils.print(hungarianAlgorithm.getBestAssigment());
        System.out.println(hungarianAlgorithm.getBestCost());
    }

    private static void solveQAP2() {
        int n;
        int[][] distances;
        int[][] flows;
        try (Scanner scanner = new Scanner(new File("resources/tai12a.txt"))) {
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

        int[] bestAssignment = qap.getUnOfficialCurrentBestAssignment();
        int cost = qap.getCurrentBestCost();

        System.out.println("The best solution is: ");
        for (int i = 0; i < n; ++i) {
            System.out.print(bestAssignment[i] + 1 + " ");
        }
        System.out.println();

        char[] bestAssignment2 = qap.getCurrentBestAssigment();
        for (int i = 0; i < n; ++i) {
            System.out.print(bestAssignment2[i] + " ");
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

    private static void textToMatrix() {
        int n;
        int[][] distances;
        int[][] flows;
        try (Scanner scanner = new Scanner(new File("resources/tai12a.txt"))) {
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

            System.out.println("Distances:");
            Utils.print(distances);
            System.out.println("Flows:");
            Utils.print(flows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

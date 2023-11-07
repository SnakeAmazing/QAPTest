package me.lluis.qaptest.qap;

import java.util.Arrays;
import java.util.stream.Stream;

public class QAP {

    private final int n;

    private final int[][] distances;
    private final int[][] flows;

    private int[] bestSol;
    private int bestCost = Integer.MAX_VALUE;

    public QAP(int[][] distances, int[][] flows) {
        this.n = distances.length;
        this.distances = distances;
        this.flows = flows;
    }

    public void solve() {
        System.out.println("Solving QAP with " + n + " facilities");
        int[] solution = new int[n];
        Arrays.fill(solution, -1);
        boolean[] used = new boolean[n];
        //bruteWithFull(solution, used, 0);
        bruteWithPartial(0, solution, used, 0);
        // Initial solution

        /*for (int i = 0; i < n; ++i) {
            solution[i] = i;
        }

        minCost = calculateTotalCost(solution);
        bestSol = solution;

        while (permutation(solution)) {
            int cost = calculateTotalCost(solution);

            if (cost < minCost) {
                minCost = cost;
                bestSol = solution.clone();
            }
        }*/
    }

    private void bruteWithFull(int[] currentSol, boolean[] used, int size) {
        if (size == n) {
            int cost = calculateTotalCost(currentSol);
            if (cost < bestCost) {
                bestCost = cost;
                bestSol = currentSol.clone();
            }
            return;
        }

        for (int i = 0; i < n; ++i) {
            if (!used[i]) {
                used[i] = true;
                currentSol[i] = i;
                bruteWithFull(currentSol, used, size + 1);
                currentSol[i] = -1;
                used[i] = false;
            }
        }
    }

    private void bruteWithPartial(int currentCost, int[] currentSol, boolean[] used, int size) {
        if (size == n) {
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestSol = currentSol.clone();
            }
            return;
        }

        for (int i = 0; i < n; ++i) {
            if (!used[i]) {
                used[i] = true;
                currentSol[i] = i;
                bruteWithPartial(currentCost + calculatePartialCost(currentSol), currentSol, used, size + 1);
                currentSol[i] = -1;
                used[i] = false;
            }
        }
    }

    public int[] getBestSolution() {
        return bestSol;
    }

    public int getMinCost() {
        return bestCost;
    }

    private boolean permutation(int[] solution) {
        int len = solution.length;

        if (len <= 1) return false;

        int last = len - 2;

        while (last >= 0) {
            if (solution[last] < solution[last + 1]) break;
            last--;
        }

        if (last < 0) return false;

        int nextGreater = len - 1;

        for (int i = len - 1; i > last; --i) {
            if (solution[i] > solution[last]) {
                nextGreater = i;
                break;
            }
        }

        swap(solution, nextGreater, last);
        reverse(solution, last + 1, len - 1);

        return true;
    }

    private void swap(int[] solution, int i, int j) {
        int aux = solution[i];
        solution[i] = solution[j];
        solution[j] = aux;
    }

    private void reverse(int[] solution, int i, int j) {
        while (i < j) {
            swap(solution, i, j);
            ++i;
            --j;
        }
    }

    private int calculateTotalCost(int[] solution) {
        int totalCost = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                int f1 = solution[i];
                int f2 = solution[j];

                totalCost += distances[f1][f2] * flows[i][j];
            }
        }

        return totalCost;
    }

    private int calculatePartialCost(int[] solution) {
        int partial = 0;

        for (int i = 0; i < n; ++i) {
            int f1 = solution[i];
            if (f1 == -1) break;

            for (int j = 0; j < n; ++j) {
                int f2 = solution[j];
                if (f2 == -1) break;

                partial += distances[f1][f2] * flows[i][j];
            }
        }

        return partial;
    }
}

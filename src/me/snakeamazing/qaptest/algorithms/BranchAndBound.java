package me.snakeamazing.qaptest.algorithms;

import java.util.Arrays;
import java.util.Random;

public class BranchAndBound {

    private final int n;
    private int[][] distanceMatrix;
    private int[][] flowMatrix;

    private int[] currentBestAssignment;
    private int currentBestCost;

    private final Random random = new Random();

    public BranchAndBound(int n, int[][] distanceMatrix, int[][] flowMatrix) {
        this.n = n;
        this.distanceMatrix = distanceMatrix;
        this.flowMatrix = flowMatrix;

        generateInitialSolution();
    }

    public void solve() {
        int[] currentAssignment = new int[n];
        boolean[] alreadyInAssignment = new boolean[n];

        treeExploration(0, 0, currentAssignment, alreadyInAssignment);
    }

    private void treeExploration(int currentCost, int currentSize, int[] currentAssigment, boolean[] alreadyInAssignment) {
        if (currentSize == n) {
            if (currentCost < currentBestCost) {
                currentBestCost = currentCost;
                currentBestAssignment = currentAssigment.clone();
                System.out.println("New best solution: " + Arrays.toString(currentBestAssignment) + " with cost " + currentBestCost);
            }
        } else if (currentSize == 0) {
            for (int i = 0; i < n; ++i) {
                currentAssigment[0] = i;
                alreadyInAssignment[i] = true;
                treeExploration(0, 1, currentAssigment, alreadyInAssignment);
                alreadyInAssignment[i] = false;
            }
        } else {
            int lowerBound = 0;
            boolean lowerBoundEvaluated = false;

            if (currentSize < n - 1) {
                lowerBound = computeLowerBound(currentSize, alreadyInAssignment, currentCost);
                lowerBoundEvaluated = true;
            }

            if (lowerBoundEvaluated && lowerBound > currentBestCost) {
                return;
            }

            for (int i = 0; i < n; ++i) {
                if (!alreadyInAssignment[i]) {
                    currentAssigment[currentSize] = i;
                    alreadyInAssignment[i] = true;

                    int costIncrease = 0;

                    for (int j = 0; j < currentSize; ++j) {
                        costIncrease += distanceMatrix[currentSize][j] * flowMatrix[i][currentAssigment[j]];
                        costIncrease += distanceMatrix[j][currentSize] * flowMatrix[currentAssigment[j]][i];
                    }

                    treeExploration(currentCost + costIncrease, currentSize + 1, currentAssigment, alreadyInAssignment);

                    alreadyInAssignment[i] = false;
                }
            }
        }
    }

    private int computeLowerBound(int currentSize, boolean[] alreadyInAssignment, int currentCost) {
        int remainingSize = n - currentSize;
        int[][] tempDistanceMatrix = new int[remainingSize][remainingSize];
        int[][] tempFlowMatrix = new int[remainingSize][remainingSize];

        int[] distanceMapping = new int[n];
        int[] flowMapping = new int[n];

        for (int i = 0; i < remainingSize; ++i) {
            tempDistanceMatrix[i] = new int[remainingSize - 1];
            tempFlowMatrix[i] = new int[remainingSize - 1];
        }

        int row = 0;
        int col;
        for (int i = currentSize; i < n; ++i) {
            col = 0;

            for (int j = currentSize; j < n; ++j) {
                if (i != j) {
                    tempDistanceMatrix[row][col] = distanceMatrix[i][j];
                    ++col;
                } else {
                    distanceMapping[row] = distanceMatrix[i][j];
                }
            }

            Arrays.sort(tempDistanceMatrix[row]); // CHECK THIS
            ++row;
        }

        row = 0;

        for (int i = 0; i < n; ++i) {
            if (alreadyInAssignment[i]) continue;

            col = 0;

            for (int j = 0; j < n; ++j) {
                if (!alreadyInAssignment[j]) {
                    if (i != j) {
                        tempFlowMatrix[row][col] = flowMatrix[i][j];
                        ++col;
                    } else {
                        flowMapping[row] = flowMatrix[i][j];
                    }
                }
            }

            Arrays.sort(tempFlowMatrix[row]); // CHECK THIS
            ++row;
        }

        int[][] min = new int[remainingSize][remainingSize];
        for (int i = 0; i < remainingSize; ++i) {
            min[i] = new int[remainingSize];
            Arrays.fill(min[i], 0);
        }

        for (int i = 0; i < remainingSize; ++i) {
            for (int j = 0; j < remainingSize; ++j) {
                for (int k = 0; k < remainingSize - 1; ++k) {
                    min[i][j] += tempDistanceMatrix[j][k] * tempFlowMatrix[i][k];
                }
            }
        }

        int[][] g = new int[remainingSize][remainingSize];

        for (int i = 0; i < remainingSize; ++i) {
            g[i] = new int[remainingSize];

            for (int j = 0; j < remainingSize; ++j)
                g[i][j] = flowMapping[i] * distanceMapping[j] + min[i][j];
        }

        //HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(g);
        //int lap = hungarianAlgorithm.compute();

        return currentCost + 0;
    }

    private void generateInitialSolution() {
        currentBestCost = 0;
        currentBestAssignment = new int[n];

        for (int i = 0; i < n; ++i) {
            currentBestAssignment[i] = i;
        }

        shuffle(currentBestAssignment);
        computeCost();

        System.out.println("Initial solution: " + Arrays.toString(currentBestAssignment) + " with cost " + currentBestCost);
    }

    private void shuffle(int[] array) {
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private void swap(int[] array, int i, int j) {
        int aux = array[i];
        array[i] = array[j];
        array[j] = aux;
    }

    private void computeCost() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                currentBestCost += distanceMatrix[i][j] * flowMatrix[currentBestAssignment[i]][currentBestAssignment[j]];
            }
        }
    }

    public int[] getCurrentBestAssignment() {
        return currentBestAssignment;
    }

    public int getCurrentBestCost() {
        return currentBestCost;
    }
}

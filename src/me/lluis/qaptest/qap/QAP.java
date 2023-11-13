package me.lluis.qaptest.qap;

import me.lluis.qaptest.algorithms.HungarianAlgorithm;
import me.lluis.qaptest.object.Pair;
import me.lluis.qaptest.util.Utils;

import java.util.*;

public class QAP {

    private final int n;
    private int[][] distanceMatrix;
    private int[][] flowMatrix;

    private int[] currentBestAssignment;
    private int currentBestCost;

    private final Random random = new Random();

    public QAP(int n, int[][] distanceMatrix, int[][] flowMatrix) {
        this.n = n;
        this.distanceMatrix = distanceMatrix;
        this.flowMatrix = flowMatrix;
        greedyInit(currentBestAssignment);
    }

    public void solve() {
        System.out.println(currentBestCost);

        int[] currentAssigment = new int[n];
        boolean[] alreadyInAssignment = new boolean[n];

        treeExploration(0, 0, currentAssigment, alreadyInAssignment);
    }

    private void generateInitialSolution(int[] assigment) {
        currentBestCost = 0;
        assigment = new int[n];

        for (int i = 0; i < n; ++i) {
            assigment[i] = i;
        }

        shuffle(assigment);
        computeCost(assigment);

        System.out.println("Initial solution: " + Arrays.toString(assigment) + " with cost " + currentBestCost);
    }

    private void greedyInit(int[] assigment) {
        assigment = new int[n];
        Arrays.fill(assigment, -1);

        Map<Pair<Integer, Integer>, Integer> distanceMap = new HashMap<>();
        for (int i = 1; i < n; ++i) {
            for (int j = 0; j < i; ++j) {
                distanceMap.put(new Pair<>(i, j), distanceMatrix[i][j]);
            }
        }

        List<Map.Entry<Pair<Integer, Integer>, Integer>> distList = distanceMap.entrySet().stream().toList(); // Ascending order

        Map<Pair<Integer, Integer>, Integer> flowMap = new HashMap<>();
        for (int i = 1; i < n; ++i) {
            for (int j = 0; j < i; ++j) {
                flowMap.put(new Pair<>(i, j), flowMatrix[i][j]);
            }
        }

        List<Map.Entry<Pair<Integer, Integer>, Integer>> flowList = flowMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList(); // Descending order

        boolean[] usedLoc = new boolean[n];
        boolean[] usedElem = new boolean[n];

        boolean filled = false;
        boolean remainPairs = true;

        int index = 0;
        while (index < n) {
            Map.Entry<Pair<Integer, Integer>, Integer> entryFlow = flowList.get(index); // Position with the higher flow
            int iF = entryFlow.getKey().getFirst();
            int jF = entryFlow.getKey().getSecond();

            Map.Entry<Pair<Integer, Integer>, Integer> entryDist = distList.get(index); // Position with the lowest cost
            int iD = entryDist.getKey().getFirst();
            int jD = entryDist.getKey().getSecond();

            if (!usedElem[iF] && !usedElem[jF] && remainPairs) {
                // Intentem posar-les juntes
                if (!usedLoc[iD] && !usedLoc[jD]) {
                    assigment[iD] = iF;
                    assigment[jD] = jF;
                    usedLoc[iD] = true;
                    usedLoc[jD] = true;
                    usedElem[iF] = true;
                    usedElem[jF] = true;
                    ++index;
                } else {
                    // Cerquem un parell de posicions lliures
                    int i = index + 1;
                    boolean found = false;
                    int ni = 0;
                    int nj = 0;
                    while (i < n - 1 && !found) {
                        Map.Entry<Pair<Integer, Integer>, Integer> newP = distList.get(i); // Position with the lowest cost
                        ni = entryDist.getKey().getFirst();
                        nj = entryDist.getKey().getSecond();

                        if (!usedLoc[ni] && !usedLoc[nj]) {
                            found = true;
                        } else {
                            ++i;
                        }
                    }
                    if (i >= n - 1) remainPairs = false;
                    else {
                        assigment[ni] = iF;
                        assigment[nj] = jF;
                        usedLoc[ni] = true;
                        usedLoc[nj] = true;
                        usedElem[iF] = true;
                        usedElem[jF] = true;
                    }
                }
            } else {
                if (!usedElem[iF]) {
                    if (!usedLoc[iD]) {
                        assigment[iD] = iF;
                        usedLoc[iD] = true;
                        usedElem[iF] = true;
                    }
                }

                if (!usedLoc[jF]) {
                    if (!usedLoc[jD]) {
                        assigment[jD] = jF;
                        usedLoc[jD] = true;
                        usedElem[jF] = true;
                    }
                }
            }

            ++index;
        }

        for (int i = 0; i < usedLoc.length; ++i) {
            boolean b = usedLoc[i];
            if (!b) {
                assigment[i] = i;
            }
        }

        Utils.printArray(assigment);
        computeCost(assigment);
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

    private void computeCost(int[] assigment) {
        currentBestCost = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                //System.out.println("Adding (" + i + ", " + j + ") " + "(" + assigment[i] + ", " + assigment[j] + ") " + distanceMatrix[i][j] + " * " + flowMatrix[assigment[i]][assigment[j]]);
                currentBestCost += distanceMatrix[i][j] * flowMatrix[assigment[i]][assigment[j]];
            }
        }
    }

    public int getCurrentBestCost() {
        return currentBestCost;
    }

    public int[] getCurrentBestAssignment() {
        return currentBestAssignment;
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

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(g);
        int lap = hungarianAlgorithm.compute();

        return currentCost + lap;
    }
}

package me.snakeamazing.qaptest.qap;

import me.snakeamazing.qaptest.algorithms.specific.HungarianAlgorithm;
import me.snakeamazing.qaptest.object.Alphabet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QAP {

    private List<Character> characters = Alphabet.latinAlphabet();

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
        generateInitialSolution(currentBestAssignment);
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
        currentBestCost = computeCost(assigment);

        currentBestAssignment = assigment;

        hillClimbing();

        System.out.println("Initial solution: " + Arrays.toString(assigment) + " with cost " + currentBestCost);
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

    private int computeCost(int[] assigment) {
        int cost = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                //System.out.println("Adding (" + i + ", " + j + ") " + "(" + assigment[i] + ", " + assigment[j] + ") " + distanceMatrix[i][j] + " * " + flowMatrix[assigment[i]][assigment[j]]);
                cost += distanceMatrix[i][j] * flowMatrix[assigment[i]][assigment[j]];
            }
        }

        return cost;
    }

    public int getCurrentBestCost() {
        return currentBestCost;
    }

    public int[] getUnOfficialCurrentBestAssignment() {
        return currentBestAssignment;
    }

    public char[] getCurrentBestAssigment() {
        char[] official = new char[n];
        for (int i = 0; i < n; ++i) {
            official[i] = characters.get(currentBestAssignment[i]);
        }
        return official;
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

                    //boolean next = false;
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
        // C1
        int m = n - currentSize;

        int[] placed = new int[currentSize];
        int[] unplaced = new int[m];

        int p = 0;
        int u = 0;
        for (int i = 0; i < alreadyInAssignment.length; ++i) {
            if (alreadyInAssignment[i]) {
                placed[p] = i;
                ++p;
            } else {
                unplaced[u] = i;
                ++u;
            }
        }

        int[][] c1 = new int[m][m];
        for (int i = 0; i < m; ++i) {
            for (int k = 0; k < m; ++k) {
                for (int j = 0; j < placed.length; ++j) {
                    int d = distanceMatrix[currentSize + k][j];
                    int f = distanceMatrix[unplaced[i]][placed[j]];
                    c1[i][k] += d * f;
                }
            }
        }

        int[][] c2 = new int[m][m];

        for (int i = 0; i < m; ++i) {
            int pos = 0;
            int[] flowAux = new int[m - 1];

            for (int j = 0; j < m; ++j) {
                if (j != i) {
                    flowAux[pos] = flowMatrix[unplaced[i]][unplaced[j]];
                    ++pos;
                }
            }

            Arrays.sort(flowAux);

            for (int k = 0; k < m; ++k) {
                Integer[] distanceAux = new Integer[m - 1];
                pos = 0;

                for (int j = 0; j < m; ++j) {
                    if (j != k) {
                        distanceAux[pos] = distanceMatrix[currentSize + j][currentSize + k];
                        ++pos;
                    }
                }

                Arrays.sort(distanceAux, Collections.reverseOrder());

                int prod = 0;
                for (int j = 0; j < flowAux.length; ++j) {
                    if (i != j) {
                        prod += flowAux[j] * distanceAux[j];
                    }
                }

                c2[i][k] = prod;
            }
        }

        int[][] c = new int[m][m];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                c[i][j] = c1[i][j] + c2[i][j];
            }
        }

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(m, c);
        hungarianAlgorithm.solve();
        int cost = hungarianAlgorithm.getBestCost();

        return currentCost + cost;
    }

    private void hillClimbing() {
        int[] best = currentBestAssignment.clone();

        int i = n * n;
        while (i > 0) {
            int[] newSolution = generateNeighbor(best);
            int newCost = computeCost(newSolution);

            if (newCost < currentBestCost) {
                best = newSolution.clone();
                currentBestCost = newCost;
            }
            --i;
        }

        currentBestAssignment = best.clone();
    }

    private int[] generateNeighbor(int[] solution) {
        int[] newSolution = solution.clone();
        int index1 = (int) (Math.random() * solution.length);
        int index2 = (int) (Math.random() * solution.length);

        swap(newSolution, index1, index2);
        return newSolution;
    }

    public void setDistanceMatrix(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void setFlowMatrix(int[][] flowMatrix) {
        this.flowMatrix = flowMatrix;
    }
}

package me.lluis.qaptest.algorithms.specific;

import me.lluis.qaptest.algorithms.HungarianAlgorithm;
import me.lluis.qaptest.object.Alphabet;
import me.lluis.qaptest.object.CharPair;

import java.util.*;

public class SKBranchAndBound {

    private final List<Character> letters = Alphabet.latinAlphabet();

    private final int n;
    private int[][] distanceMatrix;
    private int[][] flowMatrix;

    private char[] currentBestAssignment;
    private double currentBestCost;

    private final Map<String, Integer> wordFrequencies;
    private final Map<CharPair, Integer> pairFrequencies;

    private final Random random = new Random();

    public SKBranchAndBound(int n, Map<String, Integer> wordFrequencies) {
        this.n = letters.size();

        this.wordFrequencies = wordFrequencies;
        this.pairFrequencies = new HashMap<>();

        createDistanceMatrix();
        wordFreq2PairFreq();

        generateInitialSolution();
    }

    public void solve() {
        char[] currentAssignment = new char[n];
        boolean[] alreadyInAssignment = new boolean[n];

        /*for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                System.out.print(distanceMatrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                System.out.print(flowMatrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        */
        System.out.println(Arrays.toString(currentBestAssignment));
        System.out.println("Cost = " + currentBestCost);

        System.out.println("\nStarting computation...");

        treeExploration(0, 0, currentAssignment, alreadyInAssignment);
    }

    private void createDistanceMatrix() {
        distanceMatrix = new int[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                distanceMatrix[i][j] = Math.abs(i - j);
            }
        }
    }

    private void treeExploration(double currentCost, int currentSize, char[] currentAssigment, boolean[] alreadyInAssignment) {
        if (currentSize == n) {
            if (currentCost < currentBestCost) {
                currentBestCost = currentCost;
                currentBestAssignment = currentAssigment.clone();
                System.out.println("New best solution: " + Arrays.toString(currentBestAssignment) + " with cost " + currentBestCost);
            }
        } else if (currentSize == 0) {
            for (int i = 0; i < n; ++i) {
                currentAssigment[0] = letters.get(i);
                alreadyInAssignment[i] = true;
                treeExploration(0, 1, currentAssigment, alreadyInAssignment);
                alreadyInAssignment[i] = false;
            }
        } else {
            double lowerBound = 0;
            boolean lowerBoundEvaluated = false;

            if (currentSize < n - 1) {
                lowerBound = computeLowerBound(currentSize, alreadyInAssignment, currentCost);
                lowerBoundEvaluated = true;
            }

            if (lowerBoundEvaluated && lowerBound > currentBestCost) {
                System.out.println("Discarding branch with lower bound " + lowerBound);
                return;
            }

            for (int i = 0; i < n; ++i) {
                if (!alreadyInAssignment[i]) {
                    currentAssigment[currentSize] = letters.get(i);
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

    private double computeLowerBound(int currentSize, boolean[] alreadyInAssignment, double currentCost) {
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
        int lap = hungarianAlgorithm.findOptimalAssignment();

        return currentCost + lap;
    }

    private void generateInitialSolution() {
        currentBestCost = 0;
        currentBestAssignment = new char[n];

        for (int i = 0; i < n; ++i) {
            currentBestAssignment[i] = letters.get(i);
        }

        shuffle(currentBestAssignment);
        computeCost();

        System.out.println("Initial solution: " + Arrays.toString(currentBestAssignment) + " with cost " + currentBestCost);
    }

    private void shuffle(char[] array) {
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private void swap(char[] array, int i, int j) {
        char aux = array[i];
        array[i] = array[j];
        array[j] = aux;
    }

    private void computeCost() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                //currentBestCost += distanceMatrix[i][j] * flowMatrix[currentBestAssignment[i]][currentBestAssignment[j]];
                // Fitts law
                //if (currentBestAssignment[i] == 0 || currentBestAssignment[j] == 0) continue;
                int distance = distanceMatrix[currentBestAssignment[i] - 'A'][currentBestAssignment[j] - 'A'];
                double keySize = 10.0;
                double cost = (double) 10 /49 * Math.log(distance / keySize + 1);
                int freq = flowMatrix[currentBestAssignment[i] - 'A'][currentBestAssignment[j] - 'A'];

                //System.out.println(distance + " " + cost + " " + freq);
                currentBestCost += cost * freq;
            }
        }
    }

    private void wordFreq2PairFreq() {
        flowMatrix = new int[n][n];
        for (String word : wordFrequencies.keySet()) {
            for (int i = 0; i < word.length() - 1; ++i) {
                char c = word.charAt(i);
                char c2 = word.charAt(i + 1);

                CharPair pair = new CharPair(c, c2);
                pairFrequencies.compute(pair, (k, v) -> v == null ? 1 : v + 1);
                flowMatrix[c - 'A'][c2 - 'A'] += 1;
            }
        }
    }

    public char[] getCurrentBestAssignment() {
        return currentBestAssignment;
    }

    public double getCurrentBestCost() {
        return currentBestCost;
    }
}

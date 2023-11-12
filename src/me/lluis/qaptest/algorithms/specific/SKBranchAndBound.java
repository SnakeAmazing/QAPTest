package me.lluis.qaptest.algorithms.specific;

import me.lluis.qaptest.algorithms.HungarianAlgorithm;
import me.lluis.qaptest.object.Alphabet;
import me.lluis.qaptest.object.CharPair;

import java.util.*;


// Cost?
// lower cote
// distancia entre tecles?
// tamanay tecles?
// tamany matriu?
//

public class SKBranchAndBound {

    private final List<Character> letters = Alphabet.latinAlphabet();

    private final int n;
    private final int realRows;
    private final int realCols;
    private final int rows;
    private final int cols;

    private double[][] distanceMatrix;
    private int[][] flowMatrix;

    private char[] currentBestAssignment;
    private double currentBestCost;

    private final Map<String, Integer> wordFrequencies;
    private final Map<CharPair, Integer> pairFrequencies;

    private final Random random = new Random();

    public SKBranchAndBound(int rows, int cols, Map<String, Integer> wordFrequencies) {
        this.n = letters.size();

        this.realRows = rows;
        this.realCols = cols;

        if (realRows >= realCols) {
            this.rows = realRows;
            this.cols = realRows;
        } else {
            this.rows = realCols;
            this.cols = realCols;
        }

        this.wordFrequencies = wordFrequencies;
        this.pairFrequencies = new HashMap<>();

        createDistanceMatrix();
        computeFrequencies();

        generateInitialSolution();
    }

    /**
     * Solves the problem
     * Stores the best assigment found in currentBestAssignment
     */
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
        /*
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

    /**
     * Creates the distance matrix
     */
    private void createDistanceMatrix() {
        distanceMatrix = new double[n][n];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                // Euclidean distance between to matrix positions
                int row1 = i / rows;
                int col1 = i % cols;
                int row2 = j / rows;
                int col2 = j % cols;

                double keySize = 10.0;
                double x1 = col1 * keySize;
                double y1 = row1 * keySize;
                double x2 = col2 * keySize;
                double y2 = row2 * keySize;

                double euclideanDistance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                double B = 10 / 49.0;
                double movement = B * Math.log((euclideanDistance / keySize) + 1) / Math.log(2);

                distanceMatrix[i][j] = (int) (movement * 10);
            }
        }
    }

    /**
     * Explores the tree of possible solutions
     * @param currentCost the cost of the current solution
     * @param currentSize the size of the current solution
     * @param currentAssigment the current solution
     * @param alreadyInAssignment an array of booleans that indicates if a letter is already in the current solution
     */
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
                //System.out.println("Discarding branch with lower bound " + lowerBound);
                return;
            }

            for (int i = 0; i < n; ++i) {
                if (!alreadyInAssignment[i]) {
                    currentAssigment[currentSize] = letters.get(i);
                    alreadyInAssignment[i] = true;

                    int costIncrease = 0;
                    for (int j = 0; j < currentSize; ++j) {
                        costIncrease += distanceMatrix[currentSize][j] * flowMatrix[i][currentAssigment[j] - 'A'];
                        costIncrease += distanceMatrix[j][currentSize] * flowMatrix[currentAssigment[j] - 'A'][i];
                    }

                    treeExploration(currentCost + costIncrease, currentSize + 1, currentAssigment, alreadyInAssignment);

                    alreadyInAssignment[i] = false;
                }
            }
        }
    }

    /**
     * Computes the Gilmore-Lawler lower bound of the current solution
     * @param currentSize the size of the current solution
     * @param alreadyInAssignment an array of booleans that indicates if a letter is already in the current solution
     * @param currentCost the cost of the current solution
     * @return the lower bound of the current solution
     */
    private double computeLowerBound(int currentSize, boolean[] alreadyInAssignment, double currentCost) {
        int remainingSize = n - currentSize;
        double[][] tempDistanceMatrix = new double[remainingSize][remainingSize];
        int[][] tempFlowMatrix = new int[remainingSize][remainingSize];

        double[] distance = new double[n];
        int[] flow = new int[n];

        for (int i = 0; i < remainingSize; ++i) {
            tempDistanceMatrix[i] = new double[remainingSize - 1];
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
                    distance[row] = distanceMatrix[i][j];
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
                        flow[row] = flowMatrix[i][j];
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

        // Let's build C2
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

            for (int j = 0; j < remainingSize; ++j) {
                g[i][j] = (int) (flow[i] * distance[j] + min[i][j]);
            }
        }

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(g);
        int cost = hungarianAlgorithm.findOptimalAssignment();

        return currentCost + cost;
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
                double cost = distanceMatrix[i][j];
                int freq = flowMatrix[currentBestAssignment[i] - 'A'][currentBestAssignment[j] - 'A'];

                currentBestCost += cost * freq;
            }
        }
    }

    /**
     * Computes the frequencies of pairs of letters and stores it in the flow matrix
     *
     * Possible test: The same pair but inverted should be counted as the same pair. p.e "AB" and "BA" should be counted as the same pair
     */
    private void computeFrequencies() {
        flowMatrix = new int[n][n];
        for (String word : wordFrequencies.keySet()) {
            for (int i = 0; i < word.length() - 1; ++i) {
                char c = word.charAt(i);
                char c2 = word.charAt(i + 1);

                CharPair pair = new CharPair(c, c2);
                pairFrequencies.compute(pair, (k, v) -> v == null ? 1 : v + 1);
            }
        }

        for (CharPair pair : pairFrequencies.keySet()) {
            int freq = pairFrequencies.get(pair);
            flowMatrix[pair.getFirst() - 'A'][pair.getSecond() - 'A'] = freq;
            flowMatrix[pair.getSecond() - 'A'][pair.getFirst() - 'A'] = freq;
        }
    }

    public char[] getCurrentBestAssignment() {
        return currentBestAssignment;
    }

    public double getCurrentBestCost() {
        return currentBestCost;
    }
}
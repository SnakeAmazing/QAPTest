package me.snakeamazing.qaptest.algorithms.specific;

import me.snakeamazing.qaptest.object.Alphabet;
import me.snakeamazing.qaptest.object.CharPair;
import me.snakeamazing.qaptest.util.Utils;

import java.util.*;

public class SKBranchAndBound {

    private final List<Character> letters = Alphabet.reduced();

    private final int n;
    private final int realRows;
    private final int realCols;
    private final int rows;
    private final int cols;

    private int[][] distanceMatrix;
    private int[][] flowMatrix;

    private int[] currentBestAssignment;
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
        int[] currentAssignment = new int[n];
        boolean[] alreadyInAssignment = new boolean[n];

        System.out.println(Arrays.toString(currentBestAssignment));
        System.out.println("Cost = " + currentBestCost);

        System.out.println("\nStarting computation...");
        treeExploration(0, 0, currentAssignment, alreadyInAssignment);
    }

    /**
     * Creates the distance matrix
     */
    private void createDistanceMatrix() {
        distanceMatrix = new int[n][n];

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

                System.out.println(movement);
                distanceMatrix[i][j] = (int) (movement * 10);
            }
        }
    }

    /**
     * Explores the tree of possible solutions
     *
     * @param currentCost         the cost of the current solution
     * @param currentSize         the size of the current solution
     * @param currentAssigment    the current solution
     * @param alreadyInAssignment an array of booleans that indicates if a letter is already in the current solution
     */
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
            double lowerBound = 0;
            boolean lowerBoundEvaluated = false;

            if (currentSize < n - 12) {
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
                        costIncrease += distanceMatrix[currentSize][j] * flowMatrix[i][currentAssigment[j]];
                        costIncrease += distanceMatrix[j][currentSize] * flowMatrix[currentAssigment[j]][i];
                    }

                    treeExploration(currentCost + costIncrease, currentSize + 1, currentAssigment, alreadyInAssignment);

                    alreadyInAssignment[i] = false;
                }
            }
        }
    }

    /**
     * Computes the Gilmore-Lawler lower bound of the current solution
     *
     * @param currentSize         the size of the current solution
     * @param alreadyInAssignment an array of booleans that indicates if a letter is already in the current solution
     * @param currentCost         the cost of the current solution
     * @return the lower bound of the current solution
     */
    private double computeLowerBound(int currentSize, boolean[] alreadyInAssignment, double currentCost) {
        int m = n - currentSize;

        int[] placed = new int[currentSize]; // indexes of the placed characters
        int[] unplaced = new int[m]; // indexes of the unplaced characters

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
        // We create c1
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
        // We create c2
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
                Integer[] distanceAux = new Integer[m - 1]; // We use boxed integer to use the reverse order comparator
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

        int[][] c = new int[m][m]; // C = C1 + C2
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

    private void generateInitialSolution() {
        currentBestCost = 0;
        currentBestAssignment = new int[n];

        for (int i = 0; i < n; ++i) {
            currentBestAssignment[i] = letters.get(i);
        }

        shuffle(currentBestAssignment);
        Utils.print(distanceMatrix);
        Utils.print(flowMatrix);
        currentBestCost = computeCost(currentBestAssignment);

        System.out.println("Initial solution: " + Arrays.toString(currentBestAssignment) + " with cost " + currentBestCost);
        hillClimbing();
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

    private int computeCost(int[] assignment) {
        int total = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                int cost = distanceMatrix[i][j];
                int freq = flowMatrix[assignment[i]][assignment[j]];

                total += cost * freq;
            }
        }

        return total;
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

    /**
     * Computes the frequencies of pairs of letters and stores it in the flow matrix
     * Possible test: The same pair but inverted should be counted as the same pair. p.e "AB" and "BA" should be counted as the same pair
     */
    private void computeFrequencies() {
        flowMatrix = new int[n][n];
        for (String word : wordFrequencies.keySet()) {
            for (int i = 0; i < word.length() - 1; ++i) {
                char c = word.charAt(i);
                char c2 = word.charAt(i + 1);

                if (c == c2 || !letters.contains(c) || !letters.contains(c2)) continue;

                CharPair pair = new CharPair(c, c2);
                int wordFreq = wordFrequencies.get(word) / 100;
                if (pairFrequencies.containsKey(pair)) {
                    pairFrequencies.put(pair, pairFrequencies.get(pair) + Math.max(1, wordFreq));
                } else {
                    pairFrequencies.put(pair, Math.max(1, wordFreq));
                }
            }
        }

        for (CharPair pair : pairFrequencies.keySet()) {
            int freq = pairFrequencies.get(pair);
            flowMatrix[pair.getFirst() - 'A'][pair.getSecond() - 'A'] = freq;
            flowMatrix[pair.getSecond() - 'A'][pair.getFirst() - 'A'] = freq;
        }
    }

    public char[] getCurrentBestAssignment() {
        char[] res = new char[n];

        for (int i = 0; i < n; ++i) {
            res[i] = letters.get(currentBestAssignment[i]);
        }

        return res;
    }

    public double getCurrentBestCost() {
        return currentBestCost;
    }
}
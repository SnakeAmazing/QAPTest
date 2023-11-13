package me.lluis.qaptest.algorithms;
import me.lluis.qaptest.object.Pair;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of the Hungarian Algorithm
 * By Lluis
 */
public class HungarianAlgorithm {

    private final int n; // Matrix size
    // Rows of the matrix represent the facilities
    // Columns of the matrix represent the locations
    private int[][] matrix; // Matrix computed from the BranchAndBound algorithm

    private final int[] zerosInRow; // The position where a zero is marked in the rows
    private final int[] zerosInColumn; // The position where a zero is marked in the columns
    private final boolean[] rowIsCovered; // Indicates whether a row is covered
    private final boolean[] colIsCovered; // Indicates whether a column is covered
    private final int[] starredZeroesInRow; // Storage for the 0

    public HungarianAlgorithm(int[][] matrix) {
        checkIfSquared(matrix);

        n = matrix.length;

        zerosInRow = new int[n];    // squareInRow & squareInCol indicate the position
        zerosInColumn = new int[n];    // of the marked zeroes

        rowIsCovered = new boolean[n];   // indicates whether a row is covered
        colIsCovered = new boolean[n];   // indicates whether a column is covered
        starredZeroesInRow = new int[n]; // storage for the 0

        Arrays.fill(starredZeroesInRow, -1);
        Arrays.fill(zerosInRow, -1);
        Arrays.fill(zerosInColumn, -1);
    }

    /**
     * Compute the matrix to find an optimal assigment for the problem
     *
     * @return the cost of the assigment
     */
    public int compute() {
        reduceMatrix();
        coverZeros();

        while (!allColumnsAreCovered()) {
            Pair<Integer, Integer> primedZero = primeZero();

            while (primedZero == null) {  // while no zero found
                subtractMinimum();
                primedZero = primeZero();
            }

            if (zerosInRow[primedZero.getFirst()] == -1) {
                // there is no zero marked in the row of the zero found
                kChain(primedZero);
                checkColumnsWithZero();    // cover columns which contain a marked zero
            } else {
                // there is a starred zero in the row of the zero found

                rowIsCovered[primedZero.getFirst()] = true;  // Cover the row of the zero found
                colIsCovered[zerosInRow[primedZero.getFirst()]] = false;  // Uncover the column of the zero found
                subtractMinimum();
            }
        }

        int cost = 0;
        for (int i = 0; i < zerosInColumn.length; ++i) {
            cost += matrix[i][zerosInColumn[i]];
        }

        return cost;
    }

    /**
     * Check if all columns are covered
     * If all of them are covered, then we have found an optimal solution
     *
     * @return true or false weather all columns are covered
     */
    private boolean allColumnsAreCovered() {
        for (boolean i : colIsCovered) {
            if (!i) return false;
        }

        return true;
    }

    /**
     * Reduce the matrix by doing the following:
     * 1. Find and subtract the minimum value of each row from each element of the row
     * 2. Find and subtract the minimum value of each column from each element of the column
     * Results in a matrix which at least has one zero in each row and column
     */
    private void reduceMatrix() {
        // Find min element from each row
        for (int i = 0; i < matrix.length; i++) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < matrix.length; j++) {
                int e = matrix[i][j];
                if (e < min) {
                    min = e;
                }
            }

            if (min == 0) continue; // We don't need to subtract 0

            // Subtract the current min element found to the current row
            for (int k = 0; k < matrix[i].length; k++) {
                matrix[i][k] -= min;
            }
        }

        // Find min element from each column
        for (int i = 0; i < matrix.length; i++) {
            int min = Integer.MAX_VALUE;
            for (int[] ints : matrix) {
                if (ints[i] < min) {
                    min = ints[i];
                }
            }

            if (min == 0) continue; // We don't need to subtract 0

            // Subtract the current min element found to the current column
            for (int k = 0; k < matrix.length; k++) {
                matrix[k][i] -= min;
            }
        }
    }

    /**
     * Cover the zero elements of the matrix with the minimum number of lines
     * If the number of lines is equal to the number of rows, then an optimal solution is found
     */
    private void coverZeros() {
        boolean[] rowCovered = new boolean[n];
        boolean[] colCovered = new boolean[n];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                // star the first zero element found in each row and column
                if (matrix[i][j] == 0 && !rowCovered[i] && !colCovered[j]) {
                    rowCovered[i] = true;
                    colCovered[j] = true;
                    zerosInRow[i] = j; // Save the position of the zero in the row
                    zerosInColumn[j] = i; // Save the position of the zero in the column
                }
            }
        }

        checkColumnsWithZero();
    }

    /**
     * Check which columns have a starred zero in it
     * If all of them have one, we finish.
     */
    private void checkColumnsWithZero() {
        for (int i = 0; i < zerosInColumn.length; i++) {
            if (zerosInColumn[i] != -1) {
                colIsCovered[i] = !colIsCovered[i];
            }
        }
    }

    /**
     * Find a non-covered zero in a non-covered row and column and prime it
     *
     * @return position of the primed zero in the matrix
     */
    private Pair<Integer, Integer> primeZero() {
        for (int i = 0; i < matrix.length; i++) {
            if (!rowIsCovered[i]) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (!colIsCovered[j]) {
                        if (matrix[i][j] == 0) {
                            starredZeroesInRow[i] = j; // mark as temporal star
                            return new Pair<>(i, j);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a chain K of alternating primed and starred zeros
     *
     * @param primedZero primedZero
     */
    private void kChain(Pair<Integer, Integer> primedZero) {
        int i;
        int j = primedZero.getSecond();

        Set<Pair<Integer, Integer>> K = new LinkedHashSet<>();
        // add Z0 to K
        K.add(primedZero);

        boolean found = true;
        while (found) {
            // as long as no new "square" marks are found
            // add Z1 to K if
            // there is a zero Z1 which is marked with a "square " in the column of Z0
            if (zerosInColumn[j] != -1) {
                K.add(new Pair<>(zerosInColumn[j], j));
            } else {
                found = false;
            }

            // if no zero element Z1 marked with "square" exists in the column of Z0, then cancel the loop
            if (!found) {
                break;
            }

            // replace Z0 with the 0 in the row of Z1
            i = zerosInColumn[j];
            j = starredZeroesInRow[i];
            // add the new Z0 to K
            if (j != -1) {
                K.add(new Pair<>(i, j));
            } else {
                found = false;
            }
        }

        for (Pair<Integer, Integer> z : K) {
            // remove all "square" marks in K
            if (zerosInColumn[z.getSecond()] == z.getFirst()) {
                zerosInColumn[z.getSecond()] = -1;
                zerosInRow[z.getFirst()] = -1;
            }
            // replace the 0 marks in K with "square" marks
            if (starredZeroesInRow[z.getFirst()] == z.getSecond()) {
                zerosInRow[z.getFirst()] = z.getSecond();
                zerosInColumn[z.getSecond()] = z.getFirst();
            }
        }

        // remove all marks
        Arrays.fill(starredZeroesInRow, -1);
        Arrays.fill(rowIsCovered, false);
        Arrays.fill(colIsCovered, false);
    }

    /**
     * In order to simplify the steps, we do the followin:
     * 1. Find the smallest uncovered value in the matrix
     * 2. Subtract it from all uncovered values
     * 3. Add it to all twice-covered values
     */
    private void subtractMinimum() {
        int minUncoveredValue = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            if (rowIsCovered[i]) {
                continue;
            }
            for (int j = 0; j < matrix[0].length; j++) {
                if (!colIsCovered[j] && matrix[i][j] < minUncoveredValue) {
                    minUncoveredValue = matrix[i][j];
                }
            }
        }

        if (minUncoveredValue > 0) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (rowIsCovered[i] && colIsCovered[j]) {
                        matrix[i][j] += minUncoveredValue;
                    } else if (!rowIsCovered[i] && !colIsCovered[j]) {
                        matrix[i][j] -= minUncoveredValue;
                    }
                }
            }
        }
    }

    private void checkIfSquared(int[][] matrix) {
        if (matrix.length == matrix[0].length) {
            // Matrix is squared;
            this.matrix = matrix;
            return;
        }

        int max = 0;
        for (int[] ints : matrix) {
            for (int e : ints) {
                if (e > max) max = e;
            }
        }

        int rows;
        int cols;
        if (matrix.length > matrix[0].length) {
            rows = matrix.length;
            cols = matrix.length;
        } else {
            rows = matrix[0].length;
            cols = matrix[0].length;
        }

        int[][] newMatrix = new int[rows][cols];
        for (int i = 0; i < newMatrix.length; ++i) {
            for (int j = 0; j < newMatrix[0].length; ++j) {
                if (i < matrix.length && j < matrix[0].length) {
                    newMatrix[i][j] = matrix[i][j];
                } else {
                    newMatrix[i][j] = max;
                }
            }
        }

        this.matrix = newMatrix;
    }
}

package me.lluis.qaptest.algorithms;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class HungarianAlgorithm {

    private final int n; // Matrix size
    private final int[][] matrix; // Matrix computed from the BranchAndBound algorithm

    private final int[] squareInRow;
    private final int[] squareInCol;
    private final int[] rowIsCovered;
    private final int[] colIsCovered;
    private final int[] staredZeroesInRow;

    public HungarianAlgorithm(int[][] matrix) {
        this.n = matrix.length;
        if (n != matrix[0].length) {
            try {
                throw new IllegalAccessException("You must provide a square matrix.");
            } catch (IllegalAccessException ex) {
                System.exit(1);
            }
        }

        this.matrix = matrix;
        squareInRow = new int[n];    // squareInRow & squareInCol indicate the position
        squareInCol = new int[n];    // of the marked zeroes

        rowIsCovered = new int[n];   // indicates whether a row is covered
        colIsCovered = new int[n];   // indicates whether a column is covered
        staredZeroesInRow = new int[n]; // storage for the 0

        Arrays.fill(staredZeroesInRow, -1);
        Arrays.fill(squareInRow, -1);
        Arrays.fill(squareInCol, -1);
    }

    /**
     * find an optimal assignment
     *
     * @return optimal assignment
     */
    public int findOptimalAssignment() {
        reduceMatrix();
        zeroCover();    // mark independent zeroes
        checkColumnsWithZero();    // cover columns which contain a marked zero

        while (!allColumnsAreCovered()) {
            int[] mainZero = zZeroMark();
            while (mainZero == null) {  // while no zero found
                subtractMinimum();
                mainZero = zZeroMark();
            }
            if (squareInRow[mainZero[0]] == -1) {
                // there is no square mark in the mainZero line
                kChain(mainZero);
                checkColumnsWithZero();    // cover columns which contain a marked zero
            } else {
                // there is square mark in the mainZero line
                rowIsCovered[mainZero[0]] = 1;  // cover row of mainZero
                colIsCovered[squareInRow[mainZero[0]]] = 0;  // uncover column of mainZero
                subtractMinimum();
            }
        }

        int cost = 0;
        for (int i = 0; i < squareInCol.length; ++i) {
            cost += matrix[i][squareInCol[i]];
        }

        return cost;
    }

    /**
     * Check if all columns are covered. If that's the case then the
     * optimal solution is found
     *
     * @return true or false weather all columns are covered
     */
    private boolean allColumnsAreCovered() {
        for (int i : colIsCovered) {
            if (i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reduce the matrix so that in each row and column at least one zero exists:
     * 1. subtract each row minima from each element of the row
     * 2. subtract each column minima from each element of the column
     */
    private void reduceMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            int currentRowMin = Integer.MAX_VALUE;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] < currentRowMin) {
                    currentRowMin = matrix[i][j];
                }
            }

            // subtract min value from each element of the current row
            for (int k = 0; k < matrix[i].length; k++) {
                matrix[i][k] -= currentRowMin;
            }
        }

        // cols
        for (int i = 0; i < matrix.length; i++) {
            // find the min value of the current column
            int currentColMin = Integer.MAX_VALUE;
            for (int[] ints : matrix) {
                if (ints[i] < currentColMin) {
                    currentColMin = ints[i];
                }
            }
            // subtract min value from each element of the current column
            for (int k = 0; k < matrix.length; k++) {
                matrix[k][i] -= currentColMin;
            }
        }
    }

    /**
     * Mark each 0 with a "square", if there are no other marked zeroes in the same row or column
     */
    private void zeroCover() {
        boolean[] rowHasSquare = new boolean[n];
        boolean[] colHasSquare = new boolean[n];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                
                // mark if current value == 0 & there are no other marked zeroes in the same row or column
                if (matrix[i][j] == 0 && !rowHasSquare[i] && !colHasSquare[j]) {
                    rowHasSquare[i] = true;
                    colHasSquare[j] = true;
                    squareInRow[i] = j; // save the row-position of the zero
                    squareInCol[j] = i; // save the column-position of the zero
                }
            }
        }
    }

    /**
     * Cover all columns which are marked with a "square"
     */
    private void checkColumnsWithZero() {
        for (int i = 0; i < squareInCol.length; i++) {
            colIsCovered[i] = squareInCol[i] != -1 ? 1 : 0;
        }
    }

    /**
     * Find zero value Z0 and mark it as "0".
     *
     * @return position of Z0 in the matrix
     */
    private int[] zZeroMark() {
        for (int i = 0; i < matrix.length; i++) {
            if (rowIsCovered[i] == 0) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == 0 && colIsCovered[j] == 0) {
                        staredZeroesInRow[i] = j; // mark as 0*
                        return new int[]{i, j};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a chain K of alternating "squares" and "0*"
     *
     * @param mainZero => Z0
     */
    private void kChain(int[] mainZero) {
        int i;
        int j = mainZero[1];

        Set<int[]> K = new LinkedHashSet<>();
        // add Z0 to K
        K.add(mainZero);

        boolean found = true;
        while (found) {
            // as long as no new "square" marks are found
            // add Z1 to K if
            // there is a zero Z1 which is marked with a "square " in the column of Z0
            if (squareInCol[j] != -1) {
                K.add(new int[]{squareInCol[j], j});
            } else {
                found = false;
            }

            // if no zero element Z1 marked with "square" exists in the column of Z0, then cancel the loop
            if (!found) {
                break;
            }

            // replace Z0 with the 0 in the row of Z1
            i = squareInCol[j];
            j = staredZeroesInRow[i];
            // add the new Z0 to K
            if (j != -1) {
                K.add(new int[]{i, j});
            } else {
                found = false;
            }
        }

        for (int[] zero : K) {
            // remove all "square" marks in K
            if (squareInCol[zero[1]] == zero[0]) {
                squareInCol[zero[1]] = -1;
                squareInRow[zero[0]] = -1;
            }
            // replace the 0 marks in K with "square" marks
            if (staredZeroesInRow[zero[0]] == zero[1]) {
                squareInRow[zero[0]] = zero[1];
                squareInCol[zero[1]] = zero[0];
            }
        }

        // remove all marks
        Arrays.fill(staredZeroesInRow, -1);
        Arrays.fill(rowIsCovered, 0);
        Arrays.fill(colIsCovered, 0);
    }

    /**
     * 1. Find the smallest uncovered value in the matrix.
     * 2. Subtract it from all uncovered values
     * 3. Add it to all twice-covered values
     */
    private void subtractMinimum() {
        // Find the smallest uncovered value in the matrix
        int minUncoveredValue = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            if (rowIsCovered[i] == 1) {
                continue;
            }
            for (int j = 0; j < matrix[0].length; j++) {
                if (colIsCovered[j] == 0 && matrix[i][j] < minUncoveredValue) {
                    minUncoveredValue = matrix[i][j];
                }
            }
        }

        if (minUncoveredValue > 0) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    if (rowIsCovered[i] == 1 && colIsCovered[j] == 1) {
                        // Add min to all twice-covered values
                        matrix[i][j] += minUncoveredValue;
                    } else if (rowIsCovered[i] == 0 && colIsCovered[j] == 0) {
                        // Subtract min from all uncovered values
                        matrix[i][j] -= minUncoveredValue;
                    }
                }
            }
        }
    }
}

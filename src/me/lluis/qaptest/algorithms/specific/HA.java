package me.lluis.qaptest.algorithms.specific;

import me.lluis.qaptest.util.Utils;

import java.util.Arrays;

public class HA {

    private final int n;
    private final int[][] matrix; // Rows represent the facilities, columns represent the locations
    private final boolean[] isRowMarked;
    private final boolean[] isColMarked;
    private boolean[] rowIsCovered;
    private boolean[] colIsCovered;

    private int[] zeroInCoveredRow;
    private int[] zeroInCoveredCol;

    private int rowsCovered = 0;

    public HA(int n, int[][] matrix) {
        this.matrix = makeSquared(n, matrix);
        this.n = this.matrix.length;
        this.isRowMarked = new boolean[n];
        this.isColMarked = new boolean[n];
        this.rowIsCovered = new boolean[n];
        this.colIsCovered = new boolean[n];

        this.zeroInCoveredRow = new int[n];
        this.zeroInCoveredCol = new int[n];

        Arrays.fill(zeroInCoveredRow, -1);
        Arrays.fill(zeroInCoveredCol, -1);
    }

    public void solve() {
        coverZeros();
        //reduceRowsAndColumns();

        /*Utils.printMatrix(matrix);

        while (coverZeros() != n) {
            reduceByMinimumUncovered();
            Utils.printMatrix(matrix);
        }*/

    }

    /**
     * Reduces each row of the matrix by its minimum value
     */
    private void reduceRowsAndColumns() {
        for (int i = 0; i < n; ++i) {
            int min = Integer.MAX_VALUE;

            for (int j = 0; j < n; ++j) {
                if (matrix[i][j] < min) min = matrix[i][j];
            }

            for (int j = 0; j < n; ++j) {
                matrix[i][j] -= min;
            }
        }

        for (int j = 0; j < n; ++j) {
            int min = Integer.MAX_VALUE;

            for (int i = 0; i < n; ++i) {
                if (matrix[i][j] < min) min = matrix[i][j];
                System.out.println(min + " " + matrix[i][j]);
            }

            for (int i = 0; i < n; ++i) {
                matrix[i][j] -= min;
            }
        }
    }

    private boolean checkIfAllIsCovered() {
        for (int i = 0; i < n; ++i) {
            if (!rowIsCovered[i] && !rowIsCovered[i]) return false;
        }
        return true;
    }

    private int coverZeros() {
        boolean[] rowHasZero = new boolean[n];
        boolean[] colHasZero = new boolean[n];
        int[] zeroInRow = new int[n];
        int[] zeroInCol = new int[n];
        Arrays.fill(zeroInRow, -1);
        Arrays.fill(zeroInCol, -1);
        recursiveCoverZeros(rowHasZero, colHasZero, 0, 0, zeroInRow, zeroInCol);

        for (int i = 0; i < n; ++i) {
            if (!rowIsCovered[i]) {
                isRowMarked[i] = true; // Rows that are not covered are marked
            }
        }

        System.out.println("Marked rows: " + Arrays.toString(isRowMarked));
        System.out.println("Zero i " + Arrays.toString(zeroInCoveredRow));
        System.out.println("Zero j " + Arrays.toString(zeroInCoveredCol));

        int min = Integer.MAX_VALUE;
        boolean first = true;
        while (first) {
            first = false;

            for (int i = 0; i < n; ++i) {
                if (isRowMarked[i]) {
                    for (int j = 0; j < n; ++j) {
                        if (matrix[j][i] == 0 && colIsCovered[j]) {
                            isColMarked[j] = true; // Columns with zero covered by row are marked
                            break;
                        }
                    }
                }
            }

            for (int j = 0; j < n; ++j) {
                if (isColMarked[j]) {
                    for (int i = 0; i < n; ++i) {
                        if (matrix[i][j] == 0 && rowIsCovered[i]) {
                            isRowMarked[i] = true;
                        }
                    }
                }
            }

            int count = 0;
            for (int i = 0; i < n; ++i) {
                if (!isRowMarked[i]) ++count;
                if (isColMarked[i]) ++count;
            }

            if (count < min) min = count;
        }

        System.out.println("Rows not covered: " + Arrays.toString(isRowMarked));
        System.out.println("Columns with zero covered by row: " + Arrays.toString(isColMarked));
        System.out.println(min);
        return min;
    }

    private boolean freeZeros(boolean[] isRowCovered, boolean[] isColCovered) {
        System.out.println("Rows: " + Arrays.toString(isRowCovered));
        System.out.println("Cols: " + Arrays.toString(isColCovered));
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (matrix[i][j] == 0) {
                    if (!isRowCovered[i] && !isColCovered[j]) return true;
                }
            }
        }

        return false;
    }

    private void recursiveCoverZeros(boolean[] rowHasZero, boolean[] colHasZero, int row, int col, int[] zeroInRow, int[] zeroInCol) {
        if (row == n - 1) {
            int count = 0;
            for (int i = 0; i < n; ++i) {
                if (rowHasZero[i]) ++count;
            }

            if (count > rowsCovered) {
                rowIsCovered = rowHasZero.clone();
                colIsCovered = colHasZero.clone();
                zeroInCoveredRow = zeroInRow.clone();
                zeroInCoveredCol = zeroInCol.clone();
                rowsCovered = count; // Variable global
            }

            return;
        }

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (colHasZero[j]) continue;

                if (!rowHasZero[i]) {
                    if (matrix[i][j] == 0) {
                        rowHasZero[i] = true;
                        colHasZero[j] = true;
                        zeroInRow[row] = j;
                        zeroInCol[col] = i;
                        recursiveCoverZeros(rowHasZero, colHasZero, row + 1, col + 1, zeroInRow, zeroInCol);
                        zeroInRow[row] = -1;
                        zeroInCol[col] = -1;
                        rowHasZero[i] = false;
                        colHasZero[j] = false;
                    }
                }
            }
        }
    }

    private void reduceByMinimumUncovered() {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (!isRowMarked[i] && !isColMarked[j]) {
                    if (matrix[i][j] < min) min = matrix[i][j];
                }
            }
        }

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (isRowMarked[i]) {
                    matrix[i][j] += min;
                }

                if (isColMarked[j]) {
                    matrix[i][j] += min;
                }
            }
        }

        min = Integer.MAX_VALUE;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (matrix[i][j] < min) min = matrix[i][j];
            }
        }

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                matrix[i][j] -= min;
            }
        }
    }

    private int[][] makeSquared(int n, int[][] matrix) {
        if (n == matrix.length && n == matrix[0].length) return matrix; // Already squared

        int size = Math.max(n, matrix[0].length);
        int[][] newMatrix = new int[size][size];

        // Let's find the maximum value in the matrix
        int max = 0;
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix[0].length; ++j) {
                int v = ints[j];
                if (v > max) max = v;
            }
        }

        // Let's fill the new matrix
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i < matrix.length && j < matrix[0].length) {
                    newMatrix[i][j] = matrix[i][j];
                } else {
                    newMatrix[i][j] = max;
                }
            }
        }

        return newMatrix;
    }
}

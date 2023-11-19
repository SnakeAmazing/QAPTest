package me.lluis.qaptest.algorithms.specific;

import java.util.Arrays;

public class HungarianAlgorithm {

    private final int n;
    private final int[][] matrix; // Rows represent the facilities, columns represent the locations
    private final int[][] ogMatrix;

    private final boolean[] isRowCovered;
    private final boolean[] isColCovered;

    // 0 represents unmarked, 1 represents starred, 2 represents primed
    private final int[][] starredAndPrimedZeros;
    private final int[] lastZero = new int[2];

    private int[][] bestAssigment;
    private int bestCost;

    public HungarianAlgorithm(int n, int[][] matrix) {
        this.ogMatrix = matrix;
        this.matrix = makeSquared(n, matrix);
        this.n = this.matrix.length;

        this.isRowCovered = new boolean[n];
        this.isColCovered = new boolean[n];
        this.starredAndPrimedZeros = new int[n][n];

        this.bestCost = -1;
    }

    public void solve() {
        this.bestAssigment = new int[n][2];
        int step = 1;

        boolean done = false;
        while (!done) {
            //System.out.println("New iteration " + step);
            //try {Thread.sleep(1000);} catch (Exception ignored) {}
            switch (step) {
                case 1:
                    reduceRows();
                    step = 2;
                    break;
                case 2:
                    starZeros();
                    step = 3;
                    break;
                case 3:
                    step = coverColumns();
                    break;
                case 4:
                    step = step4(step);
                    break;
                case 5:
                    step = step5();
                    break;
                case 6:
                    step = subtractMinimum();
                    break;

                default:
                    done = true;
            }
        }

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (starredAndPrimedZeros[i][j] == 1) {
                    bestAssigment[i] = new int[]{i, j};
                }
            }
        }
    }

    public int[][] getBestAssigment() {
        return bestAssigment;
    }

    public int getBestCost() {
        if (bestAssigment == null) solve();
        if (bestCost != -1) return bestCost;

        bestCost = 0;
        for (int i = 0; i < ogMatrix.length; ++i) {
            bestCost += ogMatrix[bestAssigment[i][0]][bestAssigment[i][1]];
        }

        return bestCost;
    }

    /**
     * Step 1
     */
    private void reduceRows() {
        for (int i = 0; i < n; ++i) {
            int min = Integer.MAX_VALUE;
            for (int j = 0; j < n; ++j) {
                int elem = matrix[i][j];
                if (elem < min) min = elem;
            }

            for (int j = 0; j < n; ++j) {
                matrix[i][j] -= min;
            }
        }
    }

    // Step 1
    private void reduceColumns() {
        for (int j = 0; j < n; ++j) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; ++i) {
                int elem = matrix[i][j];
                if (elem < min) min = elem;
            }

            for (int i = 0; i < n; ++i) {
                matrix[i][j] -= min;
            }
        }
    }

    // Step 2
    private void starZeros() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (matrix[i][j] == 0 && !isRowCovered[i] && !isColCovered[j]) {
                    starredAndPrimedZeros[i][j] = 1;
                    isRowCovered[i] = true;
                    isColCovered[j] = true;
                }
            }
        }

        Arrays.fill(isRowCovered, false);
        Arrays.fill(isColCovered, false);
    }


    // Step 3
    private int coverColumns() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (starredAndPrimedZeros[i][j] == 1) {
                    isColCovered[j] = true;
                }
            }
        }

        int covered = 0;
        for (int i = 0; i < n; ++i) {
            if (isColCovered[i]) ++covered;
        }

        if (covered >= n) {
            return 7; // Done
        } else {
            return 4; // Step 4
        }
    }

    private int step4(int step) {
        boolean done = false;

        while (!done) {
            int[] primedZero = primeZero(); // Get an uncovered 0 and prime it
            if (primedZero[0] == -1) { // All zeros are covered
                done = true;
                step = 6; // Step 6
            } else {
                int i = primedZero[0];
                int j = primedZero[1];
                starredAndPrimedZeros[i][j] = 2; // Mark as primed

                int starredInRow = findStarredInRow(i);
                if (starredInRow != -1) { // There's a starred zero in the row of the primed zero
                    isRowCovered[i] = true;
                    isColCovered[starredInRow] = false;
                } else {
                    lastZero[0] = i;
                    lastZero[1] = j;
                    done = true;

                    step = 5; // Step 5
                }
            }
        }

        return step;
    }

    private int[] primeZero() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (!isRowCovered[i] && !isColCovered[j] && matrix[i][j] == 0) {
                    return new int[] {i, j};
                }
            }
        }

        return new int[] {-1, -1};
    }

    private int findStarredInRow(int row) {
        int c = -1;
        for (int j = 0; j < n; ++j) {
            if (starredAndPrimedZeros[row][j] == 1) {
                c = j;
            }
        }

        return c;
    }

    private int findStarredInCol(int col) {
        int r = -1;
        for (int i = 0; i < n; ++i) {
            if (starredAndPrimedZeros[i][col] == 1) {
                r = i;
            }
        }

        return r;
    }

    private int findPrimedInRow(int row) {
        int c = -1;
        for (int j = 0; j < n; ++j) {
            if (starredAndPrimedZeros[row][j] == 2) {
                c = j;
            }
        }

        return c;
    }

    // Step 5 - Pathing
    private int step5() {
        int[][] path = new int[n * n][2];
        int count = 0;
        path[count][0] = lastZero[0];
        path[count][1] = lastZero[1];

        boolean done = false;
        while (!done) {
            int i = findStarredInCol(path[count][1]);
            if (i >= 0) {
                // There's a starred zero in the same column
                // Let's find a starred zero in the row
                ++count;
                path[count][0] = i;
                path[count][1] = path[count - 1][1];
            } else {
                done = true;
            }

            if (!done) {
                // There's a starred zero in the same row
                int j = findPrimedInRow(path[count][0]);
                ++count;
                path[count][0] = path[count - 1][0];
                path[count][1] = j;
            }
        }

        convertPaths(path, count);
        unprimeAndUncover();

        return 3;
    }

    private void convertPaths(int[][] path, int count) {
        for (int i = 0; i <= count; ++i) {
            int r = path[i][0];
            int c = path[i][1];
            if (starredAndPrimedZeros[r][c] == 1) {
                starredAndPrimedZeros[r][c] = 0;
            } else {
                starredAndPrimedZeros[r][c] = 1;
            }
        }
    }

    private void unprimeAndUncover() {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (starredAndPrimedZeros[i][j] == 2) {
                    starredAndPrimedZeros[i][j] = 0;
                }
            }
        }

        Arrays.fill(isRowCovered, false);
        Arrays.fill(isColCovered, false);
    }

    /**
     * In order to simplify the steps, we do the followin:
     * 1. Find the smallest uncovered value in the matrix
     * 2. Subtract it from all uncovered values
     * 3. Add it to all twice-covered values
     */
    private int subtractMinimum() {
        int minUncoveredValue = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (!isRowCovered[i] && !isColCovered[j] && matrix[i][j] < minUncoveredValue) {
                    minUncoveredValue = matrix[i][j];
                }
            }
        }

        if (minUncoveredValue <= 0) return 4;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (isRowCovered[i]) {
                    matrix[i][j] += minUncoveredValue;
                }

                if (!isColCovered[j]) {
                    matrix[i][j] -= minUncoveredValue;
                }
            }
        }

        return 4;
    }

    private int[][] makeSquared(int n, int[][] matrix) {
        if (n == matrix.length && n == matrix[0].length) {
            // Return a copy of the matrix
            int[][] m = new int[n][n];
            for (int i = 0; i < n; ++i) {
                m[i] = Arrays.copyOf(matrix[i], n);
            }

            return m; // Already squared
        }

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

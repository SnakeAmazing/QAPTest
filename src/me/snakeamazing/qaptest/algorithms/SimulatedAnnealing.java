package me.snakeamazing.qaptest.algorithms;

import me.snakeamazing.qaptest.object.CharPair;

import java.util.*;

import java.lang.Math;

public class SimulatedAnnealing {

    private Map<String, Integer> freq;
    private List<Character> alphabet;
    private int[][] flowMatrix;
    private Map<CharPair, Integer> pairFrequencies;
    private final int rows;
    private final int cols;
    private char[] bestAssignment;

    public SimulatedAnnealing(int rows, int cols, Map<String, Integer> freq, List<Character> letters)  {
        this.rows = rows;
        this.cols = cols;
        this.freq = freq;
        this.alphabet = letters;
        this.pairFrequencies = new HashMap<>();
    }

    public void solve() {
        computeFrequencies();
        char [][] keyboard = initKeyboard(rows, cols);
        int t = 1;
        double temp = 10;
        int tope = 10000;
        Random rand = new Random();
        while (temp > 1) {
            char[][] est = clone(keyboard);
            for (int i = 0; i < tope; ++i) {
                int x1 = rand.nextInt(rows);
                int y1 = rand.nextInt(cols);
                int x2 = rand.nextInt(rows);
                int y2 = rand.nextInt(cols);
                char[][] sig = swap(keyboard, x1, y1, x2, y2);
                double difEng = coste(sig) - coste(est);
                if (difEng < 0) {
                    est = clone(sig);
                }
                else {
                    double q = Math.min(1, Math.exp(-difEng / temp));
                    if (rand.nextDouble() < q) est = clone(sig);
                }
            }
            keyboard = clone(est);
            temp = 6/Math.log(1+t);
            ++t;
        }

        char[] best = new char[rows*cols];
        int index = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols ; ++j) {
                System.out.print(keyboard[i][j] + " ");
                best[index] = keyboard[i][j];
                ++index;
            }
            System.out.println();
        }
        System.out.println(coste(keyboard));
        bestAssignment = best.clone();
    }

    public char[] getCurrentBestAssignment() {
        return bestAssignment;
    }

    private char[][] initKeyboard(int row, int col) {
        char[][] c = new char[row][col];
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                if ((i*cols+j) < alphabet.size()) {
                    c[i][j] = alphabet.get(i*cols+j);
                } else {
                    c[i][j] = ' ';
                }

            }
        }
        return c;
    }

    private char[][] swap(char[][] c, int i1, int j1, int i2, int j2) {
        char[][] clone = clone(c);
        char aux = clone[i1][j1];
        clone[i1][j1] = clone[i2][j2];
        clone[i2][j2] = aux;
        return clone;
    }

    private double coste(char[][] c) {
        double cost = 0;
        double[][] distanceMatrix = createDistanceMatrix(c);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                cost +=  (flowMatrix[i][j] * distanceMatrix[i][j]);
            }
        }
        return cost;
    }

    private void computeFrequencies() {
        flowMatrix = new int[alphabet.size()][alphabet.size()];
        for (String word : freq.keySet()) {
            for (int i = 0; i < word.length() - 1; ++i) {
                char c = word.charAt(i);
                char c2 = word.charAt(i + 1);
                int f = freq.get(word);

                if (c == c2) continue;

                CharPair pair = new CharPair(c, c2);
                if (pairFrequencies.containsKey(pair)) {
                    pairFrequencies.put(pair, pairFrequencies.get(pair) + f);
                } else {
                    pairFrequencies.put(pair, f);
                }
            }
        }

        for (CharPair pair : pairFrequencies.keySet()) {
            int freq = pairFrequencies.get(pair);
            System.out.println("c1: "+pair.getFirst()+" i: "+(pair.getFirst() - 'A')+" c2: "+pair.getSecond()+" j: "+(pair.getSecond() - 'A'));
            flowMatrix[pair.getFirst() - 'A'][pair.getSecond() - 'A'] = freq;
            flowMatrix[pair.getSecond() - 'A'][pair.getFirst() - 'A'] = freq;
        }

    }

    private double[][] createDistanceMatrix(char[][] c) {
        int n = alphabet.size();
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < rows; ++i) {
            int x1 = i/cols;
            int y1 = i % cols;
            for (int j = 0; j < cols; ++j) {
                int x2 = j/cols;
                int y2 = j % cols;
                char a = c[x1][y1];
                char b = c[x2][y2];
                if (a == ' ' || b == ' ') continue;
                double euclideanDistance = Math.sqrt(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2));
                distanceMatrix[a-'A'][b-'A'] = euclideanDistance;
            }
        }
        return distanceMatrix;
    }

    private char[][] clone(char[][] c) {
        int n = c.length;
        int m = c[0].length;
        char[][] clone = new char[n][m];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                clone[i][j] = c[i][j];
            }
        }
        return clone;
    }

}

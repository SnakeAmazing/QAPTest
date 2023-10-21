package me.lluis.qaptest.keyboard;

import java.util.Map;

public class Keyboard {

    private final int[][] layout;
    private double calculatedCost;

    public Keyboard(int q, int p) {
        layout = new int[q][p];
        for (int i = 0; i < q; ++i) {
            for (int j = 0; j < p; ++j) {
                layout[i][j] = -1;
            }
        }
    }

    public Keyboard(Keyboard keyboard) {
        int[][] tmp = keyboard.getLayout();
        layout = new int[tmp.length][tmp[0].length];

        for (int i = 0; i < tmp.length; ++i) {
            System.arraycopy(tmp[i], 0, layout[i], 0, tmp[i].length);
        }

        calculatedCost = keyboard.getCost();
    }

    public void put(int i, int j, int value) {
        layout[i][j] = value;
    }

    public char getChar(int i, int j) {
        return (char) layout[i][j];
    }

    public int get(int i, int j) {
        return layout[i][j];
    }

    public int[][] getLayout() {
        return layout;
    }

    public double getCost() {
        return calculatedCost;
    }

    public void setCost(double cost) {
        this.calculatedCost = cost;
    }

    public void calculateCost(Map<String, Integer> frequencies) {
        double cost = 0.0;

        for (String word : frequencies.keySet()) {
            cost += frequencies.get(word) * calculateWordDistance(word);
        }

        calculatedCost = cost / frequencies.size();
    }

    public void print() {
        for (int i = 0; i < layout.length; ++i) {
            for (int j = 0; j < layout[0].length; ++j) {
                if (layout[i][j] == -1) System.out.print("-");
                else System.out.print((char) layout[i][j]);
            }
            System.out.println();
        }
    }

    private double calculateWordDistance(String word) {
        double distance = 0.0;
        for (int i = 0; i < word.length() - 1; ++i) {
            char c1 = word.charAt(i);
            char c2 = word.charAt(i + 1);

            if (c1 == c2) continue;

            int[] pos1 = findChar(c1);
            int[] pos2 = findChar(c2);

            if (pos1 == null || pos2 == null) throw new RuntimeException("Character not found");

            distance += Math.sqrt(Math.pow(pos1[0] - pos2[0], 2) + Math.pow(pos1[1] - pos2[1], 2));
        }

        return distance;
    }

    private int[] findChar(char c) {
        for (int i = 0; i < layout.length; ++i) {
            for (int j = 0; j < layout[0].length; ++j) {
                if (layout[i][j] == c) return new int[] {i, j};
            }
        }

        return null;
    }
}

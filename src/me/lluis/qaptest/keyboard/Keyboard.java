package me.lluis.qaptest.keyboard;

import java.util.Map;

public class Keyboard {

    private int[][] layout;
    private double calculatedCost;

    public Keyboard(int q, int p) {
        layout = new int[q][p];
        for (int i = 0; i < q; ++i) {
            for (int j = 0; j < p; ++j) {
                layout[i][j] = -1;
            }
        }
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

    public double calculateCost(Map<Map.Entry<Character, Character>, Integer> frequencies) {
        double cost = 0.0;

        for (int i = 0; i < layout.length; ++i) {
            for (int j = 0; j < layout[i].length; ++j) {
                if (layout[i][j] == -1) continue;

                for (int k = 0; k < layout.length; ++k) {
                    for (int l = 0; l < layout[k].length; ++l) {
                        if (layout[k][l] == -1) continue;

                        char c1 = (char) layout[i][j];
                        char c2 = (char) layout[k][l];

                        int freq = frequencies.getOrDefault(Map.entry(c1, c2), 1);

                        if (c1 == c2) {
                            cost += 0.142 * freq;
                        };

                        double distance = Math.sqrt(Math.pow(i - k, 2) + Math.pow(j - l, 2));
                        cost  += (double) freq * 1/4.9 * Math.log(distance + 1); // Fitts' law
                    }
                }
            }
        }
        calculatedCost = cost;
        return cost;
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
}

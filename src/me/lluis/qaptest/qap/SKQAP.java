package me.lluis.qaptest.qap;

import me.lluis.qaptest.keyboard.Keyboard;
import me.lluis.qaptest.object.CharPair;

import java.util.*;

public class SKQAP {

    private static final List<Character> LETTERS = Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    );

    private final char[][] QWERTY = {
            {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'},
            {'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Ñ'},
            {'Z', 'X', 'C', 'V', 'B', 'N', 'M'}
    };

    private final Random random = new Random();

    private final int p;
    private final int q;

    private final Map<String, Integer> wordFrequencies;
    private final Map<Character, Integer> letterFrequencies; // Flows is equivalent to the frequency between a pair of letters

    private int[][] distances; // Distances between letters

    private int cost;

    public SKQAP(int p, int q, Map<String, Integer> wordFrequencies, Map<Character, Integer> letterFrequencies) {
        this.p = p;
        this.q = q;

        this.wordFrequencies = wordFrequencies;
        this.letterFrequencies = letterFrequencies;
    }

    public void solve() {

    }

    private void branchAndBound(int[] currentSol, int currentCost, List<Character> letters, int i) {
        if (letters.isEmpty()) {
            // Base case
            return;
        }

        for (char c : letters) {
            currentSol[i] = c;

        }
    }

    private void print(char[][] layout) {
        for (int i = 0; i < p; ++i) {
            for (int j = 0; j < q; ++j) {
                System.out.print(layout[i][j] + " ");
            }
            System.out.println();
        }
    }
}

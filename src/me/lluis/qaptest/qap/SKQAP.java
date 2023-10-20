package me.lluis.qaptest.qap;

import me.lluis.qaptest.keyboard.Keyboard;

import java.util.*;

public class SKQAP {

    private static final List<Character> LETTERS = Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'L', 'M', 'N', 'Ñ', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    );

    private final Random random = new Random();

    private final int restarts;
    private final int iterations;

    private final int p;
    private final int q;

    private final Map<Map.Entry<Character, Character>, Integer> frequencies;

    public SKQAP(int restarts, int iterations, int p, int q, Map<Map.Entry<Character, Character>, Integer> frequencies) {
        this.restarts = restarts;
        this.iterations = iterations;

        this.p = p;
        this.q = q;

        this.frequencies = frequencies;
    }

    public void solve() {
        for (int r = 0; r < restarts; ++r) {
            Keyboard keyboard = generateKeyboard();

            for (int it = 0; it < iterations; ++it) {
                permutation(keyboard);
            }

            System.out.println("Restart " + (r + 1));
            System.out.println("Cost: " + keyboard.getCost());
            keyboard.print();
        }

    }

    private Keyboard generateKeyboard() {
        List<Character> letters = new ArrayList<>(LETTERS);
        Keyboard keyboard = new Keyboard(p, q);

        int lc = p/2;
        int rc = q/2;
        keyboard.put(lc, rc, ' ');

        char c1 = letters.remove(random.nextInt(letters.size()));
        char c2 = letters.remove(random.nextInt(letters.size()));
        char c3 = letters.remove(random.nextInt(letters.size()));
        char c4 = letters.remove(random.nextInt(letters.size()));

        keyboard.put(lc - 1, rc, c1);
        keyboard.put(lc + 1, rc, c2);
        keyboard.put(lc, rc - 1, c3);
        keyboard.put(lc, rc + 1, c4);

        while (!letters.isEmpty()) {
            double minCost = Double.MAX_VALUE;
            int bestI = -1;
            int bestJ = -1;
            char bestC = ' ';
            for (int i = 0; i < p; ++i) {
                for (int j = 0; j < q; ++j) {
                    for (char c : letters) {
                        if (keyboard.get(i, j) != -1) continue;
                        keyboard.put(i, j, c);
                        double cost = keyboard.calculateCost(frequencies);
                        if (cost < minCost) {
                            bestI = i;
                            bestJ = j;
                            bestC = c;
                            minCost = cost;
                        }
                        keyboard.put(i, j, -1);
                    }
                }
            }

            keyboard.put(bestI, bestJ, bestC);
            letters.remove(Character.valueOf(bestC));
        }

        return keyboard;
    }

    private void permutation(Keyboard keyboard) {
        double cost = keyboard.getCost();

        int i = random.nextInt(p);
        int j = random.nextInt(q);

        int k = random.nextInt(p);
        int l = random.nextInt(q);

        int c1 = keyboard.get(i, j);
        int c2 = keyboard.get(k, l);

        keyboard.put(i, j, c2);
        keyboard.put(k, l, c1);

        double newCost = keyboard.calculateCost(frequencies);

        if (newCost < cost) return; // We keep the changes made.

        // We don't want the changes.
        keyboard.put(i, j, c1);
        keyboard.put(k, l, c2);
    }
}

package me.lluis.qaptest.qap;

import me.lluis.qaptest.keyboard.Keyboard;

import java.util.*;

public class SKQAP {

    private static final List<Character> LETTERS = Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    );

    private final Random random = new Random();

    private final int restarts;
    private final int iterations;

    private final int p;
    private final int q;

    private final Map<String, Integer> frequencies;

    public SKQAP(int restarts, int iterations, int p, int q, Map<String, Integer> frequencies) {
        this.restarts = restarts;
        this.iterations = iterations;

        this.p = p;
        this.q = q;

        this.frequencies = frequencies;
    }

    public void solve() {
        for (int r = 0; r < restarts; ++r) {
            Keyboard keyboard = generateKeyboard();

            double currCost = keyboard.getCost();

            int it = 0;
            while (it < iterations) {
                Keyboard tmp = new Keyboard(keyboard);
                permutation(tmp);

                if (tmp.getCost() < currCost) {
                    keyboard = tmp;
                    currCost = keyboard.getCost();
                    it = 0;
                }

                perturbation(keyboard);
                it++;
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
            int i = random.nextInt(p);
            int j = random.nextInt(q);

            if (keyboard.get(i, j) != -1) continue;

            char c = letters.remove(random.nextInt(letters.size()));
            keyboard.put(i, j, c);
        }

        keyboard.calculateCost(frequencies);
        return keyboard;
    }

    private void permutation(Keyboard keyboard) {
        int i = random.nextInt(p);
        int j = random.nextInt(q);

        while (keyboard.get(i, j) == -1) {
            i = random.nextInt(p);
            j = random.nextInt(q);
        }

        int k = random.nextInt(p);
        int l = random.nextInt(q);

        while (keyboard.get(k, l) == -1) {
            k = random.nextInt(p);
            l = random.nextInt(q);
        }

        swap(keyboard, i, j, k, l);

        keyboard.calculateCost(frequencies);
    }

    private void swap(Keyboard keyboard, int i, int j, int k, int l) {
        int c1 = keyboard.get(i, j);
        int c2 = keyboard.get(k, l);

        keyboard.put(i, j, c2);
        keyboard.put(k, l, c1);
    }

    private void perturbation(Keyboard keyboard) {
        //int op = random.nextInt(2); Make more perturbations

        // Between 3 and 5 swaps
        //int howMany = random.nextInt(3) + 3;

        // Cyclic swaps: (i, j) -> (j, k) -> (k, i)

        int i = random.nextInt(p);
        int j = random.nextInt(q);

        while (keyboard.get(i, j) == -1) {
            i = random.nextInt(p);
            j = random.nextInt(q);
        }

        int c = keyboard.get(i, j);

        int k = random.nextInt(p);
        int tmp = keyboard.get(j, k);

        while (tmp == -1) {
            k = random.nextInt(p);
            tmp = keyboard.get(j, k);
        }
        keyboard.put(j, k, c);

        c = tmp;
        tmp = keyboard.get(k, i);
        keyboard.put(k, i, c);

        keyboard.put(i, j, tmp);
    }

    /*
            while (howMany > 0) {
            int i = random.nextInt(p); // We take a random position
            int j = random.nextInt(q);

            if (first) {
                firstI = i; // We store the first random position in order to update it at the end
                firstJ = j;
                first = false;
            }

            c = keyboard.get(i, j); // We store the character in that position

            int k = random.nextInt(p); // We take another random position
            int tmp = keyboard.get(j, k);
            keyboard.put(j, k, c); // We put the character in the new position
            c = tmp;

            howMany--;
        }
     */



    /*
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
    keyboard.setCost(minCost);
     */
}

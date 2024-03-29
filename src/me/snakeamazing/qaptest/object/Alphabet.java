package me.snakeamazing.qaptest.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Alphabet {

    private static final List<Character> BASE_ALPHABET = Arrays.asList(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    );

    private static final List<Character> REDUCED = Arrays.asList(
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
        'J', 'K', 'L', 'M'
    ); // 12

    public static List<Character> latinAlphabet() {
        return new ArrayList<>(BASE_ALPHABET);
    }

    public static List<Character> reduced() {
        return new ArrayList<>(REDUCED);
    }

    public static List<Character> spanishAlphabet() {
        List<Character> temp = new ArrayList<>(BASE_ALPHABET);
        temp.add('Ñ');
        temp.add('Ç');

        return temp;
    }
}

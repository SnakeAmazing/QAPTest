package me.lluis.qaptest.object;

public class CharPair extends Pair<Character, Character> {

    public CharPair(Character first, Character second) {
        super(first, second);
    }

    public CharPair(String pair) {
        super(pair.charAt(0), pair.charAt(1));
    }


}

package me.snakeamazing.qaptest.object;

import java.util.Objects;

public class CharPair {

    private char first;
    private char second;

    public CharPair(char first, char second) {
        if (first >= second) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }

    }

    public char getFirst() {
        return first;
    }

    public char getSecond() {
        return second;
    }

    public void setFirst(char first) {
        this.first = first;
    }

    public void setSecond(char second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CharPair)) return false;

        CharPair pair = (CharPair) obj;

        return (pair.getFirst() == first && pair.getSecond() == second)
                || (pair.getFirst() == second && pair.getSecond() == first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

package me.lluis.qaptest.object;

import java.util.Objects;

public class Pair<T, U> {

    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Pair)) return false;

        Pair<T, U> pair = (Pair<T, U>) obj;

        return (first.equals(pair.first) && second.equals(pair.second)) ||
                (first.equals(pair.second) && second.equals(pair.first));

    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
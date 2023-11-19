package me.lluis.qaptest.util;

import java.util.Arrays;

public final class Utils {

    public static void print(int[][] matrix) {
        Integer[][] integers = new Integer[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; ++i) {
            integers[i] = Arrays.stream(matrix[i]).boxed().toArray(Integer[]::new);
        }

        print(integers);
    }

    public static void print(int[] array) {
        Integer[] integers = Arrays.stream(array).boxed().toArray(Integer[]::new);

        print(integers);
    }

    public static void print(boolean[] array) {
        Boolean[] booleans = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            booleans[i] = array[i];
        }

        print(booleans);
    }

    private static <T> void print(T[][] t) {
        for (T[] row : t) {
            print(row);
        }
    }

    private static <T> void print(T[] t) {
        for (T value : t) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}

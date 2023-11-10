package me.lluis.qaptest.vector;

public class Vector {
    /**
     * The first value of the vector
     */
    private final double x;
    /**
     * The second value of the vector
     */
    private final double y;

    /**
     * Constructor which initializes a vector with given values
     *
     * @param x the first value of the vector
     * @param y the second value of the vector
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return first value of the vector
     */
    public double getX() {
        return x;
    }

    /**
     * @return second value of the vector
     */
    public double getY() {
        return y;
    }

    /**
     * Calculates the Euclidean distance between two vectors
     *
     * @param a the first vector
     * @param b the second vector
     * @return the Euclidean distance between two vectors
     */
    public static int calculateDistance(Vector a, Vector b) {
        double x = Math.pow(b.getX() - a.getX(), 2);
        double y = Math.pow(b.getY() - a.getY(), 2);

        return (int) (Math.round(Math.sqrt(x + y) * 100.0) / 100.0);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

}

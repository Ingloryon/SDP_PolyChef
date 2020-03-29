package ch.epfl.polychef.utils;

public final class Preconditions {
    //private Preconditions() {}

    public static void checkArgument(boolean b, String log) {
        if (! b) {
            throw new IllegalArgumentException(log);
        }
    }

    public static void checkArgument(boolean b) {
        if (! b) {
            throw new IllegalArgumentException();
        }
    }

    public static int checkIndex(int index, int size) {
        if (! (0 <= index && index < size)) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }
}
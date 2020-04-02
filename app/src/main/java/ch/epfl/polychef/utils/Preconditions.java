package ch.epfl.polychef.utils;

public final class Preconditions {
    //private Preconditions() {}

    public static void checkArgument(boolean bool, String log) {
        if (! bool) {
            throw new IllegalArgumentException(log);
        }
    }

    public static void checkArgument(boolean bool) {
        if (! bool) {
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
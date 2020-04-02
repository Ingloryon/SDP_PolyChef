package ch.epfl.polychef.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import ch.epfl.polychef.utils.Preconditions;

/**
 * Class that represent either {@code left}, {@code right} or is {@code none}.
 * <p>This class does not use java's {@code Optional} internally because it is intended to be uploaded to Firebase and Optionals cannot be serialized</p>
 * <p>Thus the absence of element is represented by {@code null}</p>
 *
 * @param <L> type of the left element
 * @param <R> type of the right element
 */
public final class Either<L, R> implements Serializable {

    private final L left;
    private final R right;

    public Either() {
        this(null, null);
    }

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Create a new {@code Either} defined left with value {@code val}.
     *
     * @param val the left value
     * @param <L> type of the left part of the {@code Either}
     * @param <R> type of the right part of the {@code Either}
     * @return the new left defined {@code Either}
     */
    public static <L, R> Either<L, R> left(@NonNull L val) {
        Preconditions.checkArgument(val != null, "Left value cannot be null");
        return new Either<L, R>(val, null);
    }

    /**
     * Create a new {@code Either} defined right with value {@code val}.
     *
     * @param val the right value
     * @param <L> type of the left part of the {@code Either}
     * @param <R> type of the right part of the {@code Either}
     * @return the new right defined {@code Either}
     */
    public static <L, R> Either<L, R> right(@NonNull R val) {
        Preconditions.checkArgument(val != null, "Right value cannot be null");
        return new Either<L, R>(null, val);
    }

    /**
     * Create a new {@code Either} defined as a none (not left nor right).
     *
     * @param <L> type of the left part of the {@code Either}
     * @param <R> type of the right part of the {@code Either}
     * @return the new none defined {@code Either}
     */
    public static <L, R> Either<L, R> none() {
        return new Either<L, R>(null, null);
    }

    /**
     * Return whether this is a left {@code Either}.
     *
     * @return whether this is a left {@code Either}
     */
    @Exclude
    public boolean isLeft() {
        return left != null;
    }

    /**
     * Return the left {@code Either} or null if it not a left {@code Either}.
     *
     * @return the left {@code Either}
     */
    public L getLeft() {
        return left;
    }

    /**
     * Return whether this is a right {@code Either}.
     *
     * @return whether this is a right {@code Either}
     */
    @Exclude
    public boolean isRight() {
        return right != null;
    }

    /**
     * Return the right {@code Either} or null if it not a right {@code Either}.
     *
     * @return the right {@code Either}
     */
    public R getRight() {
        return right;
    }

    /**
     * Return whether this is a none {@code Either}.
     *
     * @return whether this is a none {@code Either}
     */
    @Exclude
    public boolean isNone() {
        return !isLeft() && !isRight();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Either)) {
            return false;
        }
        Either other = (Either) obj;
        boolean rightEqual = this.isRight() && other.isRight()
                && this.getRight().equals(other.getRight());
        boolean leftEqual = this.isLeft() && other.isLeft()
                && this.getLeft().equals(other.getLeft());
        boolean noneEqual = this.isNone() && other.isNone();
        return rightEqual || leftEqual || noneEqual;
    }

    @NonNull
    @Override
    public String toString() {
        return "Either: " + (isNone() ? "none" : isRight() ? "right" : "left") + (isNone() ? "" : " value: " + (isRight() ? getRight() : getLeft()));
    }
}

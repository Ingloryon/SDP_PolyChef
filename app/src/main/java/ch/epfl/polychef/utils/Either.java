package ch.epfl.polychef.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

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

    public static <L, R> Either<L, R> left(@NonNull L val) {
        return new Either<L, R>(val, null);
    }

    public static <L, R> Either<L, R> right(@NonNull R val) {
        return new Either<L, R>(null, val);
    }

    public static <L, R> Either<L, R> none() {
        return new Either<L, R>(null, null);
    }

    @Exclude
    public boolean isLeft() {
        return left != null;
    }

    public L getLeft() {
        return left;
    }

    @Exclude
    public boolean isRight() {
        return right != null;
    }

    public R getRight() {
        return right;
    }

    @Exclude
    public boolean isNone() {
        return !isLeft() && !isRight();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Either)) {
            return false;
        }
        Either other = (Either)obj;
        if(this.isRight() && other.isRight()) {
            return this.getRight().equals(other.getRight());
        } else if(this.isLeft() && other.isRight()) {
            return this.getLeft().equals(other.getLeft());
        } else {
            return this.isNone() && other.isNone();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Either: defined for: " + (isNone() ? "none" : isRight() ? "right" : "left") + " value: " + (isRight() ? getRight() : getLeft());
    }
}

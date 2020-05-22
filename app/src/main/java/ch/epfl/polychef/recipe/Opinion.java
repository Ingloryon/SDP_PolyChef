package ch.epfl.polychef.recipe;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A class representing an opinion of a recipe (composed of a rate and a comment).
 */
public class Opinion implements Serializable {

    private String comment;
    private int rate;

    /**
     * Empty constructor for Firebase.
     */
    public Opinion() {
    }

    /**
     * Constructs an opinion with no comment.
     * @param rate the rate given to the recipe
     */
    public Opinion(int rate) {
        this(rate, null);
    }

    /**
     * Constructs an opinion with a rate and a comment.
     * @param rate the rate given to the recipe
     * @param comment the comment concerning the recipe
     */
    public Opinion(int rate, String comment) {
        this.rate = rate;
        this.comment = comment;
    }

    /**
     * Gets the rate of the Opinion.
     * @return the rate of the Opinion
     */
    public int getRate() {
        return rate;
    }

    /**
     * Gets the comment of the Opinion.
     * @return the comment of the Opinion
     */
    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Opinion that = (Opinion) obj;
        return Objects.equals(this.comment, that.comment) && rate == that.rate;
    }
}

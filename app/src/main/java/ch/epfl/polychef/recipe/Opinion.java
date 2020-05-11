package ch.epfl.polychef.recipe;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Opinion {

    private String comment;
    private int rate;

    public Opinion(int rate) {
        this(rate, null);
    }

    public Opinion(int rate, String comment) {
        this.rate = rate;
        this.comment = comment;
    }

    public int getRate() {
        return rate;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Opinion that = (Opinion) obj;
        return Objects.equals(this.comment, that.comment) && rate == that.rate;
    }
}

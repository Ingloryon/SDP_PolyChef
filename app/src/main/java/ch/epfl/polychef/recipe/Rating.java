package ch.epfl.polychef.recipe;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.epfl.polychef.utils.Preconditions;

public final class Rating implements Serializable {
    private int ratingSum;
    private Map<String, Opinion> userIDMatchOp;
    private List<Opinion> opinions;

    /**
     * Constructs a new empty rating.
     */
    public Rating() {
        ratingSum = 0;
        userIDMatchOp = new HashMap<>();
        opinions = new ArrayList<>();
    }

    /**
     * Add a rating for the user defined by {@code userID}.
     *
     * @param userID the id of the user that rated
     * @param rate   the rate
     * @return the rate given by the user
     */
    public int addOpinion(String userID, int rate) {
        return addOpinion(userID, rate, null);
    }

    /**
     * Add a rating and a comment to a user defined by {@code userID}.
     *
     * @param userID  the id of the user that rated and commented
     * @param rate    the rate
     * @param comment the comment, can be null if there is no comment
     * @return the rate given by the user
     */
    public int addOpinion(String userID, int rate, String comment) {
        Preconditions.checkArgument(0 <= rate && rate <= 5, "A rate's value should be between 0 and 5");

        if (userIDMatchOp.containsKey(userID)) {
            Opinion oldOpinion = userIDMatchOp.get(userID);
            opinions.remove(oldOpinion);
            Opinion newOpinion = new Opinion(rate, comment);
            opinions.add(newOpinion);
            userIDMatchOp.replace(userID, newOpinion);
            ratingSum = ratingSum - oldOpinion.getRate() + rate;
            return oldOpinion.getRate();
        } else {
            Opinion newOpinion = new Opinion(rate, comment);
            opinions.add(newOpinion);
            userIDMatchOp.put(userID, newOpinion);
            ratingSum += rate;
            return -1;
        }
    }

    /**
     * Returns the average rating.
     *
     * @return the average rating
     */
    public double ratingAverage() {
        return userIDMatchOp.size() == 0 ? 0.0 : ((double) ratingSum) / userIDMatchOp.size();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%.2f", ratingAverage()) + "/5 stars by " + userIDMatchOp.size() + " users.\n";
    }

    public int getRatingSum() {
        return ratingSum;
    }

    public Map<String, Opinion> getUserIDMatchOp() {
        return userIDMatchOp;
    }

    public List<Opinion> getOpinions(){
        return  opinions;
    }
}
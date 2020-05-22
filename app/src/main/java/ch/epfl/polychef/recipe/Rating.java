package ch.epfl.polychef.recipe;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ch.epfl.polychef.utils.Preconditions;

/**
 * Class representing a Rating given to an Object.
 */
public final class Rating implements Serializable {
    private int ratingSum;
    private Map<String, Opinion> allOpinion;

    /**
     * Constructs a new empty rating.
     */
    public Rating() {
        ratingSum = 0;
        allOpinion = new HashMap<>();
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

        if (allOpinion.containsKey(userID)) {
            Opinion oldOpinion = allOpinion.get(userID);
            allOpinion.replace(userID, new Opinion(rate, comment));
            ratingSum = ratingSum - oldOpinion.getRate() + rate;
            return oldOpinion.getRate();
        } else {
            allOpinion.put(userID, new Opinion(rate, comment));
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
        return allOpinion.size() == 0 ? 0.0 : ((double) ratingSum) / allOpinion.size();
    }

    /**
     * Gets the sum of all the ratings.
     * @return the total rating sum
     */
    public int getRatingSum() {
        return ratingSum;
    }

    /**
     * Gets the map of all ratings received (user, opinion given).
     * @return the map of all ratings
     */
    public Map<String, Opinion> getAllOpinion() {
        return allOpinion;
    }

    /**
     * Gets the userId corresponding to a given Opinion.
     * @param opinion the opinion whose userId we look for
     * @return the corresponding user
     */
    public String getUserIdFromOpinion(@NonNull Opinion opinion){
        if(!allOpinion.containsValue(opinion)){
            return null;
        }
        for(String userId : allOpinion.keySet()){
            if(allOpinion.get(userId).equals(opinion)){
                return userId;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "%.2f", ratingAverage()) + "/5 stars by " + allOpinion.size() + " users.\n";
    }

}
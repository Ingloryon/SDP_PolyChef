package ch.epfl.polychef.recipe;

import ch.epfl.polychef.utils.Preconditions;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Rating implements Serializable {
    private int ratingSum;
    private Map<Integer, Integer> allRatings;

    /**
     * Constructs a new empty rating.
     */
    public Rating(){
        ratingSum = 0;
        allRatings = new HashMap<>();
    }

    /**
     * Adds a rating from a user, if he already rated it changes his personal rating.
     * @param userID the ID of the user, a positive integer
     * @param rate the rate given by the user, between 0 and 5
     */
    public void addRate(int userID, int rate){
        Preconditions.checkArgument(0 <= rate && rate <= 5, "A rate's value should be between 0 and 5");
        Preconditions.checkArgument(userID >= 0, "UserID should be positive");

        if(allRatings.containsKey(userID)) {
            int oldRate = allRatings.get(userID);
            allRatings.replace(userID, rate);
            ratingSum = ratingSum - oldRate + rate;
        }
        else {
            allRatings.put(userID, rate);
            ratingSum += rate;
        }
    }

    /**
     * Returns the average rating.
     * @return the average rating
     */
    public double ratingAverage(){
        return allRatings.size()==0 ? 0.0 : ((double)ratingSum) / allRatings.size();
    }

    @Override
    public String toString(){
        return String.format(Locale.ENGLISH,"%.2f", ratingAverage()) + "/5 stars by " + allRatings.size() + " users.\n";
    }

    public int getRatingSum(){
        return ratingSum;
    }

    public Map<Integer, Integer> getAllRatings(){
        return allRatings;
    }
}
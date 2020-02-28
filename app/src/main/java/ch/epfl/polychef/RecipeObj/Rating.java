package ch.epfl.polychef.RecipeObj;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polychef.Preconditions;

public final class Rating {
    private double ratingSum;
    private Map<Integer, Double> allRatings;

    /**
     * Constructs a new empty rating
     */
    public Rating(){
        ratingSum = 0;
        allRatings = new HashMap<>();
    }

    /**
     * Adds a rating from a user
     * @param userID the ID of the user, a positive integer
     * @param rate the rate given by the user, between 0 and 5
     */
    public void addRate(int userID, double rate){
        Preconditions.checkArgument(0 <= rate && rate <= 5, "A rate's value should be between 0 and 5");
        Preconditions.checkArgument(userID >= 0, "UserID should be positive");

        //TODO: Who can access this method ? How to avoid multiple ratings per user (check requesting user not in the map ?)

        allRatings.put(userID, rate);
        ratingSum += rate;
    }

    /**
     * Returns the average rating
     * @return the average rating
     */
    public double ratingAverage(){
        return allRatings.size()==0 ? 0 : ratingSum / allRatings.size();
    }
}

package ch.epfl.polychef.RecipeObj;

import java.util.ArrayList;

public final class Rating {
    private double ratingSum;
    private ArrayList<Double> ratingList;

    /**
     * Constructs a new empty rating
     */
    public Rating(){
        ratingSum = 0;
        ratingList = new ArrayList<>();
    }

    public void addRate(double rate){
        if( 0 > rate || rate > 5 ) {
            throw new IllegalArgumentException("A rate's value should be between 0 and 5");
        }

        //TODO: Who can access this method ? How to avoid multiple ratings per user (add list<userID> and check requesting user not in the list ?)


        ratingList.add(rate);
        ratingSum += rate;
    }

    public double ratingAverage(){
        return ratingList.size()==0 ? 0 : ratingSum / ratingList.size();
    }
}

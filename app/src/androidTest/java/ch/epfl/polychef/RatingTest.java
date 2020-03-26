package ch.epfl.polychef;

import ch.epfl.polychef.recipe.Rating;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatingTest {

    @Test
    public void addRateRejectsInvalidInputs(){
        Rating rating = new Rating();
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(0,8));
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(0,-5));
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(-8,3));
    }

    @Test
    public void onlyChangeRatingIfUserAlreadyRated(){
        Rating rating = new Rating();

        assertTrue(rating.ratingAverage() == 0);
        rating.addRate(5, 2);
        rating.addRate(6, 2);
        rating.addRate(7, 2);
        rating.addRate(8, 3);

        assertTrue(rating.ratingAverage() == 2.25);

        rating.addRate(2, 4);
        assertTrue(rating.ratingAverage() == (4d*2.25d + 4d) / 5d);

        rating.addRate(5, 5);
        assertTrue(rating.ratingAverage() == (2d+2d+3d+4d + 5d)/5d);
    }


    @Test
    public void testRatingToStringMethod(){
        Rating rating = new Rating();

        int nbIteration=16;
        int sum=addRandomRatingAndReturnSum(rating,nbIteration);

        String result=String.format(Locale.ENGLISH,"%.2f", ((double)sum)/nbIteration) + "/5 stars by " + nbIteration + " users.\n";
        assertEquals(rating.toString(),result);
    }

    @Test
    public void testGetRatingSum(){
        Rating rating = new Rating();

        int nbIteration=16;
        int sum=addRandomRatingAndReturnSum(rating,nbIteration);

        assertEquals(rating.getRatingSum(),sum);
    }

    @Test
    public void testGetAllRatings(){
        Rating rating = new Rating();

        Random rnd=new Random();
        int nb=16;
        HashMap<Integer,Integer> userToRating=new HashMap<>();
        for(int i=0;i<nb;++i){
            int randomInt=rnd.nextInt(6);
            rating.addRate(i,randomInt);
            userToRating.put(i,randomInt);
        }

        assertEquals(rating.getAllRatings(),userToRating);
    }

    private int addRandomRatingAndReturnSum(Rating rating,int iterationNumber){
        Random rnd=new Random();
        int accumulator=0;
        for(int i=0;i<iterationNumber;++i){
            int randomInt=rnd.nextInt(6);
            accumulator+=randomInt;
            rating.addRate(i,randomInt);
        }
        return accumulator;
    }

}

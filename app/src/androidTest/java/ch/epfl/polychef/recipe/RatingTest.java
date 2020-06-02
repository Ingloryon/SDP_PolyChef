package ch.epfl.polychef.recipe;

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;



public class RatingTest {

    @Test
    public void addRateRejectsInvalidInputs(){
        Rating rating = new Rating();
        assertThrows(IllegalArgumentException.class, () -> rating.addOpinion("0",8));
        assertThrows(IllegalArgumentException.class, () -> rating.addOpinion("0",-5));
    }

    @Test
    public void onlyChangeRatingIfUserAlreadyRated(){
        Rating rating = new Rating();

        assertTrue(rating.ratingAverage() == 0);
        rating.addOpinion("5", 2);
        rating.addOpinion("6", 2);
        rating.addOpinion("7", 2);
        rating.addOpinion("8", 3);

        assertEquals(2.25, rating.ratingAverage(), 0.0);

        rating.addOpinion("2", 4);
        assertEquals((4d * 2.25d + 4d) / 5d, rating.ratingAverage(), 0.0);

        rating.addOpinion("5", 5);
        assertEquals((2d + 2d + 3d + 4d + 5d) / 5d, rating.ratingAverage(), 0.0);
    }


    @Test
    public void testRatingToStringMethod(){
        Rating rating = new Rating();
        int nb=16;
        int accumulator = initRating(rating, nb);

        String result=String.format(Locale.ENGLISH,"%.2f", ((double)accumulator)/nb) + "/5 stars by " + nb + " users.\n";
        assertEquals(rating.toString(),result);
    }

    @Test
    public void testGetRatingSum(){
        Rating rating = new Rating();
        int nb=16;
        int accumulator = initRating(rating, nb);

        assertEquals(rating.getRatingSum(),accumulator);
    }

    private int initRating(Rating rating, int nb){
        Random rnd=new Random();

        int accumulator=0;
        for(int i=0;i<nb;++i){
            int rndNext =rnd.nextInt(6);
            accumulator+=rndNext;
            rating.addOpinion(Integer.toString(i),rndNext);
        }
        return accumulator;
    }

    @Test
    public void testGetAllRatings(){
        Rating rating = new Rating();

        Random rnd=new Random();
        int nb=16;
        HashMap<String,Opinion> userToRating=new HashMap<>();
        for(int i=0;i<nb;++i){
            int rndNext=rnd.nextInt(6);
            rating.addOpinion(Integer.toString(i),rndNext);
            userToRating.put(Integer.toString(i),new Opinion(rndNext));
        }

        assertEquals(rating.getAllOpinion(),userToRating);
    }

}
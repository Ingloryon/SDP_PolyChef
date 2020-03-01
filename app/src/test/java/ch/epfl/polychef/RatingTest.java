package ch.epfl.polychef;

import org.junit.Test;

import ch.epfl.polychef.RecipeObj.Rating;
import static org.junit.Assert.assertTrue;

public class RatingTest {

    @Test
    public void addRateRejectsInvalidInputs(){
        Rating r = new Rating();/*
        assertThrows(IllegalArgumentException.class, () -> r.addRate(0,8));
        assertThrows(IllegalArgumentException.class, () -> r.addRate(0,-5));
        assertThrows(IllegalArgumentException.class, () -> r.addRate(-8,3.2));     */
    }

    @Test
    public void onlyChangeRatingIfUserAlreadyRated(){
        Rating r = new Rating();

        assertTrue(r.ratingAverage() == 0);
        r.addRate(5, 2.25);

        assertTrue(r.ratingAverage() == 2.25);

        r.addRate(2, 4);
        assertTrue(r.ratingAverage() == (2.25d + 4d) / 2d);

        r.addRate(5, 5);
        assertTrue(r.ratingAverage() == (4d + 5d)/2);
    }
}

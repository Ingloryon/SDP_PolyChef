package ch.epfl.polychef;

import ch.epfl.polychef.recipe.Rating;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatingTest {

    @Test
    public void addRateRejectsInvalidInputs(){
        Rating rating = new Rating();
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(0,8));
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(0,-5));
        assertThrows(IllegalArgumentException.class, () -> rating.addRate(-8,3.2));
    }

    @Test
    public void onlyChangeRatingIfUserAlreadyRated(){
        Rating rating = new Rating();

        assertTrue(rating.ratingAverage() == 0);
        rating.addRate(5, 2.25);

        assertTrue(rating.ratingAverage() == 2.25);

        rating.addRate(2, 4);
        assertTrue(rating.ratingAverage() == (2.25d + 4d) / 2d);

        rating.addRate(5, 5);
        assertTrue(rating.ratingAverage() == (4d + 5d)/2);
    }
}

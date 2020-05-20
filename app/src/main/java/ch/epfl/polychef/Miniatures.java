package ch.epfl.polychef;

import ch.epfl.polychef.recipe.Rating;

public interface Miniatures {

    String getKey();

    void setKey(String key);

    Rating getRating();

    boolean isUser();

    boolean isRecipe();

}

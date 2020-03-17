package ch.epfl.polychef;

import ch.epfl.polychef.recipe.Recipe;

public interface FireHandler {

    public void onSuccess(Recipe recipe);

    public void onFailure();
}

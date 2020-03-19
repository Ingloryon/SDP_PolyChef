package ch.epfl.polychef.firebase;

import ch.epfl.polychef.recipe.Recipe;

public interface FireHandler {

    public void onSuccess(Recipe recipe);

    public void onFailure();
}
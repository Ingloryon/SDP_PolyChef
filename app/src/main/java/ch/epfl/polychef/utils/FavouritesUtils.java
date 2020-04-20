package ch.epfl.polychef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class FavouritesUtils {

    private static final String TAG = "FavouritesUtils";
    private static final Gson gson = new Gson();
    private static final String favouriteKey = "favourites";
    private static final FavouritesUtils INSTANCE = new FavouritesUtils();

    private FavouritesUtils() {
    }

    public static FavouritesUtils getInstance() {
        return INSTANCE;
    }

    /**
     * Set a toggle button to act as a favourite button for a recipe.
     *
     * @param userStorage the user storage
     * @param button      the favourite button
     * @param recipe      the recipe to add to favourite on click
     */
    public void setFavouriteButton(UserStorage userStorage, ToggleButton button, Recipe recipe) {
        Preconditions.checkArgument(recipe != null, "The recipe can not be null");
        Preconditions.checkArgument(button != null, "The button can not be null");
        if (userStorage != null && userStorage.getPolyChefUser() != null) {
            List<String> favouritesList = userStorage.getPolyChefUser().getFavourites();
            button.setVisibility(View.VISIBLE);
            if (favouritesList.contains(recipe.getRecipeUuid())) {
                button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_yellow));
                button.setChecked(true);
            } else {
                button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_grey));
                button.setChecked(false);
            }
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                List<Recipe> recipes = getOfflineFavourites();
                if (isChecked) {
                    button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_yellow));
                    userStorage.getPolyChefUser().addFavourite(recipe.getRecipeUuid());
                    userStorage.updateUserInfo();
                    if (!recipes.contains(recipe)) {
                        recipes.add(recipe);
                        putFavouriteList(recipes);
                    }
                } else {
                    button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_grey));
                    userStorage.getPolyChefUser().removeFavourite(recipe.getRecipeUuid());
                    userStorage.updateUserInfo();
                    if (recipes.contains(recipe)) {
                        recipes.remove(recipe);
                        putFavouriteList(recipes);
                    }
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }

    /**
     * Get the locally saved favourites.
     *
     * @return the locally saved favourites
     */
    public List<Recipe> getOfflineFavourites() {
        SharedPreferences sharedPref = getSharedPreferences();
        String recipesJson = sharedPref.getString(favouriteKey, "");
        return recipesJson.isEmpty() ? new ArrayList<>() : gson.fromJson(recipesJson, new TypeToken<List<Recipe>>() {
        }.getType());
    }

    /**
     * Add all online favourites of a user to the local storage for later use.
     *
     * @param user    the user
     */
    public void setOfflineFavourites(User user) {
        Preconditions.checkArgument(user != null, "User can not be null");
        List<String> favouritesList = user.getFavourites();
        List<Recipe> newOfflineRecipes = new ArrayList<>();
        for (int i = 0; i < favouritesList.size(); ++i) {
            final boolean isLast = i == favouritesList.size() - 1;
            getRecipeStorage().readRecipeFromUuid(favouritesList.get(i), new CallHandler<Recipe>() {
                @Override
                public void onSuccess(Recipe data) {
                    newOfflineRecipes.add(data);
                    if (isLast) {
                        putFavouriteList(newOfflineRecipes);
                    }
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Error could not charge recipe");
                }
            });
        }
    }

    /**
     * Get the recipe storage.
     *
     * @return the recipe storage
     */
    public RecipeStorage getRecipeStorage() {
        return RecipeStorage.getInstance();
    }

    private void putFavouriteList(List<Recipe> recipes) {
        SharedPreferences sharedPref = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        String recipesJson = gson.toJson(recipes);
        editor.putString(favouriteKey, recipesJson);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return GlobalApplication.getAppContext().getSharedPreferences("FavouriteList", Context.MODE_PRIVATE);
    }
}

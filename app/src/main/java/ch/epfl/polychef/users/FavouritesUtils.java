package ch.epfl.polychef.users;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Recipe;

public class FavouritesUtils {

    private static final Gson gson = new Gson();
    private static final String favouriteKey = "favourites";

    /**
     * Set a toggle button to act as a favourite button for a recipe.
     *
     * @param context     the context of the button
     * @param userStorage the user storage
     * @param button      the favourite button
     * @param recipe      the recipe to add to favourite on click
     */
    public static void setFavouriteButton(Context context, UserStorage userStorage, ToggleButton button, Recipe recipe) {
        if (userStorage != null && userStorage.getPolyChefUser() != null) {
            List<String> favouritesList = userStorage.getPolyChefUser().getFavourites(); // TODO take offline favourite if no connection
            button.setVisibility(View.VISIBLE);
            if (favouritesList.contains(recipe.getRecipeUuid())) {
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_yellow));
                button.setChecked(true);
            } else {
                button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_grey));
                button.setChecked(false);
            }
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_yellow));
                    userStorage.getPolyChefUser().addFavourite(recipe.getRecipeUuid());
                    userStorage.updateUserInfo();
                    List<Recipe> recipes = getOfflineFavourites(context);
                    if (!recipes.contains(recipe)) {
                        recipes.add(recipe);
                        putFavouriteList(context, recipes);
                    }
                } else {
                    button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_grey));
                    userStorage.getPolyChefUser().removeFavourite(recipe.getRecipeUuid());
                    userStorage.updateUserInfo();
                    List<Recipe> recipes = getOfflineFavourites(context);
                    if (recipes.contains(recipe)) {
                        recipes.remove(recipe);
                        putFavouriteList(context, recipes);
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
     * @param context the context to get the locally saved favourites from
     * @return the locally saved favourites
     */
    public static List<Recipe> getOfflineFavourites(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        String recipesJson = sharedPref.getString(favouriteKey, "");
        return recipesJson.isEmpty() ? new ArrayList<>() : gson.fromJson(recipesJson, new TypeToken<List<Recipe>>() {
        }.getType());
    }

    private static void putFavouriteList(Context context, List<Recipe> recipes) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        String recipesJson = gson.toJson(recipes);
        editor.putString(favouriteKey, recipesJson);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("FavouriteList", Context.MODE_PRIVATE);
    }
}

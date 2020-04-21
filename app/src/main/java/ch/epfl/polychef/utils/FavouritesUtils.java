package ch.epfl.polychef.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.MultipleCallHandler;
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
            button.setChecked(favouritesList.contains(recipe.getRecipeUuid()));
            setButton(button);
            button.setOnCheckedChangeListener((buttonView, isChecked) -> onClickToggleButton(userStorage, button, recipe));
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void onClickToggleButton(UserStorage userStorage, ToggleButton button, Recipe recipe) {
        setButton(button);
        setOrRemoveRecipe(userStorage, recipe, button.isChecked());
    }

    private void setButton(ToggleButton button) {
        if(button.isChecked()) {
            button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_yellow));
        } else {
            button.setBackgroundDrawable(ContextCompat.getDrawable(GlobalApplication.getAppContext(), R.drawable.ic_star_black_grey));
        }
    }

    private void setOrRemoveRecipe(UserStorage userStorage, Recipe recipe, boolean shouldAdd) {
        List<Recipe> recipes = getOfflineFavourites();
        if(shouldAdd) {
            userStorage.getPolyChefUser().addFavourite(recipe.getRecipeUuid());
        } else {
            userStorage.getPolyChefUser().removeFavourite(recipe.getRecipeUuid());
        }
        userStorage.updateUserInfo();
        if(shouldAdd) {
            recipes.add(recipe);
        } else {
            recipes.remove(recipe);
        }
        putFavouriteList(recipes);
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
        MultipleCallHandler<Recipe> multipleCallHandler = new MultipleCallHandler<>(favouritesList.size(), this::putFavouriteList);
        for (int i = 0; i < favouritesList.size(); ++i) {
            getRecipeStorage().readRecipeFromUuid(favouritesList.get(i), multipleCallHandler);
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

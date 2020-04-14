package ch.epfl.polychef.users;

import android.content.Context;
import android.view.View;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Recipe;

public class FavouritesUtils {

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
            List<String> favouritesList = userStorage.getPolyChefUser().getFavourites();
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
                } else {
                    button.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_grey));
                    userStorage.getPolyChefUser().removeFavourite(recipe.getRecipeUuid());
                    userStorage.updateUserInfo();
                }
            });
        } else {
            button.setVisibility(View.GONE);
        }
    }
}

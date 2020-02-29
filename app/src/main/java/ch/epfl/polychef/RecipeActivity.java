package ch.epfl.polychef;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polychef.RecipeObj.Recipe;
import ch.epfl.polychef.RecipeObj.RecipeBuilder;

public class RecipeActivity extends AppCompatActivity {

    // Need to have a intent that have the recipe to display
    // For now I will hardcode one for example purpose
    public Recipe defaultRecipe = new RecipeBuilder()
            .setName("Chicken tartar")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(30)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(40)
            .addIngredient("Raw chicken", 500)
            .addIngredient("Parsley", 3)
            .addInstruction("Don't cook this it's dangerous !")
            .addInstruction("Cut the chicken into small pieces")
            .addInstruction("Add salt and pepper")
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullrecipe);
        // Recipe name
        TextView recipeName = findViewById(R.id.recipeName);
        recipeName.setText(defaultRecipe.getName());
        // Rating
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        // TODO remove me when rating is implemented in the application
        defaultRecipe.getRating().addRate(3, 2);
        defaultRecipe.getRating().addRate(4, 5);
        ratingBar.setRating((float) defaultRecipe.getRating().ratingAverage());
        // Prep time
    }
}

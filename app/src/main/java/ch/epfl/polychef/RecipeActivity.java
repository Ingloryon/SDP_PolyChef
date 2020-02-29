package ch.epfl.polychef;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polychef.RecipeObj.Recipe;
import ch.epfl.polychef.RecipeObj.RecipeBuilder;

public class RecipeActivity extends AppCompatActivity {

    // Need to have a intent that have the recipe to display
    // For now I will hardcode one for example purpose
    public Recipe defaultRecipe = new RecipeBuilder()
            .setName("Tartare de poulet")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(30)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(40)
            .addIngredient("Poulet cru", 500)
            .addIngredient("Parsley", 3)
            .addInstruction("Don't cook this it's dangerous !", 0)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullrecipe);
        TextView recipeName = findViewById(R.id.recipeName);
    }
}

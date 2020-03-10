package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Recipe currentRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullrecipe);
        Intent intent = getIntent();
        currentRecipe = (Recipe) intent.getSerializableExtra("Recipe");
        displayRecipeName();
        displayImage();
        displayRating();
        displayPrepAndCookTime();
        displayDifficulty();
        displayInstructions();
        displayIngredients();
    }

    private void displayImage() {
        ImageView recipeImage = findViewById(R.id.recipeImage);
        recipeImage.setImageResource(currentRecipe.getPicturesNumbers().get(0));
    }

    /**
     * Display the recipe name in the correct field in the activity.
     */
    private void displayRecipeName(){
        TextView recipeName = findViewById(R.id.recipeName);
        recipeName.setText(currentRecipe.getName());
    }

    /**
     * Display the rating of the recipe in the correct field in the activity.
     */
    private void displayRating(){
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating((float) currentRecipe.getRating().ratingAverage());
    }

    /**
     * Display both the preparation time and the cooking time in the correct field in the activity.
     */
    private void displayPrepAndCookTime(){
        TextView prepTime = findViewById(R.id.prepTime);
        prepTime.setText("Prep time : "+currentRecipe.getEstimatedPreparationTime()+" mins");
        TextView cookTime = findViewById(R.id.cookTime);
        cookTime.setText("Cook time : "+currentRecipe.getEstimatedCookingTime()+" mins");
    }

    /**
     * Display the difficulty in the correct field in the activity.
     */
    private void displayDifficulty(){
        TextView difficulty = findViewById(R.id.difficulty);
        String diff = currentRecipe.getRecipeDifficulty().toString();
        String finalDiffStr = diff.substring(0, 1).toUpperCase().concat(diff.substring(1, diff.length()).toLowerCase().replaceAll("_", " "));
        difficulty.setText("Difficulty : " + finalDiffStr);
    }

    /**
     * Display all the ingredients from the list in a big string.
     */
    private void displayIngredients(){
        StringBuilder strBuilder = new StringBuilder();
        String newLine = "\n";
        for(Ingredient ingredient: currentRecipe.getIngredients()){
            strBuilder.append("● ");
            strBuilder.append(ingredient.toString());
            strBuilder.append(newLine);
            strBuilder.append(newLine);
        }
        // Remove the last line return since there is no more ingredients to display
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        TextView ingredients = findViewById(R.id.ingredientsList);
        ingredients.setText(strBuilder.toString());
    }

    /**
     * Display all the instructions from the list in a big string.
     */
    private void displayInstructions(){
        StringBuilder strBuilder = new StringBuilder();
        String newLine = "\n";
        List<String> allInstructions = currentRecipe.getRecipeInstructions();
        for(int i = 0; i < allInstructions.size(); i++){
            strBuilder.append(i + 1);
            strBuilder.append(". ");
            strBuilder.append(allInstructions.get(i));
            strBuilder.append(newLine);
            strBuilder.append(newLine);
        }
        // Remove the last line return since there is no more instructions to display
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        TextView instructions = findViewById(R.id.instructionsList);
        instructions.setText(strBuilder.toString());
    }

}

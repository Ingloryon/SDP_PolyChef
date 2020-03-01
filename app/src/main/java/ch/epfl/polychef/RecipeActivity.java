package ch.epfl.polychef;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

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
        displayRecipeName();
        displayRating();
        displayPrepAndCookTime();
        displayDifficulty();
        displayInstructions();
        displayIngredients();
    }

    /**
     * Display the recipe name in the correct field in the activity
     */
    private void displayRecipeName(){
        TextView recipeName = findViewById(R.id.recipeName);
        recipeName.setText(defaultRecipe.getName());
    }

    /**
     * Display the rating of the recipe in the correct field in the activity
     */
    private void displayRating(){
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        // TODO remove me when rating is implemented in the application
        defaultRecipe.getRating().addRate(3, 2);
        defaultRecipe.getRating().addRate(4, 5);
        ratingBar.setRating((float) defaultRecipe.getRating().ratingAverage());
    }

    /**
     * Display both the preparation time and the cooking time in the correct field in the activity
     */
    private void displayPrepAndCookTime(){
        TextView prepTime = findViewById(R.id.prepTime);
        prepTime.setText("Prep time : "+defaultRecipe.getEstimatedPreparationTime()+" mins");
        TextView cookTime = findViewById(R.id.cookTime);
        cookTime.setText("Cook time : "+defaultRecipe.getEstimatedCookingTime()+" mins");
    }

    /**
     * Display the difficulty in the correct field in the activity
     */
    private void displayDifficulty(){
        TextView difficulty = findViewById(R.id.difficulty);
        String diff = defaultRecipe.getRecipeDifficulty().toString();
        String finalDiffStr = diff.substring(0, 1).toUpperCase().concat(diff.substring(1, diff.length()).toLowerCase().replaceAll("_", " "));
        difficulty.setText("Difficulty : " + finalDiffStr);
    }

    /**
     * Display all the ingredients from the list in a big string
     */
    private void displayIngredients(){
        StringBuilder strBuilder = new StringBuilder();
        String newLine = "\n";
        Map<String, Double> allIngredients = defaultRecipe.getIngredients();
        for(Map.Entry<String, Double> ingredient : allIngredients.entrySet()){
            strBuilder.append("● ");
            strBuilder.append(ingredient.getValue() + " grams of ");
            strBuilder.append(ingredient.getKey().toLowerCase());
            strBuilder.append(newLine);
            strBuilder.append(newLine);
        }
        // Remove the last line return since there is no more ingredients to display
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        TextView ingredients = findViewById(R.id.ingredientsList);
        ingredients.setText(strBuilder.toString());
    }

    /**
     * Display all the instructions from the list in a big string
     */
    private void displayInstructions(){
        StringBuilder strBuilder = new StringBuilder();
        String newLine = "\n";
        List<String> allInstructions = defaultRecipe.getRecipeInstructions();
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

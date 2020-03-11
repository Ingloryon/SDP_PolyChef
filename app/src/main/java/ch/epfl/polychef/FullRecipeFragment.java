package ch.epfl.polychef;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;

import java.util.List;

public final class FullRecipeFragment extends Fragment {
    private Recipe currentRecipe;
    private static final String NEW_LINE = "\n";

    /**
     * Required empty public constructor
     */
    public FullRecipeFragment() {}

    /**
     * When the View is created we get the recipe and display its attributes
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_recipe, container, false);
        // Get the recipe from the click listener of the miniature recyclerView
        Bundle bundle = this.getArguments();
        if(bundle != null){
            currentRecipe = (Recipe) bundle.getSerializable("Recipe");
        }
        displayRecipeName(view);
        displayImage(view);
        displayRating(view);
        displayPrepAndCookTime(view);
        displayDifficulty(view);
        displayInstructions(view);
        displayIngredients(view);

        return view;
    }

    /**
     * Display the recipe main image in the correct field in the activity.
     */
    private void displayImage(View view) {
        ImageView recipeImage = view.findViewById(R.id.recipeImage);
        recipeImage.setImageResource(currentRecipe.getPicturesNumbers().get(0));
    }

    /**
     * Display the recipe name in the correct field in the activity.
     */
    private void displayRecipeName(View view){
        TextView recipeName = view.findViewById(R.id.recipeName);
        recipeName.setText(currentRecipe.getName());
    }

    /**
     * Display the rating of the recipe in the correct field in the activity.
     */
    private void displayRating(View view){
        final RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) currentRecipe.getRating().ratingAverage());
    }

    /**
     * Display both the preparation time and the cooking time in the correct field in the activity.
     */
    private void displayPrepAndCookTime(View view){
        TextView prepTime = view.findViewById(R.id.prepTime);
        prepTime.setText("Prep time : "+currentRecipe.getEstimatedPreparationTime()+" mins");
        TextView cookTime = view.findViewById(R.id.cookTime);
        cookTime.setText("Cook time : "+currentRecipe.getEstimatedCookingTime()+" mins");
    }

    /**
     * Display the difficulty in the correct field in the activity.
     */
    private void displayDifficulty(View view){
        TextView difficulty = view.findViewById(R.id.difficulty);
        String diff = currentRecipe.getRecipeDifficulty().toString();
        String finalDiffStr = diff.substring(0, 1).toUpperCase().concat(diff.substring(1, diff.length()).toLowerCase().replaceAll("_", " "));
        difficulty.setText("Difficulty : " + finalDiffStr);
    }

    /**
     * Display all the ingredients from the list in a big string.
     */
    private void displayIngredients(View view){
        StringBuilder strBuilder = new StringBuilder();
        for(Ingredient ingredient: currentRecipe.getIngredients()){
            strBuilder.append("● ");
            strBuilder.append(ingredient.toString());
            strBuilder.append(NEW_LINE);
            strBuilder.append(NEW_LINE);
        }
        // Remove the last line return since there is no more ingredients to display
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        TextView ingredients = view.findViewById(R.id.ingredientsList);
        ingredients.setText(strBuilder.toString());
    }

    /**
     * Display all the instructions from the list in a big string.
     */
    private void displayInstructions(View view){
        StringBuilder strBuilder = new StringBuilder();
        List<String> allInstructions = currentRecipe.getRecipeInstructions();
        for(int i = 0; i < allInstructions.size(); i++){
            strBuilder.append(i + 1);
            strBuilder.append(". ");
            strBuilder.append(allInstructions.get(i));
            strBuilder.append(NEW_LINE);
            strBuilder.append(NEW_LINE);
        }
        // Remove the last line return since there is no more instructions to display
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        TextView instructions = view.findViewById(R.id.instructionsList);
        instructions.setText(strBuilder.toString());
    }
}

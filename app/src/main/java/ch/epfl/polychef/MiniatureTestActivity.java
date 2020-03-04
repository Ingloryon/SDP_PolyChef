package ch.epfl.polychef;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeMiniatureAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an activity to test the display of a recyclerview containing the recipe miniatures
 * This will be deleted later when there is no more need for an example
 */
public class MiniatureTestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecipeMiniatureAdapter miniatureAdapter;
    List<Recipe> testRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miniaturetest);
        testRecipeList = new ArrayList<>();
        // set the recyclerView object to be the one inside an activity using its id
        recyclerView = findViewById(R.id.cardList);
        // bind a layoutmanager to it (might not need one maybe try without ?)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Recipe recipeTarTar = new RecipeBuilder()
                .setName("Beef tartar")
                .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
                .setEstimatedCookingTime(30)
                .setPersonNumber(4)
                .setEstimatedPreparationTime(40)
                .addIngredient("Raw beef", 500)
                .addIngredient("Parsley", 3)
                .addInstruction("Cut the parsley very fine")
                .addInstruction("Cut the beef into small pieces")
                .addInstruction("Add salt and pepper to taste")
                .addInstruction("Serve it in a dome shape")
                .build();

        Recipe recipeCurry = new RecipeBuilder()
                .setName("Chicken with red curry")
                .setRecipeDifficulty(Recipe.Difficulty.HARD)
                .setEstimatedCookingTime(40)
                .setPersonNumber(4)
                .setEstimatedPreparationTime(20)
                .addIngredient("Raw chicken", 435)
                .addIngredient("Curry", 6)
                .addIngredient("Parsley", 3)
                .addInstruction("Stir the chicken in an average pan for 10 minutes")
                .addInstruction("Add lots of curry")
                .addInstruction("Serve with fresh parsley")
                .build();

        Recipe recipeNoodles = new RecipeBuilder()
                .setName("Noodles with mushrooms")
                .setRecipeDifficulty(Recipe.Difficulty.VERY_EASY)
                .setEstimatedCookingTime(10)
                .setPersonNumber(4)
                .setEstimatedPreparationTime(50)
                .addIngredient("Noodles", 500)
                .addIngredient("Mushrooms", 200)
                .addInstruction("Had noodles to hot water for 5 minutes")
                .addInstruction("In a wok add the mushrooms with oil and stir frequently")
                .addInstruction("Add salt and pepper")
                .build();

        recipeTarTar.getRating().addRate(3, 2);
        recipeTarTar.getRating().addRate(4, 5);

        recipeCurry.getRating().addRate(10, 2);
        recipeCurry.getRating().addRate(8, 4);

        recipeNoodles.getRating().addRate(10, 4);
        recipeNoodles.getRating().addRate(8, 5);

        testRecipeList.add(recipeTarTar);
        testRecipeList.add(recipeNoodles);
        testRecipeList.add(recipeCurry);

        // ones we have the recipe list we can create the adapter that contains the list and set the recyclerview to have this list
        miniatureAdapter = new RecipeMiniatureAdapter(this, testRecipeList);
        recyclerView.setAdapter(miniatureAdapter);
    }
}

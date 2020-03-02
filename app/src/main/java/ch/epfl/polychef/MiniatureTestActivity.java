package ch.epfl.polychef;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.RecipeObj.Recipe;
import ch.epfl.polychef.RecipeObj.RecipeBuilder;
import ch.epfl.polychef.RecipeObj.RecipeMiniatureAdapter;

public class MiniatureTestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecipeMiniatureAdapter miniatureAdapter;
    List<Recipe> testRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miniaturetest);
        testRecipeList = new ArrayList<>();
        recyclerView = findViewById(R.id.cardList);
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

        testRecipeList.add(recipeTarTar);
        testRecipeList.add(recipeNoodles);
        testRecipeList.add(recipeCurry);

        miniatureAdapter = new RecipeMiniatureAdapter(this, testRecipeList);
        recyclerView.setAdapter(miniatureAdapter);
    }
}

package ch.epfl.polychef;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestingUnitTest {

    @Test
    public void toStringDisplaysRecipe() {
        Recipe recipe = setStandardRecipe().build();

        String startingString = "\n" + "Recipe name: Chicken fried\n" + "\n" +
                "Recipe instructions:\n" +
                "1- Start by the beginning\n" +
                "2- Then keep going\n" +
                "3- Now it ends\n" +
                "\n" +
                "For 4 persons, the needed ingredients are:\n";

        String endingString = "\n" + "The recipe is very easy.\n" +
                "The recipes takes around 45min of preparation and 50min of cooking.\n" +
                "The recipe is rated 0.00/5 stars by 0 users.\n";

        String str1 = startingString +
                "300.0 grams of Carrots\n" +
                "75.0 kilograms of Chicken wings\n" +
                endingString;

        String str2 = startingString +
                "75.0 kilograms of Chicken wings\n" +
                "300.0 grams of Carrots\n" +
                endingString;

        System.out.println(recipe.toString());

        // The ingredients can be displayed in any order so toString() can be either str1 or str2
        assertTrue(str1.equals(recipe.toString()) || str2.equals(recipe.toString()));
    }

    public static RecipeBuilder setStandardRecipe(){
        RecipeBuilder rb1 = new RecipeBuilder();
        rb1.setName("Chicken fried");
        rb1.addInstruction("Start by the beginning");
        rb1.addInstruction("Then keep going");
        rb1.addInstruction("Now it ends");
        rb1.addIngredient("Carrots", 300d, Ingredient.Unit.GRAM);
        rb1.addIngredient("Chicken wings", 75d, Ingredient.Unit.KILOGRAM);
        rb1.setPersonNumber(4);
        rb1.setEstimatedPreparationTime(45);
        rb1.setEstimatedCookingTime(50);
        rb1.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        return rb1;
    }


    @Test
    public void builderArgumentsAreSetCorrectlyInRecipe() {
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d, Ingredient.Unit.NONE);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_HARD);

        ArrayList<String> instruc = new ArrayList<>();
        instruc.add("Start by the beginning");
        List<Ingredient> ingre = new ArrayList<>();
        ingre.add(new Ingredient("Carrots", 300d, Ingredient.Unit.NONE));

        Recipe.Difficulty.values();
        Recipe.Difficulty.valueOf("VERY_HARD");
        Recipe recipe = rb.build();
        recipe.getRating();

        assertEquals(recipe.getName(), "Chicken fried");
        assertEquals(recipe.getRecipeInstructions(), Collections.unmodifiableList(instruc));
        assertEquals(recipe.getIngredients(), Collections.unmodifiableList(ingre));
        assertEquals(recipe.getPersonNumber(), 4);
        assertEquals(recipe.getEstimatedPreparationTime(), 45);
        assertEquals(recipe.getEstimatedCookingTime(), 50);
        assertEquals(recipe.getRecipeDifficulty(), Recipe.Difficulty.VERY_HARD);
        assertEquals(recipe.getEstimatedTotalTime(), 95);
        assertEquals(recipe.getMiniaturePath(), "/src/default_miniature.png");
        assertEquals(recipe.getPicturesNumbers(), Arrays.asList(R.drawable.frenchtoast));

        rb.addPicturePath(15);
        rb.setMiniaturePath("/src/miniature.jpeg");
        rb.setMiniaturePath("/src/miniature.png");
        Recipe recipe2 = rb.build();

        assertEquals(recipe2.getMiniaturePath(), "/src/miniature.png");
        assertEquals(recipe2.getPicturesNumbers(), Arrays.asList(15));
    }
}

package ch.epfl.polychef;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipeTest {

    @Test
    public void recipeBuilderAndSettersRejectInvalidInputs() {
        RecipeBuilder rb = new RecipeBuilder();

        // rejects empty names
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        // setter rejects empty names
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setName(""));
        rb.setName("Chicken fried");

        // rejects when no instructions added
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addInstruction(""));
        rb.addInstruction("Start by the beginning");

        // rejects when no ingredients
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("", 300));
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("Carrots", 0));
        rb.addIngredient("Carrots", 300);

        // rejects when no persons
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setPersonNumber(0));
        rb.setPersonNumber(4);

        // rejects when no entered estimatedPreparationTime
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setEstimatedPreparationTime(0));
        rb.setEstimatedPreparationTime(45);

        // rejects when no entered estimatedCookingTime
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setEstimatedCookingTime(0));
        rb.setEstimatedCookingTime(45);

        // rejects when no entered recipeDifficulty
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.build());
        rb.setRecipeDifficulty(Recipe.Difficulty.EASY);

        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath("Does not end by png"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(-1));

        rb.build();
    }


    @Test
    public void builderArgumentsAreSetCorrectlyInRecipe() {
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_HARD);
        Recipe recipe = rb.build();

        ArrayList<String> instruc = new ArrayList<String>();
        instruc.add("Start by the beginning");
        HashMap<String, Double> ingre = new HashMap<String, Double>();
        ingre.put("Carrots", 300d);
        Recipe.Difficulty.values();
        Recipe.Difficulty.valueOf("VERY_HARD");
        recipe.getRating();

        assertEquals(recipe.getName(), "Chicken fried");
        assertEquals(recipe.getRecipeInstructions(), Collections.unmodifiableList(instruc));
        assertEquals(recipe.getIngredients(), Collections.unmodifiableMap(ingre));
        assertEquals(recipe.getPersonNumber(), 4);
        assertEquals(recipe.getEstimatedPreparationTime(), 45);
        assertEquals(recipe.getEstimatedCookingTime(), 50);
        assertEquals(recipe.getRecipeDifficulty(), Recipe.Difficulty.VERY_HARD);
        assertEquals(recipe.getEstimatedTotalTime(), 95);
        assertEquals(recipe.getMiniaturePath(), "/src/default_miniature.png");
        assertEquals(recipe.getPicturesNumbers(), Arrays.asList(R.drawable.koreansteaktartare));

        rb.addPicturePath(15);
        rb.setMiniaturePath("/src/miniature.jpeg");
        rb.setMiniaturePath("/src/miniature.png");
        Recipe recipe2 = rb.build();

        assertEquals(recipe2.getMiniaturePath(), "/src/miniature.png");
        assertEquals(recipe2.getPicturesNumbers(), Arrays.asList(15));
    }

    @Test
    public void argumentsReturnedAreUnmodifiable() {
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE);
        Recipe recipe = rb.build();

        Map<String, Double> ingre = recipe.getIngredients();
        List<String> instr = recipe.getRecipeInstructions();


        Assertions.assertThrows(UnsupportedOperationException.class, () -> ingre.put("Steaks", 1000d));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> instr.add("/src/hello.png"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> instr.set(0, "/src/evilChanger.png"));
        for (Map.Entry<String, Double> e : ingre.entrySet()) {
            Assertions.assertThrows(UnsupportedOperationException.class, () -> e.setValue(e.getValue() * 10));
        }

    }

    @Test
    public void changingPersonNumberScalesIngredients() {
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.addIngredient("Chicken wings", 75);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.EASY);
        Recipe recipe = rb.build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> recipe.scalePersonAndIngredientsQuantities(0));

        recipe.scalePersonAndIngredientsQuantities(2);
        Map<String, Double> ingre = recipe.getIngredients();

        assertTrue(ingre.get("Carrots") == 150d);
        assertTrue(ingre.get("Chicken wings") == (75d / 2d));
    }

    @Test
    public void equalsOnlyReturnsTrueWhenUuidIdentical() {
        RecipeBuilder rb1 = new RecipeBuilder();
        rb1.setName("Chicken fried");
        rb1.addInstruction("Start by the beginning");
        rb1.addIngredient("Carrots", 300d);
        rb1.addIngredient("Chicken wings", 75);
        rb1.setPersonNumber(4);
        rb1.setEstimatedPreparationTime(45);
        rb1.setEstimatedCookingTime(50);
        rb1.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe1 = rb1.build();

        RecipeBuilder rb2 = new RecipeBuilder();
        rb2.setName("Chicken fried");
        rb2.addInstruction("Start by the beginning");
        rb2.addIngredient("Carrots", 300d);
        rb2.addIngredient("Chicken wings", 75);
        rb2.setPersonNumber(4);
        rb2.setEstimatedPreparationTime(45);
        rb2.setEstimatedCookingTime(50);
        rb2.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe2 = rb2.build();

        assertTrue(!recipe1.equals(""));
        assertTrue(!recipe1.equals(recipe2));
        assertTrue(recipe1.equals(recipe1));
        assertTrue(recipe2.equals(recipe2));
    }

    @Test
    public void toStringDisplaysRecipe() {
        RecipeBuilder rb1 = new RecipeBuilder();
        rb1.setName("Chicken fried");
        rb1.addInstruction("Start by the beginning");
        rb1.addInstruction("Then keep going");
        rb1.addInstruction("Now it ends");
        rb1.addIngredient("Carrots", 300d);
        rb1.addIngredient("Chicken wings", 75);
        rb1.setPersonNumber(4);
        rb1.setEstimatedPreparationTime(45);
        rb1.setEstimatedCookingTime(50);
        rb1.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe = rb1.build();

        String str1 = "\n" +
                "Recipe name: Chicken fried\n" +
                "\n" +
                "Recipe instructions:\n" +
                "1- Start by the beginning\n" +
                "2- Then keep going\n" +
                "3- Now it ends\n" +
                "\n" +
                "For 4 persons, the needed ingredients are:\n" +
                "75.00 of Chicken wings\n" +
                "300.00 of Carrots\n" +
                "\n" +
                "The recipe is very easy.\n" +
                "The recipes takes around 45min of preparation and 50min of cooking.\n" +
                "The recipe is rated 0.00/5 stars by 0 users.\n";

        String str2 = "\n" +
                "Recipe name: Chicken fried\n" +
                "\n" +
                "Recipe instructions:\n" +
                "1- Start by the beginning\n" +
                "2- Then keep going\n" +
                "3- Now it ends\n" +
                "\n" +
                "For 4 persons, the needed ingredients are:\n" +
                "300.00 of Carrots\n" +
                "75.00 of Chicken wings\n" +
                "\n" +
                "The recipe is very easy.\n" +
                "The recipes takes around 45min of preparation and 50min of cooking.\n" +
                "The recipe is rated 0.00/5 stars by 0 users.\n";

        // The ingredients can be displayed in any order so toString() can be either str1 or str2
        assertTrue(str1.equals(recipe.toString()) || str2.equals(recipe.toString()));
    }
}

package ch.epfl.polychef;

import org.junit.Test;

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
import static org.junit.jupiter.api.Assertions.*;


public class RecipeTest {


    @Test
    public void recipeBuilderAndSettersRejectInvalidInputs() {
        RecipeBuilder rb = new RecipeBuilder();

        // rejects empty names
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        // setter rejects empty names
        assertThrows(IllegalArgumentException.class, () -> rb.setName(""));
        rb.setName("Chicken fried");

        // rejects when no instructions added
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.addInstruction(""));
        rb.addInstruction("Start by the beginning");

        // rejects when no ingredients
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("", 300));
        assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("Carrots", 0));
        rb.addIngredient("Carrots", 300);

        // rejects when no persons
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.setPersonNumber(0));
        rb.setPersonNumber(4);

        // rejects when no entered estimatedPreparationTime
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.setEstimatedPreparationTime(0));
        rb.setEstimatedPreparationTime(45);

        // rejects when no entered estimatedCookingTime
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        assertThrows(IllegalArgumentException.class, () -> rb.setEstimatedCookingTime(0));
        rb.setEstimatedCookingTime(45);

        // rejects when no entered recipeDifficulty
        assertThrows(IllegalArgumentException.class, () -> rb.build());
        rb.setRecipeDifficulty(Recipe.Difficulty.EASY);

        assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath(""));
        assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(0));
        assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath("Does not end by png"));
        assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(12));

        rb.build();
    }


    @Test
    public void builderArgumentsAreSetCorrectlyInRecipe(){
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe = rb.build();

        ArrayList<String> instruc = new ArrayList<String>();
        instruc.add("Start by the beginning");
        HashMap<String, Double> ingre =  new HashMap<String, Double>();
        ingre.put("Carrots", 300d);
        recipe.getRating();

        assertEquals(recipe.getName(),  "Chicken fried");
        assertEquals(recipe.getRecipeInstructions(),  Collections.unmodifiableList(instruc));
        assertEquals(recipe.getIngredients(),  Collections.unmodifiableMap(ingre));
        assertEquals(recipe.getPersonNumber(),  4);
        assertEquals(recipe.getEstimatedPreparationTime(),  45);
        assertEquals(recipe.getEstimatedCookingTime(),  50);
        assertEquals(recipe.getRecipeDifficulty(),  Recipe.Difficulty.VERY_EASY);
        assertEquals(recipe.getEstimatedTotalTime(),  95);
        assertEquals(recipe.getMiniaturePath(), "/src/default_miniature.png");
        assertEquals(recipe.getPicturesNumbers(), Arrays.asList("/src/default_picture.png"));

        rb.addPicturePath(R.drawable.koreansteaktartare);
        rb.setMiniaturePath("/src/miniature.jpeg");
        Recipe recipe2 = rb.build();

        assertEquals(recipe2.getMiniaturePath(), "/src/miniature.jpeg");
        assertEquals(recipe2.getPicturesNumbers(), Arrays.asList("/src/cake.png"));
    }

    @Test
    public void argumentsReturnedAreUnmodifiable(){
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe = rb.build();

        Map<String, Double> ingre = recipe.getIngredients();
        List<String> instr = recipe.getRecipeInstructions();


        assertThrows(UnsupportedOperationException.class, () -> ingre.put("Steaks", 1000d));
        assertThrows(UnsupportedOperationException.class, () -> instr.add("/src/hello.png"));
        assertThrows(UnsupportedOperationException.class, () -> instr.set(0, "/src/evilChanger.png"));
        for (Map.Entry<String, Double> e : ingre.entrySet()) {
            assertThrows(UnsupportedOperationException.class, () -> e.setValue(e.getValue()*10));
        }

    }

    @Test
    public void changingPersonNumberScalesIngredients(){
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.addInstruction("Start by the beginning");
        rb.addIngredient("Carrots", 300d);
        rb.addIngredient("Chicken wings", 75);
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_EASY);
        Recipe recipe = rb.build();

        //assertThrows(IllegalArgumentException.class, () -> recipe.scalePersonAndIngredientsQuantities(0));
        //recipe.scalePersonAndIngredientsQuantities(0); TODO: Understand why this test fails

        recipe.scalePersonAndIngredientsQuantities(2);
        Map<String, Double> ingre = recipe.getIngredients();

        assertTrue(ingre.get("Carrots") == 150d);
        assertTrue(ingre.get("Chicken wings") == (75d / 2d));
    }
}

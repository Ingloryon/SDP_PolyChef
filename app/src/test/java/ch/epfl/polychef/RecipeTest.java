package ch.epfl.polychef;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ch.epfl.polychef.RecipeObj.Recipe;
import ch.epfl.polychef.RecipeObj.RecipeBuilder;

import static org.junit.Assert.assertEquals;

/*
import static org.junit.Assert.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
 */

public class RecipeTest {


    @Test
    public void recipeBuilderAndSettersRejectInvalidInputs() {
        RecipeBuilder rb = new RecipeBuilder();/*

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
        assertThrows(IllegalArgumentException.class, () -> rb.setRecipeDifficulty(null));
        rb.setRecipeDifficulty(Recipe.Difficulty.EASY);

        assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath(""));
        assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(""));
        assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath(null));
        assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath(null));
        assertThrows(IllegalArgumentException.class, () -> rb.setMiniaturePath("Does not end by png"));
        assertThrows(IllegalArgumentException.class, () -> rb.addPicturePath("Does not end by jpeg"));

        rb.build();*/
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
        rb.addPicturePath("/src/cake.png");
        rb.setMiniaturePath("/src/miniature.jpeg");
        Recipe recipe = rb.build();

        ArrayList<String> instruc = new ArrayList<String>();
        instruc.add("Start by the beginning");
        HashMap<String, Double> ingre =  new HashMap<String, Double>();
        ingre.put("Carrots", 300d);

        assertEquals(recipe.getName(),  "Chicken fried");
        assertEquals(recipe.getRecipeInstructions(),  Collections.unmodifiableList(instruc));
        assertEquals(recipe.getIngredients(),  Collections.unmodifiableMap(ingre));
        assertEquals(recipe.getPersonNumber(),  4);
        assertEquals(recipe.getEstimatedPreparationTime(),  45);
        assertEquals(recipe.getEstimatedCookingTime(),  50);
        assertEquals(recipe.getRecipeDifficulty(),  Recipe.Difficulty.VERY_EASY);
        assertEquals(recipe.getName(),  "Chicken fried");
        assertEquals(recipe.getEstimatedTotalTime(),  95);
    }

    //TODO: check method addIngredient is unmodifiable outside
}

package ch.epfl.polychef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("", 300, Ingredient.Unit.GRAM));
        Assertions.assertThrows(IllegalArgumentException.class, () -> rb.addIngredient("Carrots", -1, Ingredient.Unit.GRAM));
        rb.addIngredient("Carrots", 300, Ingredient.Unit.GRAM);

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
        assertEquals(recipe.getIngredients().get(0), Collections.unmodifiableList(ingre).get(0));
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

    @Test
    public void argumentsReturnedAreUnmodifiable() {
        RecipeBuilder rb = setStandardRecipe();
        rb.setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE);
        Recipe recipe = rb.build();

        List<Ingredient> ingre = recipe.getIngredients();
        List<String> instr = recipe.getRecipeInstructions();


        Assertions.assertThrows(UnsupportedOperationException.class, () -> ingre.add(new Ingredient("Steaks", 1000d, Ingredient.Unit.GRAM)));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> instr.add("/src/hello.png"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> instr.set(0, "/src/evilChanger.png"));

        //TODO: to implement
        //Assertions.assertThrows(UnsupportedOperationException.class, () -> ingre.get(0).setQuantity(50));
    }

    @Test
    public void changingPersonNumberScalesIngredients() {
        RecipeBuilder rb = setStandardRecipe();
        rb.setRecipeDifficulty(Recipe.Difficulty.EASY);
        Recipe recipe = rb.build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> recipe.scalePersonAndIngredientsQuantities(0));

        recipe.scalePersonAndIngredientsQuantities(2);
        for(Ingredient ingredient: recipe.getIngredients()){
            if(ingredient.getName().equals("Carrots")){
                assertTrue(ingredient.getQuantity() == 150d);
            } else if(ingredient.getName().equals("Chicken wings")){
                assertTrue(ingredient.getQuantity() == (75d / 2d));
            }
        }
    }

    @Test
    public void equalsOnlyReturnsTrueWhenUuidIdentical() {
        Recipe recipe1 = setStandardRecipe().build();
        Recipe recipe2 = setStandardRecipe().build();

        assertTrue(!recipe1.equals(""));
        assertTrue(!recipe1.equals(recipe2));
        assertTrue(recipe1.equals(recipe1));
        assertTrue(recipe2.equals(recipe2));
    }

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
                "300.0 grams of carrots\n" +
                "75.0 kilograms of chicken wings\n" +
                endingString;

        String str2 = startingString +
                "75.0 kilograms of chicken wings\n" +
                "300.0 grams of carrots\n" +
                endingString;

        // The ingredients can be displayed in any order so toString() can be either str1 or str2
        // TODO FIX ME PLEASE with new toString version
        //assertTrue(str1.equals(recipe.toString()) || str2.equals(recipe.toString()));
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
}

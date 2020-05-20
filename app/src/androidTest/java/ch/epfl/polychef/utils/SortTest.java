package ch.epfl.polychef.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortTest {
    @Test
    public void sortByRateTest(){
        List<Miniatures> list = new ArrayList<>();
        User user1 = new User();
        user1.getRating().addOpinion("0",1);
        User user2 = new User();
        user2.getRating().addOpinion("1",5);
        User user3 = new User();
        user3.getRating().addOpinion("2",3);
        list.add(user1);
        list.add(user2);
        list.add(user3);
        Sort.sortByRate(list);
        assertEquals(user2, list.get(0));
    }

    @Test
    public void sortBySimilarityIngredient(){
        List<Miniatures> list = new ArrayList<>();
        RecipeBuilder rb = new RecipeBuilder();
        rb.setName("Chicken fried");
        rb.setAuthor("testUser@polychef.ch");
        rb.addInstruction("Start by the beginning");
        rb.setPersonNumber(4);
        rb.setEstimatedPreparationTime(45);
        rb.setEstimatedCookingTime(50);
        rb.setRecipeDifficulty(Recipe.Difficulty.VERY_HARD);
        Recipe recipe1 = rb.addIngredient("Carrots", 300d, Ingredient.Unit.NONE).build();
        Recipe recipe2 = rb.addIngredient("Chocolate", 300d, Ingredient.Unit.NONE).build();
        Recipe recipe3 = rb.addIngredient("Rat", 300d, Ingredient.Unit.NONE).build();
        list.add(recipe1);
        list.add(recipe2);
        list.add(recipe3);
        Sort.sortByIngredientSimilarity(list,"Chocolate");
        assertEquals(recipe2, list.get(0));
    }
}

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
        User user1 = new User();
        user1.getRating().addOpinion("0",1);
        User user2 = new User();
        user2.getRating().addOpinion("1",5);
        User user3 = new User();
        user3.getRating().addOpinion("2",3);
        User user4 = new User();
        user4.getRating().addOpinion("0",1);
        List<Miniatures> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);
        Sort.sortByRate(list);
        assertEquals(user2, list.get(0));
    }

    @Test
    public void sortBySimilarityIngredient(){
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
        Recipe recipe4 = rb.addIngredient("Rat", 300d, Ingredient.Unit.NONE).build();
        List<Miniatures> list = new ArrayList<>();
        list.add(recipe1);
        list.add(recipe2);
        list.add(recipe3);
        list.add(recipe4);
        Sort.sortByIngredientSimilarity(list,"Chocolate");
        assertEquals(recipe2, list.get(0));
    }

    @Test
    public void sortBySimilarity(){
        RecipeBuilder rb = new RecipeBuilder();
        rb.setEstimatedPreparationTime(4);
        rb.setEstimatedCookingTime(5);
        rb.setRecipeDifficulty(Recipe.Difficulty.HARD);
        rb.setAuthor("testUser@polychef.ch");
        rb.addInstruction("instruction");
        rb.addIngredient("Carrots", 300d, Ingredient.Unit.NONE);
        rb.setPersonNumber(1);
        Recipe recipe1 = rb.setName("Chicken fried").build();
        Recipe recipe2 = rb.setName("Chicken").build();
        Recipe recipe3 = rb.setName("Carrot").build();
        Recipe recipe4 = rb.setName("Carrot").build();
        List<Miniatures> list = new ArrayList<>();
        list.add(recipe1);
        list.add(recipe2);
        list.add(recipe3);
        list.add(recipe4);
        Sort.sortBySimilarity(list,"Chicken");
        assertEquals(recipe2, list.get(0));
    }
}

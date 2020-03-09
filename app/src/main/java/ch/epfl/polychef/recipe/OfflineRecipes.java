package ch.epfl.polychef.recipe;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;

public class OfflineRecipes {

    public static List<Recipe> offlineRecipes = new ArrayList<>();

    public static Recipe recipe1 = new RecipeBuilder()
            .setName("Oven-Baked Salmon")
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setEstimatedCookingTime(15)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(5)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe2 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe3 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe4 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe5 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe6 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe7 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe8 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe9 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe10 = new RecipeBuilder()
            .setName("")
            .setRecipeDifficulty(Recipe.Difficulty.INTERMEDIATE)
            .setEstimatedCookingTime(0)
            .setPersonNumber(0)
            .setEstimatedPreparationTime(0)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addIngredient("", 0, Ingredient.Unit.GRAM)
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addInstruction("")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    static{
        offlineRecipes.add(recipe1);
        offlineRecipes.add(recipe2);
        offlineRecipes.add(recipe3);
        offlineRecipes.add(recipe4);
        offlineRecipes.add(recipe5);
        offlineRecipes.add(recipe6);
        offlineRecipes.add(recipe7);
        offlineRecipes.add(recipe8);
        offlineRecipes.add(recipe9);
        offlineRecipes.add(recipe10);
    }
}

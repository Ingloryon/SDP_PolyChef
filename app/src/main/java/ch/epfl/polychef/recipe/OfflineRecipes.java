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
            .addIngredient("Salmon fillet, cut into 4 pieces", 12, Ingredient.Unit.OUNCE)
            .addIngredient("Coarse-grained salt", 0, Ingredient.Unit.NONE)
            .addIngredient("Freshly ground black pepper", 0, Ingredient.Unit.NONE)
            .addIngredient("Baked squash, for serving, optional", 0, Ingredient.Unit.NONE)
            .addInstruction("Preheat the oven to 450 degrees F. ")
            .addInstruction("Season salmon with salt and pepper. Place salmon, skin side down, on a non-stick baking sheet or in a non-stick pan with an oven-proof handle. Bake until salmon is cooked through, about 12 to 15 minutes. Serve with the Toasted Almond Parsley Salad and squash, if desired. ")
            .addPicturePath(R.drawable.koreansteaktartare)
            .build();

    public static Recipe recipe2 = new RecipeBuilder()
            .setName("Excellent MeatBalls")
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setEstimatedCookingTime(43)
            .setPersonNumber(4)
            .setEstimatedPreparationTime(40)
            .addIngredient("Extra-virgin olive oil", 0, Ingredient.Unit.NONE)
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

package ch.epfl.polychef.RecipeObj;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.polychef.Preconditions;

public final class RecipeBuilder {
    private String name = "";
    private String recipeInstructions = "";
    private HashMap<String, Double> ingredients = new HashMap<>();
    private int personNumber;
    private int estimatedTimeRequired;
    private Recipe.Difficulty recipeDifficulty;

    private String miniaturePath = "";
    private ArrayList<String> picturesPaths = new ArrayList<>();

    /**
     * Builds a Recipe
     * @return a Recipe with the characteristics given to the builder
     */
    public Recipe build(){
        // TODO: faire les verifs en fonction du constructeur de Recipe (privé / publique) et conditions minimales d'une recette


        return new Recipe(name, recipeInstructions, ingredients, personNumber, estimatedTimeRequired, recipeDifficulty, miniaturePath, picturesPaths);
    }

    /**
     * Set the name of the recipe
     * @param name name of the recipe, must be non empty
     * @return the modified builder
     */
    public RecipeBuilder setName(String name){
        Preconditions.checkArgument(name != null && !name.equals(""), "The name must be non empty");
        this.name = name;
        return this;
    }

    /**
     * Sets the instructions to follow the recipe
     * @param recipeInstructions the instructions to follow the recipe, must be non empty
     * @return the modified builder
     */
    public RecipeBuilder setRecipeInstructions(String recipeInstructions){
        Preconditions.checkArgument(recipeInstructions != null && !recipeInstructions.equals(""), "The instructions must be non empty");
        this.recipeInstructions = recipeInstructions;
        return this;
    }

    /**
     * Adds an ingredient of the recipe and its corresponding quantity
     * @param ingredientName the name of the ingredient, must be non empty
     * @param quantity the corresponding quantity of the ingredient, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder addIngredient(String ingredientName, double quantity){
        Preconditions.checkArgument(ingredientName != null && !ingredientName.equals(""), "The ingredient name must be non empty");
        Preconditions.checkArgument(quantity > 0, "The ingredient quantity must be strictly positive");

        ingredients.put(ingredientName, quantity);
        return this;
    }

    /**
     * Sets the number of persons the recipe is for
     * @param personNumber the number of persons, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setPersonNumber(int personNumber){
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");

        this.personNumber = personNumber;
        return this;
    }

    /**
     * Sets the estimated time required to complete the recipe
     * @param estimatedTimeRequired  estimated time required to complete the recipe, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setEstimatedTimeRequired(int estimatedTimeRequired){
        Preconditions.checkArgument(estimatedTimeRequired > 0, "The estimated time required must be strictly positive");

        this.estimatedTimeRequired = estimatedTimeRequired;
        return this;
    }

    /**
     * Sets the recipe's difficulty level
     * @param recipeDifficulty  the difficulty level, must be non null
     * @return the modified builder
     */
    public RecipeBuilder setRecipeDifficulty(Recipe.Difficulty recipeDifficulty){
        Preconditions.checkArgument(recipeDifficulty != null, "The difficulty must be non null");
        this.recipeDifficulty = recipeDifficulty;
        return this;
    }


    /**
     * Sets the path where to find the miniature
     * @param miniaturePath path to find the miniature
     * @return the modified builder
     */
    public RecipeBuilder setMiniaturePath(String miniaturePath){
        Preconditions.checkArgument(miniaturePath != null && !miniaturePath.equals(""), "The miniature path must be non empty"); //TODO: sanitization ?
        this.miniaturePath = miniaturePath;
        return this;
    }

    /**
     * Adds the path of an image of the meal
     * @param picturePaths path of an image
     * @return the modified builder
     */
    public RecipeBuilder addPicturePath(String picturePaths) {
        Preconditions.checkArgument(picturePaths != null && !picturePaths.equals(""), "The picture path must be non empty");
        this.picturesPaths.add(picturePaths);
        return this;
    }
}

package ch.epfl.polychef.RecipeObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.polychef.Preconditions;

public final class RecipeBuilder {
    private String name = "";
    private List<String> recipeInstructions = new ArrayList<>();
    private HashMap<String, Double> ingredients = new HashMap<>();
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;

    private String miniaturePath = "";
    private ArrayList<String> picturesPaths = new ArrayList<>();

    /**
     * Builds a Recipe
     * @return a Recipe with the characteristics given to the builder
     */
    public Recipe build(){
        Preconditions.checkArgument(!name.equals(""), "The name must be set");
        Preconditions.checkArgument(!recipeInstructions.isEmpty(), "There must be at least one instruction");
        for(String instr : recipeInstructions){
            Preconditions.checkArgument(!instr.equals(""), "An instruction can't be nothing");
        }
        Preconditions.checkArgument(ingredients.size()>0, "The recipe should have at least one ingredient");
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be set and can't be zero");
        Preconditions.checkArgument(estimatedPreparationTime > 0, "The estimated preparation time must be set");
        Preconditions.checkArgument(estimatedCookingTime > 0, "The estimated cooking time must be set");
        Preconditions.checkArgument(recipeDifficulty != null, "The recipe difficulty must be set");

        return new Recipe(name, recipeInstructions, ingredients, personNumber, estimatedPreparationTime, estimatedCookingTime, recipeDifficulty, miniaturePath, picturesPaths);
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
     * Sets a instruction to follow in the recipe
     * @param recipeInstruction the specific instruction in the recipe, must be non empty
     * @param stepNumber
     * @return the modified builder
     */
    public RecipeBuilder addInstruction(String recipeInstruction, int stepNumber){
        Preconditions.checkArgument(!recipeInstruction.isEmpty(), "The instruction must be non empty");
        Preconditions.checkArgument(stepNumber >= 0, "The step index can't be below zero");
        this.recipeInstructions.add(stepNumber, recipeInstruction);
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
     * Sets the estimated time required to prepare the recipe
     * @param estimatedPreparationTime  estimated time required to prepare the recipe, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setEstimatedPreparationTime(int estimatedPreparationTime){
        Preconditions.checkArgument(estimatedPreparationTime > 0, "The estimated time required must be strictly positive");

        this.estimatedPreparationTime = estimatedPreparationTime;
        return this;
    }

    /**
     * Sets the estimated time required to cook the recipe
     * @param estimatedCookingTime  estimated time required to cook the recipe, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setEstimatedCookingTime(int estimatedCookingTime){
        Preconditions.checkArgument(estimatedCookingTime > 0, "The estimated time required must be strictly positive");

        this.estimatedCookingTime = estimatedCookingTime;
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
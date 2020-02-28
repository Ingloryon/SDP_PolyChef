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
     *
     * @return
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
     *
     * @param recipeInstructions
     * @return
     */
    public RecipeBuilder setRecipeInstructions(String recipeInstructions){
        Preconditions.checkArgument(recipeInstructions != null && !recipeInstructions.equals(""), "The instructions must be non empty");
        this.recipeInstructions = recipeInstructions;
        return this;
    }

    public RecipeBuilder addIngredient(String ingredientName, double quantity){
        Preconditions.checkArgument(ingredientName != null && !ingredientName.equals(""), "The ingredient name must be non empty");
        Preconditions.checkArgument(quantity > 0, "The ingredient quantity must be strictly positive");

        ingredients.put(ingredientName, quantity);
        return this;
    }

    public RecipeBuilder setPersonNumber(int personNumber){
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");

        this.personNumber = personNumber;
        return this;
    }

    public RecipeBuilder setEstimatedTimeRequired(int estimatedTimeRequired){
        Preconditions.checkArgument(estimatedTimeRequired > 0, "The estimated time required must be strictly positive");

        this.estimatedTimeRequired = estimatedTimeRequired;
        return this;
    }

    public RecipeBuilder setRecipeDifficulty(Recipe.Difficulty recipeDifficulty){
        Preconditions.checkArgument(recipeDifficulty != null, "The difficulty must be non null");
        this.recipeDifficulty = recipeDifficulty;
        return this;
    }


    public RecipeBuilder setMiniaturePath(String miniaturePath){
        Preconditions.checkArgument(miniaturePath != null && !miniaturePath.equals(""), "The miniature path must be non empty"); //TODO: sanitization ?
        this.miniaturePath = miniaturePath;
        return this;
    }

    public RecipeBuilder addPicturePath(String picturePaths) {
        Preconditions.checkArgument(picturePaths != null && !picturePaths.equals(""), "The picture path must be non empty");
        this.picturesPaths.add(picturePaths);
        return this;
    }
}

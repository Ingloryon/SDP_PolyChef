package ch.epfl.polychef.RecipeObj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ch.epfl.polychef.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Recipe {

    public enum Difficulty {
        VERY_EASY, EASY, INTERMEDIATE, HARD
    }

    private String name;
    private List<String> recipeInstructions;
    private Map<String, Double> ingredients;
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Difficulty recipeDifficulty;
    private final Rating rating;

    // Having pictures and miniature is optional, if none is provided the default one should be displayed
    private boolean hasMiniature;
    private boolean hasPictures;
    private String miniaturePath;
    private List<String> picturesPaths;

    /**
     * Creates a new Recipe
     * @param name the title of the recipe, must be non empty
     * @param recipeInstructions the instructions to follow the recipe, must be non empty
     * @param ingredients a list of the ingredients the recipe needs and their corresponding quantities
     * @param personNumber the number of persons corresponding to the quantities indicated, must be strictly positive
     * @param estimatedPreparationTime the approximate time needed to prepare the recipe, in minutes (strictly positive)
     * @param estimatedCookingTime the approximate time needed to cook the recipe, in minutes (strictly positive)
     * @param recipeDifficulty the difficulty of the recipe
     * @param miniaturePath path to access the miniature image, provide empty string for default miniature
     * @param picturesPaths path to access the pictures of the recipe, provide empty list for default picture
     */
    protected Recipe(String name, List<String> recipeInstructions, HashMap<String,Double> ingredients, int personNumber, int estimatedPreparationTime, int estimatedCookingTime, Difficulty recipeDifficulty, String miniaturePath, ArrayList<String> picturesPaths){

        this.hasMiniature = !miniaturePath.isEmpty();
        this.hasPictures = picturesPaths.size()!=0;

        this.name = name;
        this.recipeInstructions = recipeInstructions;
        this.ingredients = new HashMap<>();
        this.ingredients.putAll(ingredients);  //No need for deep copy (comes from the builder where map/list aren't accessible)
        this.personNumber = personNumber;
        this.estimatedPreparationTime = estimatedPreparationTime;
        this.estimatedCookingTime = estimatedCookingTime;
        this.recipeDifficulty = recipeDifficulty;
        this.rating = new Rating();
        this.miniaturePath = miniaturePath;
        this.picturesPaths = new ArrayList<>();
        if(hasPictures) this.picturesPaths.addAll(picturesPaths);
    }

    /**
     * Changes the number of persons the recipe is meant for and updates the quantities accordingly
     * @param newPersonNumber: strictly positive integer
     */
    public void changePersonNumber(int newPersonNumber){
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");
        personNumber=newPersonNumber;

        double ratio = newPersonNumber / personNumber;
        for (Map.Entry<String, Double> e : ingredients.entrySet()) {
            e.setValue(e.getValue()*ratio);
        }
        //TOREMOVE: ingredients.replaceAll((k, v) -> v * ratio);  -> Cleaner but wrong java version ?
    }

    /**
     * Returns the total estimated time
     * @return total estimated time in minutes
     */
    public int estimatedTotalTime(){
        return estimatedCookingTime + estimatedPreparationTime;
    }

    /**
     * Returns a copy of the recipe instructions
     * @return list of instructions for the recipe
     */
    public List<String> getRecipeInstructions() {
        return Collections.unmodifiableList(recipeInstructions);
    }

    /**
     * Returns the name of the recipe
     * @return the name of the recipe
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ingredients map
     * @return the ingredients and their amounts
     */
    public Map<String, Double> getIngredients() {
        return Collections.unmodifiableMap(ingredients);
    }

    /**
     * Returns the current number of person for the recipe
     * @return the current number of person for the recipe
     */
    public int getPersonNumber() {
        return personNumber;
    }
    /**
     * Returns the estimated preparation time of the recipe
     * @return the estimated preparation time of the recipe
     */
    public int getEstimatedPreparationTime() {
        return estimatedPreparationTime;
    }
    /**
     * Returns the estimated cooking time of the recipe
     * @return the estimated cooking time of the recipe
     */
    public int getEstimatedCookingTime() {
        return estimatedCookingTime;
    }
    /**
     * Returns the recipe difficulty of the recipe
     * @return the recipe difficulty of the recipe
     */
    public Difficulty getRecipeDifficulty() {
        return recipeDifficulty;
    }
    /**
     * Returns the rating of the recipe
     * @return the rating of the recipe
     */
    public Rating getRating() {
        return rating;
    }


    // TODO: Add getters and setters for needed attributes
    // TODO : Redefine methods toString, equals, hash
    // TODO: how to differentiate two parts of the class' methods : the ones for the recipe owner that is only modifiable by him (change quantities, name, photos, ect...), the ones that are public (change nb of persons, comment, ...)
    // TODO: general remark: should we handle overflows (for total preparation time for example)
}

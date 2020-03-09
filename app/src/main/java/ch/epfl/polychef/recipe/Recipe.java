package ch.epfl.polychef.recipe;

import ch.epfl.polychef.Preconditions;
import ch.epfl.polychef.R;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class Recipe implements Serializable {

    public enum Difficulty {
        VERY_EASY, EASY, INTERMEDIATE, HARD, VERY_HARD
    }
    public enum Unit {
        TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM
    }

    private final UUID rUuid;
    private String name;
    private List<String> recipeInstructions;
    // private Map<String, Double> ingredients;
    private List<Ingredient> ingredients;

    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Difficulty recipeDifficulty;
    private final Rating rating;

    // Having pictures and miniature is optional, if none is provided the default one should be displayed
    private boolean hasPictures;
    // TODO should we have this ?
    private boolean hasMiniature;
    private String miniaturePath;
    private List<Integer> picturesNumbers;
    private static final String DEFAULT_MINIATURE_PATH = "/src/default_miniature.png";
    private static final List<Integer> DEFAULT_PICTURE_PATH = Arrays.asList(R.drawable.koreansteaktartare);

    /**
     * Creates a new Recipe.
     * @param name the title of the recipe, must be non empty
     * @param recipeInstructions the instructions to follow the recipe, must be non empty
     * @param ingredients a list of the ingredients the recipe needs and their corresponding quantities
     * @param personNumber the number of persons corresponding to the quantities indicated, must be strictly positive
     * @param estimatedPreparationTime the approximate time needed to prepare the recipe, in minutes (strictly positive)
     * @param estimatedCookingTime the approximate time needed to cook the recipe, in minutes (strictly positive)
     * @param recipeDifficulty the difficulty of the recipe
     * @param miniaturePath path to access the miniature image, provide empty string for default miniature
     * @param picturesNumbers path to access the pictures of the recipe, provide empty list for default picture
     */
    protected Recipe(String name, List<String> recipeInstructions, List<Ingredient> ingredients, int personNumber, int estimatedPreparationTime, int estimatedCookingTime, Difficulty recipeDifficulty, String miniaturePath, ArrayList<Integer> picturesNumbers){

        this.hasMiniature = !miniaturePath.isEmpty();
        this.hasPictures = picturesNumbers.size()!=0;

        this.rUuid = UUID.randomUUID();
        this.name = name;
        this.recipeInstructions = recipeInstructions;
        //this.ingredients = new HashMap<>();
        //this.ingredients.putAll(ingredients);  //No need for deep copy (comes from the builder where map/list aren't accessible)
        this.ingredients = new ArrayList<>(ingredients);
        this.personNumber = personNumber;
        this.estimatedPreparationTime = estimatedPreparationTime;
        this.estimatedCookingTime = estimatedCookingTime;
        this.recipeDifficulty = recipeDifficulty;
        this.rating = new Rating();
        this.miniaturePath = miniaturePath;
        this.picturesNumbers = new ArrayList<>();
        if(hasPictures) {
            this.picturesNumbers.addAll(picturesNumbers);
        }
    }

    /**
     * Changes the number of persons the recipe is meant for and updates the ingredients quantities accordingly.
     * @param newPersonNumber strictly positive integer
     */
    public void scalePersonAndIngredientsQuantities(int newPersonNumber){
//        Preconditions.checkArgument(newPersonNumber > 0, "The number of persons must be strictly positive");
//
//        double ratio = (double)newPersonNumber / (double)personNumber;
//        personNumber=newPersonNumber;
//
//        for (Map.Entry<String, Double> e : ingredients.entrySet()) {
//            e.setValue(e.getValue()*ratio);
//        }
//        //TO_REMOVE: ingredients.replaceAll((k, v) -> v * ratio);  -> Cleaner but wrong java version ?
        Preconditions.checkArgument(newPersonNumber > 0, "The number of persons must be strictly positive");
        double ratio = (double)newPersonNumber / (double)personNumber;
        personNumber=newPersonNumber;
        for(Ingredient ingredient : ingredients){
            ingredient.setQuantity(ingredient.getQuantity() * ratio);
        }
    }

    /**
     * Returns the total estimated time.
     * @return total estimated time in minutes
     */
    public int getEstimatedTotalTime(){
        return estimatedCookingTime + estimatedPreparationTime;
    }

    /**
     * Returns the name of the recipe.
     * @return the name of the recipe
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ingredients map.
     * @return the ingredients and their amounts
     */
    //public Map<String, Double> getIngredients() {
    //    return Collections.unmodifiableMap(ingredients);
    //}
    public List<Ingredient> getIngredients(){
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Returns a copy of the recipe instructions.
     * @return list of instructions for the recipe
     */
    public List<String> getRecipeInstructions() {
        return Collections.unmodifiableList(recipeInstructions);
    }

    /**
     * Returns the current number of person for the recipe.
     * @return the current number of person for the recipe
     */
    public int getPersonNumber() {
        return personNumber;
    }

    /**
     * Returns the estimated preparation time of the recipe.
     * @return the estimated preparation time of the recipe
     */
    public int getEstimatedPreparationTime() {
        return estimatedPreparationTime;
    }

    /**
     * Returns the estimated cooking time of the recipe.
     * @return the estimated cooking time of the recipe
     */
    public int getEstimatedCookingTime() {
        return estimatedCookingTime;
    }

    /**
     * Returns the recipe difficulty of the recipe.
     * @return the recipe difficulty of the recipe
     */
    public Difficulty getRecipeDifficulty() {
        return recipeDifficulty;
    }

    /**
     * Returns the rating of the recipe.
     * @return the rating of the recipe
     */
    public Rating getRating() {
        return rating;
    }

    /**
     * Returns the miniature path of the recipe.
     * @return the rating of the recipe
     */
    public String getMiniaturePath() {
        return hasMiniature ? miniaturePath : DEFAULT_MINIATURE_PATH;
    }

    /**
     * Returns the list of the pictures' numbers.
     * @return List of picture numbers
     */
    public List<Integer> getPicturesNumbers() {
        return hasPictures ? Collections.unmodifiableList(picturesNumbers) : DEFAULT_PICTURE_PATH;
    }

    /**
     * Returns the String representation of the unique id of the recipe.
     * @return string of recipe's unique id
     */
    public UUID getUuid(){
        return rUuid;
    }

    @Override
    public boolean equals(Object otherRecipe){
        if ( otherRecipe instanceof Recipe ){
            return ((Recipe) otherRecipe).getUuid().equals(this.rUuid) ;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("\nRecipe name: " + name + "\n\nRecipe instructions:");
        for(int i = 0 ; i < recipeInstructions.size() ; ++i){
            str.append("\n" + (i+1) + "- " + recipeInstructions.get(i));
        }
        str.append("\n\nFor " + personNumber + " persons, the needed ingredients are:");

        //for (Map.Entry<String, Double> e : ingredients.entrySet()) {
        //    str.append("\n" + String.format("%.2f", e.getValue()) + " of " + e.getKey());
        //}
        for (Ingredient ingredient : ingredients){
            str.append("\n");
            str.append(ingredient.toString());
        }
        str.append("\n\nThe recipe is " + recipeDifficulty.toString().toLowerCase().replaceAll("_", " ") + ".\n");
        str.append("The recipes takes around " + estimatedPreparationTime + "min of preparation and " + estimatedCookingTime + "min of cooking.\n");
        str.append("The recipe is rated " + rating.toString());

        return str.toString();
    }

    // TODO: Add setters for needed attributes -> how to differentiate two parts of the class' methods : the ones for the recipe owner that is only modifiable by him (change quantities, name, photos, ect...), the ones that are public (change nb of persons, comment, ...)
    // TODO: general remark: should we handle overflows ? (for total preparation time / scale quantities / huge strings for example)
    // TODO: Changer argument ingredients en Map<String: ingreName, Map<Double: quantity, String: unit>> ?
    // TODO: Or all the UUID setup isn't necessary and just using Object's equals def is enough ?
}

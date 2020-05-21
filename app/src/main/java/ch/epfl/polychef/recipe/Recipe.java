package ch.epfl.polychef.recipe;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.utils.Either;
import ch.epfl.polychef.utils.Preconditions;

public final class Recipe implements Serializable, Cloneable, Comparable<Recipe>, Miniatures {

    public Recipe(){
        recipeUuid=null;
        rating=null;
    }

    public enum Difficulty {
        VERY_EASY, EASY, INTERMEDIATE, HARD, VERY_HARD
    }

    private final String recipeUuid;
    private String name;
    private String date;
    private String author;
    private List<String> recipeInstructions;
    private List<Ingredient> ingredients;

    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Difficulty recipeDifficulty;
    private final Rating rating;

    private String recipeDatabaseKey;

    // Having pictures and miniature is optional, if none is provided the default one should be displayed
    private Either<String, Integer> miniaturePath;
    private List<String> picturesPath;
    public static final int DEFAULT_MINIATURE_PATH = R.drawable.default_miniature;

    /**
     * Creates a new Recipe.
     * @param name the title of the recipe, must be non empty
     * @param recipeInstructions the instructions to follow the recipe, must be non empty
     * @param ingredients a list of the ingredients the recipe needs and their corresponding quantities
     * @param personNumber the number of persons corresponding to the quantities indicated, must be strictly positive
     * @param estimatedPreparationTime the approximate time needed to prepare the recipe, in minutes (strictly positive)
     * @param estimatedCookingTime the approximate time needed to cook the recipe, in minutes (strictly positive)
     * @param recipeDifficulty the difficulty of the recipe
     * @param miniaturePath path to access the miniature image, provide {@code Either.none()} for default miniature
     * @param picturesPath path to access the pictures of the recipe, provide empty list for default picture
     */
    protected Recipe(String name, List<String> recipeInstructions, List<Ingredient> ingredients,
                     int personNumber, int estimatedPreparationTime, int estimatedCookingTime, Difficulty recipeDifficulty,
                     Either<String, Integer> miniaturePath, List<String> picturesPath, String author, String date){

        this.recipeUuid = UUID.randomUUID().toString();
        this.name = name;
        this.recipeInstructions = recipeInstructions;
        this.ingredients = new ArrayList<>(ingredients);
        this.personNumber = personNumber;
        this.estimatedPreparationTime = estimatedPreparationTime;
        this.estimatedCookingTime = estimatedCookingTime;
        this.recipeDifficulty = recipeDifficulty;
        this.rating = new Rating();
        this.miniaturePath = miniaturePath;
        if(this.miniaturePath == null) {
            this.miniaturePath = Either.none();
        }
        this.picturesPath = picturesPath;
        if(date == null) {
            this.date = RecipeStorage.getInstance().getCurrentDate();
        }else{
            this.date = date;
        }
        this.author = author;
        this.recipeDatabaseKey = "";
    }

    /**
     * Changes the number of persons the recipe is meant for and updates the ingredients quantities accordingly.
     * @param newPersonNumber strictly positive integer
     */
    public void scalePersonAndIngredientsQuantities(int newPersonNumber){
        Preconditions.checkArgument(newPersonNumber > 0, "The number of persons must be strictly positive");

        double ratio = (double)newPersonNumber / (double)personNumber;

        personNumber=newPersonNumber;
        for(Ingredient ingredient : ingredients){
            if(ingredient.getUnit() != Ingredient.Unit.NONE) {
                // in java, doubles do not overflow but stay stuck on "infinity" value
                ingredient.setQuantity(ingredient.getQuantity() * ratio);
            }
        }
    }

    /**
     * Returns the total estimated time.
     * @return total estimated time in minutes
     */
    public int getEstimatedTotalTime(){
        Preconditions.checkArgument(estimatedCookingTime + estimatedPreparationTime >= 0, "The added times are too big and lead to an overflow !");
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
     * Returns a copy of the list of the ingredients.
     * @return the ingredients (name, amount, unit)
     */
    public List<Ingredient> getIngredients(){
        List<Ingredient> ingredientsDeepCopy = new ArrayList<>();
        for(int i =0 ; i < ingredients.size() ; ++i){
            Ingredient ingre = ingredients.get(i);
            ingredientsDeepCopy.add(new Ingredient(ingre.getName(), ingre.getQuantity(), ingre.getUnit()));
        }

        return ingredientsDeepCopy;
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
     * Returns if it exists the miniature path of the recipe.
     * @return the rating of the recipe
     */
    public Either<String, Integer> getMiniaturePath() {
        return miniaturePath != null ? miniaturePath : Either.none();
    }

    /**
     * Returns the list of the pictures' path.
     * @return List of picture path
     */
    public List<String> getPicturesPath() {
        return picturesPath != null ? Collections.unmodifiableList(picturesPath) : new ArrayList<>();
    }

    /**
     * Returns the date the recipe was created at.
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the author of this recipe.
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the String representation of the unique id of the recipe.
     * @return string of recipe's unique id
     */
    public String getRecipeUuid(){
        return recipeUuid;
    }

    /**
     * Returns the key used by the database to store the recipe.
     * @return the key used by the database to store the recipe
     */
    @Exclude
    public String getKey(){
        return recipeDatabaseKey;
    }

    /**
     * Should only be used by Firebase.
     * Sets the key used by the database to store the recipe.
     * @param recipeDatabaseKey the new key for the recipe
     */
    @Exclude
    public void setKey(String recipeDatabaseKey){
        this.recipeDatabaseKey =recipeDatabaseKey;
    }

    @Override
    public boolean isUser() {
        return false;
    }

    @Override
    public boolean isRecipe() {
        return true;
    }

    @Override
    public boolean equals(Object otherRecipe){
        if ( otherRecipe instanceof Recipe ){
            return ((Recipe) otherRecipe).getRecipeUuid().equals(this.recipeUuid) ;
        }
        return false;
    }

    /**
     * Compare two recipes based on their date.
     *
     * @param other recipe we compare to
     * @return 1 if {@code this} recipe is older that the {@code other},
     *         -1 if {@code this} recipe is newer that the {@code other} and
 *              0 if the were posted at the same time
     */
    @Override
    public int compareTo(Recipe other) {
        Preconditions.checkArgument(other != null, "Cannot compare to a null recipe");
        return - getDate().compareTo(other.getDate());
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("\nRecipe name: " + name + "\n");
        str.append("\nRecipe author: " + author + "\n");
        str.append("\nRecipe instructions:");
        for(int i = 0 ; i < recipeInstructions.size() ; ++i){
            str.append("\n" + (i+1) + "- " + recipeInstructions.get(i));
        }
        str.append("\n\nFor " + personNumber + " persons, the needed ingredients are:");
        for (Ingredient ingredient : ingredients){
            str.append("\n");
            str.append(ingredient.toString());
        }
        str.append("\n\nThe recipe is " + recipeDifficulty.toString().toLowerCase().replaceAll("_", " ") + ".\n");
        str.append("The recipes takes around " + estimatedPreparationTime + "min of preparation and " + estimatedCookingTime + "min of cooking.\n");
        str.append("The recipe is rated " + rating.toString());

        return str.toString();
    }

    /*
     *  Here we override clone to make it usable in other classes
     */
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}

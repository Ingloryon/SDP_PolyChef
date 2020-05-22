package ch.epfl.polychef.recipe;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.utils.Either;
import ch.epfl.polychef.utils.Preconditions;

/**
 * The builder to construct the object {@link Recipe}.
 */
public final class RecipeBuilder {

    private String date;
    private String name = "";
    private String author = "";
    private List<String> recipeInstructions = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;

    private Either<String, Integer> miniaturePath = Either.none();
    private ArrayList<String> picturesName = new ArrayList<>();
    private static final String TAG = "RecipeBuilder";

    /**
     * Builds a Recipe.
     *
     * @return a Recipe with the characteristics given to the builder
     */
    public Recipe build() {
        Preconditions.checkArgument(!name.isEmpty(), "The name must be set");
        Preconditions.checkArgument(!recipeInstructions.isEmpty(), "There must be at least one instruction");
        Preconditions.checkArgument(ingredients.size() > 0, "The recipe should have at least one ingredient");
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be set and can't be zero");
        Preconditions.checkArgument(estimatedPreparationTime >= 0, "The estimated preparation time must be set");
        Preconditions.checkArgument(estimatedCookingTime >= 0, "The estimated cooking time must be set");
        Preconditions.checkArgument(recipeDifficulty != null, "The recipe difficulty must be set");
        Preconditions.checkArgument(!author.isEmpty(), "The author must be set");

        return new Recipe(name, recipeInstructions, ingredients, personNumber, estimatedPreparationTime, estimatedCookingTime, recipeDifficulty, miniaturePath, picturesName, author, date);
    }

    /**
     * Set the name of the recipe.
     *
     * @param name name of the recipe, must be non empty
     * @return the modified builder
     */
    public RecipeBuilder setName(@NonNull String name) {
        Preconditions.checkArgument(!name.isEmpty(), "The name must be non empty");
        this.name = name;
        return this;
    }

    /**
     * Set the date of the Recipe.
     *
     * @param date date of the recipe, must be not null
     * @return the modified builder
     */
    public RecipeBuilder setDate(@NonNull String date){
        Preconditions.checkArgument(!date.isEmpty(), "The date can't be empty");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try{
            formatter.parse(date);
        }catch(ParseException e){
            Log.e(TAG, "The date you wrote is not in the correct format");
        }
        this.date = date;
        return this;
    }

    /**
     * Sets a instruction to follow in the recipe.
     *
     * @param recipeInstruction the specific instruction in the recipe, must be non empty
     * @return the modified builder
     */
    public RecipeBuilder addInstruction(@NonNull String recipeInstruction) {
        Preconditions.checkArgument(!recipeInstruction.isEmpty(), "The instruction must be non empty");
        this.recipeInstructions.add(recipeInstruction);
        return this;
    }

    /**
     * Adds an ingredient of the recipe and its corresponding quantity.
     *
     * @param ingredientName the name of the ingredient, must be non empty
     * @param quantity the corresponding quantity of the ingredient, must be strictly positive
     * @param unit the corresponding unit of the ingredient's quantity, must be non null
     * @return the modified builder
     */
    public RecipeBuilder addIngredient(@NonNull String ingredientName, double quantity, @NonNull Ingredient.Unit unit) {
        //checks are performed in Ingredient's constructor
        ingredients.add(new Ingredient(ingredientName, quantity, unit));
        return this;
    }

    /**
     * Add an ingredient from an exiting one.
     *
     * @param ingredient an Ingredient
     * @return the recipe builder
     */
    public RecipeBuilder addIngredient(@NonNull Ingredient ingredient) {
        //checks are performed in Ingredient's constructor
        ingredients.add(new Ingredient(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit()));
        return this;
    }

    /**
     * Sets the number of persons the recipe is for.
     *
     * @param personNumber the number of persons, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setPersonNumber(int personNumber) {
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");

        this.personNumber = personNumber;
        return this;
    }

    /**
     * Sets the estimated time required to prepare the recipe.
     *
     * @param estimatedPreparationTime estimated time required to prepare the recipe, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setEstimatedPreparationTime(int estimatedPreparationTime) {
        Preconditions.checkArgument(estimatedPreparationTime >= 0, "The estimated time required must be strictly positive");

        this.estimatedPreparationTime = estimatedPreparationTime;
        return this;
    }

    /**
     * Sets the estimated time required to cook the recipe.
     *
     * @param estimatedCookingTime estimated time required to cook the recipe, must be strictly positive
     * @return the modified builder
     */
    public RecipeBuilder setEstimatedCookingTime(int estimatedCookingTime) {
        Preconditions.checkArgument(estimatedCookingTime >= 0, "The estimated time required must be positive");

        this.estimatedCookingTime = estimatedCookingTime;
        return this;
    }

    /**
     * Sets the recipe's difficulty level.
     *
     * @param recipeDifficulty the difficulty level, must be non null
     * @return the modified builder
     */
    public RecipeBuilder setRecipeDifficulty(@NonNull Recipe.Difficulty recipeDifficulty) {
        this.recipeDifficulty = recipeDifficulty;
        return this;
    }

    /**
     * Sets the path where to find the miniature.
     *
     * @param miniaturePath path to find the miniature, must be non-empty
     * @return the modified builder
     */
    public RecipeBuilder setMiniatureFromPath(@NonNull String miniaturePath) {
        Preconditions.checkArgument(!miniaturePath.isEmpty(), "The miniature path must be non empty");
        this.miniaturePath = Either.left(miniaturePath);
        return this;
    }

    /**
     * Sets the id of the miniature.
     *
     * @param miniatureId id the local miniature
     * @return the modified builder
     */
    @SuppressWarnings("WeakerAccess")
    public RecipeBuilder setMiniatureFromId(@NonNull Integer miniatureId) {
        this.miniaturePath = Either.right(miniatureId);
        return this;
    }

    /**
     * Adds the path of an image of the meal.
     *
     * @param pictureName String path of an image
     * @return the modified builder
     */
    public RecipeBuilder addPicturePath(@NonNull String pictureName) {
        Preconditions.checkArgument(!pictureName.isEmpty(), "Picture path should not be empty");
        this.picturesName.add(pictureName);
        return this;
    }

    /**
     * Set the author of the recipe.
     *
     * @param author author of the recipe, must be non empty
     * @return the modified builder
     */
    public RecipeBuilder setAuthor(@NonNull String author) {
        Preconditions.checkArgument(!author.isEmpty(), "The author must be non empty");
        this.author = author;
        return this;
    }
}

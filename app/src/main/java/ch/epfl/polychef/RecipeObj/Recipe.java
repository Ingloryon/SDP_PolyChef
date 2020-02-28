package ch.epfl.polychef.RecipeObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polychef.Preconditions;


public final class Recipe {

    // TODO: leave it in the class or refactor it elsewhere ?
    public enum Difficulty {
        VERY_EASY, EASY, INTERMEDIATE, HARD
    }

    private String name;
    private String recipeInstructions;
    private HashMap<String, Double> ingredients;
    private int estimatedTimeRequired; // TODO: separate it into preparation + cooking time ?
    private int personNumber;
    private final Rating rating;
    private Difficulty recipeDifficulty;

    // Having pictures and miniature is optional, if none is provided the default one should be displayed
    private boolean hasMiniature;
    private boolean hasPictures;
    private String miniaturePath;
    private ArrayList<String> picturesPaths;


    /**
     * Creates a new Recipe
     * @param name the title of the recipe
     * @param recipeInstructions the title of the recipe
     * @param ingredients a list of the ingredients the recipe needs and their corresponding quantities
     * @param personNumber the number of persons corresponding to the quantities indicated in the ingredients amounts
     * @param estimatedTimeRequired the approximate time needed to complete the recipe, in minutes
     * @param recipeDifficulty the difficulty of the recipe
     * @param miniaturePath path to access the miniature image, provide empty string
     * @param picturesPaths path to access the pictures of the recipe
     */
    public Recipe(String name, String recipeInstructions, HashMap<String,Double> ingredients, int personNumber, int estimatedTimeRequired, Difficulty recipeDifficulty, String miniaturePath, ArrayList<String> picturesPaths){

        Preconditions.checkArgument(name != null && recipeInstructions != null && miniaturePath != null && picturesPaths != null && ingredients != null && recipeDifficulty != null, "A recipe takes non null arguments !");
        Preconditions.checkArgument(!ingredients.isEmpty(), "There should be at least one ingredient in the recipe !");
        Preconditions.checkArgument(estimatedTimeRequired>0, "The estimated time must be strictly positive");
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");
        // TODO: Authorize empty names and descriptions "" ?

        this.name = name;
        this.recipeInstructions = recipeInstructions;
        this.picturesPaths = new ArrayList<>();
        this.miniaturePath = miniaturePath;
        this.ingredients = new HashMap<>();
        this.personNumber = personNumber;
        this.rating = new Rating();
        this.recipeDifficulty = recipeDifficulty;

        this.hasMiniature = miniaturePath.equals("");
        this.hasPictures = picturesPaths.size()!=0;

        this.ingredients.putAll(ingredients);  //TODO: Verify makes a deep copy, else use a for loop --> tests
        if(hasPictures) this.picturesPaths.addAll(picturesPaths); //TODO: Verify makes a deep copy, else use a for loop --> tests
        /*{
            for (int i = 0 ; i < picturesPaths.size() ; ++i){
                this.picturesPaths.add(picturesPaths.get(i));
            }
        }*/
    }

    // TODO: create another method to modify the number of persons but not the quantities -> if wrong estimation
    // TODO: how to differentiate two parts of the class' methods : the ones for the recipe owner that is only modifiable by him (change quantities, name, photos, ect...), the ones that are public (change nb of persons, comment, ...)


    /**
     * Changes the number of persons the recipe is for and updates the quantities accordingly
     * @param newPersonNumber: strictly positive integer
     */
    public void changePersonNumber(int newPersonNumber){
        Preconditions.checkArgument(personNumber > 0, "The number of persons must be strictly positive");
        personNumber=newPersonNumber;

        double ratio = newPersonNumber / personNumber;
        for (Map.Entry<String, Double> e : ingredients.entrySet()) {
            e.setValue(e.getValue()*ratio);
        }
        //ingredients.replaceAll((k, v) -> v * ratio);  -> Cleaner but wrong java version ?
    }


    // TODO : Redefine methods toString, equals, hash


}

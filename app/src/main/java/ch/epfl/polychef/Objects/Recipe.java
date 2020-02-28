package ch.epfl.polychef.Objects;

import java.util.ArrayList;
import java.util.HashMap;

import ch.epfl.polychef.Preconditions;

public final class Recipe {
    private String name;

    private HashMap<String, Double> ingredients;

    private boolean hasMiniature;
    private String miniaturePath;

    private boolean hasPictures;
    private ArrayList<String> picturesPaths;


    public Recipe(String name, String miniaturePath, ArrayList<String> picturesPaths, HashMap<String, Double> ingredients){
        Preconditions.checkArgument(name != null && miniaturePath != null && picturesPaths != null && ingredients != null, "A recipe takes non null arguments !");
        Preconditions.checkArgument(!ingredients.isEmpty(), "There should be at least one ingredient in the recipe !");

        this.name = name;
        this.picturesPaths = new ArrayList<>();
        this.miniaturePath = miniaturePath;
        this.ingredients = new HashMap<>();
        this.ingredients.putAll(ingredients);  // Verify makes a deep copy, else use a for loop --> tests

        this.hasMiniature = miniaturePath != "";
        this.hasPictures = picturesPaths.size()!=0;


        if(hasPictures){
            for (int i = 0 ; i < picturesPaths.size() ; ++i){
                this.picturesPaths.add(picturesPaths.get(i));
            }
        }







    }






}

package ch.epfl.polychef.recipe;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.Search;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.Similarity;

public class SearchRecipe extends Search<Recipe> {

    @Override
    protected String getTag() {
        return "SearchRecipe";
    }

    @Override
    protected String getDbName() {
        return RecipeStorage.DB_NAME;
    }

    @Override
    protected Recipe getValue(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Recipe.class);
    }

    private static final  SearchRecipe INSTANCE = new SearchRecipe();

    private SearchRecipe() {
    }

    public static SearchRecipe getInstance() {
        return INSTANCE;
    }

    /**
     * Get all the recipes that contains the {@code ingredient}.
     *
     * @param ingredient the ingredient to match
     * @param caller     the caller to call onSuccess and onFailure
     */
    public void searchRecipeByIngredient(String ingredient, CallHandler<List<Miniatures>> caller) {
        // this is just an example we should be able to apply more filter later
        search(ingredient, this::compareIngredient, caller);
    }

    /**
     * Get all the recipes that matches the {@code query}.
     *
     * @param query  the query to be matched
     * @param caller the caller to call onSuccess and onFailure
     */
    public void searchForRecipe(String query, CallHandler<List<Miniatures>> caller) {
        search(query, this::compareSimilarity, caller);
    }

    private boolean compareSimilarity(String query, Recipe value) {
        return Similarity.similarity(query,value.getName())>0.1;
    }

    /**
    private boolean compareName(String query, Recipe value) {
        String searchInput = query;
        searchInput = searchInput.toLowerCase();
        String name = value.getName().toLowerCase();
        return searchInput.contains(name) || name.contains(searchInput);
    }
     */

    private boolean compareIngredient(String ingredient, Recipe value) {
        for (String ing : value.getIngredients().stream().map(Ingredient::getName).collect(Collectors.toList())) {
            if (Similarity.similarity(ingredient,ing)>0.3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    @Override
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

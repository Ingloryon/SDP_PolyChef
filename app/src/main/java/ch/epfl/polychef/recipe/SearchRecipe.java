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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ch.epfl.polychef.CallHandler;

public class SearchRecipe {
    private static final String TAG = "Search Recipe";

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
    public void searchRecipeByIngredient(String ingredient, CallHandler<List<Recipe>> caller) {
        // this is just an example we should be able to apply more filter later
        searchRecipe(ingredient, this::compareIngredient, caller);
    }

    /**
     * Get all the recipes that matches the {@code query}.
     *
     * @param query  the query to be matched
     * @param caller the caller to call onSuccess and onFailure
     */
    public void searchForRecipe(String query, CallHandler<List<Recipe>> caller) {
        searchRecipe(query, this::compareName, caller);
    }

    private void searchRecipe(String query, BiFunction<String, Recipe, Boolean> comparator, CallHandler<List<Recipe>> caller) {
        DatabaseReference nameRef = getDatabase().getReference("recipe");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<Recipe> recipes = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Recipe value = d.getValue(Recipe.class);
                        if (comparator.apply(query, value)) {
                            recipes.add(value);
                        }
                    }
                    caller.onSuccess(recipes);
                } else {
                    caller.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                caller.onFailure();
            }
        });
    }

    private boolean compareName(String query, Recipe value) {
        String searchInput = query;
        searchInput = searchInput.toLowerCase();
        String name = value.getName().toLowerCase();
        return searchInput.contains(name) || name.contains(searchInput);
    }

    private boolean compareIngredient(String ingredient, Recipe value) {
        String searchInput = ingredient;
        searchInput = searchInput.toLowerCase();
        for (String ing : value.getIngredients().stream().map(Ingredient::getName).collect(Collectors.toList())) {
            if (searchInput.contains(ing.toLowerCase()) || ing.toLowerCase().contains(searchInput)) {
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
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

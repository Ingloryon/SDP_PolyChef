package ch.epfl.polychef.recipe;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.utils.Preconditions;

/**
 * Uploader and downloader of {@code Recipe} from the storage.
 */
@SuppressWarnings("WeakerAccess")
public class RecipeStorage implements Serializable  {

    public static final String TAG = "RecipeStorage";
    public static final String DB_NAME = "recipes";
    public static final String OLDEST_RECIPE = "2020/01/01 00:00:00";
    public static final String RECIPE_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    private static RecipeStorage INSTANCE = new RecipeStorage();

    private RecipeStorage(){
    }

    /**
     * Gets the instance of Recipe Storage.
     * @return the recipe storage instance
     */
    public static RecipeStorage getInstance(){
        return INSTANCE;
    }

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    /**
     *  Returns the search recipe instance.
     * @return the search recipe instance
     */
    public SearchRecipe getSearch(){
        return SearchRecipe.getInstance();
    }

    /**
     * Create a string for the current date.
     *
     * @return the current date
     */
    public String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat(RECIPE_DATE_FORMAT);
        Date date = new Date();
        return formatter.format(date);
    }

    /**
     * Update the recipe with the local changes.
     *
     * @param recipe the recipe to update
     */
    public void updateRecipe(@NonNull Recipe recipe) {
        getFirebaseDatabase()
                .getReference(RecipeStorage.DB_NAME).child(recipe.getKey())
                .setValue(recipe);
    }

    /**
     * Add a new {@code Recipe} to the storage.
     *
     * @param recipe the {@code Recipe to add}
     */
    public void addRecipe(@NonNull Recipe recipe) {
        DatabaseReference ref = getFirebaseDatabase().getReference(DB_NAME).push();
        recipe.setKey(ref.getKey());
        ref.setValue(recipe);
    }

    /**
     * Reads the Recipe with the given uuid.
     * @param uuid the uuid of the recipe to read, must be non null
     * @param ch the call handler of the recipe
     */
    public void readRecipeFromUuid(String uuid, @NonNull CallHandler<Recipe> ch){
        Preconditions.checkArgument(!uuid.isEmpty(), "The given uuid must be non empty.");

        getFirebaseDatabase()
                .getReference(DB_NAME)
                .orderByChild("recipeUuid")
                .equalTo(uuid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long nbChildren = dataSnapshot.getChildrenCount();

                        if(nbChildren == 1){

                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                getSingleRecipeFromSnapshot(child, ch);
                            }
                        } else {
                            Log.e(TAG, nbChildren + " recipes with uuid " + uuid);
                            ch.onFailure();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ch.onFailure();
                    }
                });
    }

    /**
     * Get {@code n} recipes posted during the given date interval.
     *
     * @param nbRecipes number of recipes to get
     * @param startDate oldest recipe to get
     * @param endDate most recent recipe to get
     * @param newest whether we want recent recipes or older recipes
     * @param caller the caller of this method
     */
    public void getNRecipes(int nbRecipes,@NonNull String startDate,@NonNull String endDate, boolean newest,@NonNull CallHandler<List<Miniatures>> caller){
        Preconditions.checkArgument(nbRecipes > 0, "Number of recipe to get should "
                + "be positive");
        Preconditions.checkArgument(startDate.compareTo(endDate) < 0);

        Query query = getFirebaseDatabase().getReference(DB_NAME)
                .orderByChild("date").startAt(startDate).endAt(endDate);

        if(newest){
            query = query.limitToFirst(nbRecipes);
        } else {
            query = query.limitToLast(nbRecipes);
        }

        listenerForListOfRecipes(query, caller);
    }

    /**
     * Adds a listener for the given query.
     * @param query the query to add a listener to, must be non null
     * @param ch the caller of this method
     */
    public void listenerForListOfRecipes(@NonNull Query query,@NonNull CallHandler<List<Miniatures>> ch){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getManyRecipeFromSnapshot(dataSnapshot, ch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ch.onFailure();
            }
        });
    }

    /**
     * Gets a single recipe from the given data snapshot.
     * @param snapshot the data snapshot
     * @param ch the call handler of the recipe
     */
    public void getSingleRecipeFromSnapshot(@NonNull DataSnapshot snapshot,@NonNull CallHandler<Recipe> ch){
        Recipe recipe = snapshot.getValue(Recipe.class);
        if (recipe == null) {
            ch.onFailure();
        } else {
            recipe.setKey(snapshot.getKey());
            ch.onSuccess(recipe);
        }
    }

    /**
     * Gets a multiple recipes from the given data snapshot.
     * @param dataSnapshot the data snapshot
     * @param ch the call handler of the recipe
     */
    public void getManyRecipeFromSnapshot(@NonNull DataSnapshot dataSnapshot,@NonNull CallHandler<List<Miniatures>> ch){
        if(dataSnapshot.getChildrenCount() == 0){
            ch.onFailure();

        } else {
            List<Miniatures> recipes = new ArrayList<>();
            for(DataSnapshot child : dataSnapshot.getChildren()){
                Recipe recipe = child.getValue(Recipe.class);
                Objects.requireNonNull(recipe).setKey(child.getKey());
                recipes.add(recipe);
            }

            ch.onSuccess(recipes);
        }
    }
}

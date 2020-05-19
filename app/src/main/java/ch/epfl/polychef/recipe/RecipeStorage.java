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

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.utils.Preconditions;

/**
 * Uploader and downloader of {@code Recipe} from the storage.
 */
public class RecipeStorage implements Serializable  {

    private static RecipeStorage INSTANCE=new RecipeStorage();

    private static final String TAG = "RecipeStorage";
    public static final String DB_NAME = "recipes";
    public static final String OLDEST_RECIPE = "2020/01/01 00:00:00";
    public static final String RECIPE_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    public static RecipeStorage getInstance(){
        return INSTANCE;
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


    private RecipeStorage(){
    }

    /**
     * Update the recipe with the local changes.
     *
     * @param recipe the recipe to update
     */
    public void updateRecipe(Recipe recipe) {
        Preconditions.checkArgument(recipe != null, "Can not update an empty recipe");
        getFirebaseDatabase()
                .getReference(RecipeStorage.DB_NAME).child(recipe.getKey())
                .setValue(recipe);
    }

    /**
     * Add a new {@code Recipe} to the storage.
     *
     * @param recipe the {@code Recipe to add}
     */
    public void addRecipe(Recipe recipe) {
        Preconditions.checkArgument(recipe != null);

        DatabaseReference ref = getFirebaseDatabase().getReference(DB_NAME).push();
        recipe.setKey(ref.getKey());
        ref.setValue(recipe);
    }

    public void readRecipeFromUuid(String uuid, CallHandler<Recipe> ch){
        Preconditions.checkArgument(uuid != null, "Uuid should not be null");
        Preconditions.checkArgument(ch != null, "Call handler should not be null");

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
    public void getNRecipes(int nbRecipes, String startDate, String endDate, boolean newest, CallHandler<List<Miniatures>> caller){
        Preconditions.checkArgument(nbRecipes > 0, "Number of recipe to get should "
                + "be positive");
        Preconditions.checkArgument(startDate != null);
        Preconditions.checkArgument(endDate != null);
        Preconditions.checkArgument(caller != null, "Call handler should not be null");
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

    //TODO Is it ok to pull all the recipes of a user? Or do we want to get them N by N as we scroll?
    /*public void getAllRecipesByUser(String userEmail, CallHandler<List<Recipe>> caller){
        Preconditions.checkArgument(userEmail != null);
        Preconditions.checkArgument(caller != null, "Call handler should not be null");

        Query query = getFirebaseDatabase().getReference(DB_NAME)
                .orderByChild("author").equalTo(userEmail);

        listenerForListOfRecipes(query, caller);
    }*/

    public void listenerForListOfRecipes(Query query, CallHandler<List<Miniatures>> ch){
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

    public void getSingleRecipeFromSnapshot(DataSnapshot snapshot, CallHandler<Recipe> ch){
        Recipe recipe = snapshot.getValue(Recipe.class);
        if (recipe == null) {
            ch.onFailure();
        } else {
            recipe.setKey(snapshot.getKey());
            ch.onSuccess(recipe);
        }
    }

    public void getManyRecipeFromSnapshot(DataSnapshot dataSnapshot, CallHandler<List<Miniatures>> ch){
        if(dataSnapshot.getChildrenCount() == 0){
            ch.onFailure();

        } else {
            List<Miniatures> recipes = new ArrayList<>();
            for(DataSnapshot child : dataSnapshot.getChildren()){
                Recipe recipe = child.getValue(Recipe.class);
                recipe.setKey(child.getKey());
                recipes.add((Miniatures)recipe);
            }

            ch.onSuccess(recipes);
        }
    }

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public SearchRecipe getSearch(){
        return SearchRecipe.getInstance();
    }
}

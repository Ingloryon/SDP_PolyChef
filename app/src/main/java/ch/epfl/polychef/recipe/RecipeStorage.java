package ch.epfl.polychef.recipe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.utils.Preconditions;

/**
 * Uploader and downloader of {@code Recipe} from the storage.
 */
public class RecipeStorage implements Serializable {

    private static RecipeStorage INSTANCE=new RecipeStorage();

    private static final String TAG = "Firebase";
    private static final String DB_NAME = "recipe";
    private static final String DB_ID = "id";
    private int id;

    public static RecipeStorage getInstance(){
        return INSTANCE;
    }

    private RecipeStorage(){
    }

    /**
     * Add a new {@code Recipe} to the storage.
     *
     * @param recipe the {@code Recipe to add}
     */
    public void addRecipe(Recipe recipe) {
        Preconditions.checkArgument(recipe != null);

        DatabaseReference idRef = getFirebaseDatabase().getReference(DB_ID);
        //Get the last ID used in the database
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value
                id = dataSnapshot.getValue(Integer.class);
                //Change the value of the ID in the database
                id += 1;
                idRef.setValue(id);
                DatabaseReference myRef = getFirebaseDatabase().getReference(DB_NAME);
                myRef.child(Integer.toString(id)).setValue(recipe);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void readRecipeFromUuid(String uuid, CallHandler<Recipe> ch){
        Preconditions.checkArgument(ch != null, "Call handler should not be null");

        getFirebaseDatabase()
                .getReference(DB_NAME)
                .orderByChild("recipeUuid")
                .equalTo(uuid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() == 1){
                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                getRecipeFromSnapshot(child, ch);
                            }
                        } else {
                            ch.onFailure();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ch.onFailure();
                    }
                });
    }

    public void getRecipeFromSnapshot(DataSnapshot snapshot, CallHandler<Recipe> ch){
        Recipe recipe = snapshot.getValue(Recipe.class);
        if (recipe == null) {
            ch.onFailure();
        } else {
            ch.onSuccess(recipe);
        }
    }

    public void getRecipeInDataBaseFromReference(DatabaseReference ref, CallHandler<Recipe> ch){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getRecipeFromSnapshot(dataSnapshot, ch);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                ch.onFailure();
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Get a {@code Recipe} from the storage.
     *
     * @param id the id of the {@code Recipe} to get
     * @param ch the {@code CallHandler} to call on success or failure
     */
    public void readRecipe(int id, CallHandler<Recipe> ch) {
        Preconditions.checkArgument(id > 0, "Id should be positive");
        Preconditions.checkArgument(ch != null, "Call handler should not be null");
        DatabaseReference myRef = getFirebaseDatabase().getReference(DB_NAME).child(Integer.toString(id));
        getRecipeInDataBaseFromReference(myRef, ch);
    }

    /**
     * Get {@code numberOfRecipes} recipes from the database starting at {@code fromId} and call the
     * {@code CallHandler} when all recipes are ready.
     *
     * @param numberOfRecipes the number of recipes to get
     * @param fromId          the start index from where to get the recipes
     * @param caller          the caller to call onSuccess
     */
    public void getNRecipes(int numberOfRecipes, int fromId, CallHandler<List<Recipe>> caller) {
        Preconditions.checkArgument(fromId > 0, "Id should be positive");
        Preconditions.checkArgument(numberOfRecipes > 0, "Number of recipe to get should "
                + "be positive");
        Preconditions.checkArgument(caller != null, "Call handler should not be null");
        getNRecipeQuery(numberOfRecipes, fromId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<Recipe> recipes = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        recipes.add(d.getValue(Recipe.class));
                    }
                    caller.onSuccess(recipes);
                } else {
                    caller.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                caller.onFailure();
            }
        });
    }

    /**
     * Get {@code numberOfRecipes} recipes from the database starting at {@code fromId} and notify the
     * {@code CallNotifier} when there is a recipe ready.
     *
     * @param numberOfRecipes the number of recipes to get
     * @param fromId          the start index from where to get the recipes
     * @param caller          the caller to notify when data are ready
     */
    public void getNRecipesOneByOne(int numberOfRecipes, int fromId, CallNotifier<Recipe> caller) {
        Preconditions.checkArgument(fromId > 0, "Id should be positive");
        Preconditions.checkArgument(numberOfRecipes > 0, "Number of recipe to get should "
                + "be positive");
        Preconditions.checkArgument(caller != null, "Call handler should not be null");
        getNRecipeQuery(numberOfRecipes, fromId).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                caller.notify(dataSnapshot.getValue(Recipe.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                caller.notify(dataSnapshot.getValue(Recipe.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String prevChildKey) {
                caller.notify(dataSnapshot.getValue(Recipe.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                caller.onFailure();
            }
        });
    }

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    private Query getNRecipeQuery(int numberOfRecipes, int fromId) {
        DatabaseReference myRef = getFirebaseDatabase().getReference(DB_NAME);
        return myRef.orderByKey().startAt(Integer.toString(fromId)).endAt(Integer.toString(fromId + numberOfRecipes - 1));
    }
}

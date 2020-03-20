package ch.epfl.polychef.recipe;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import java.util.function.Function;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Preconditions;
import ch.epfl.polychef.recipe.Recipe;

/**
 * Uploader and downloader of {@code Recipe} from the storage.
 */
public class RecipeStorage {
    private final String TAG = "Firebase";
    private int id;

    /**
     * Add a new {@code Recipe} to the storage.
     *
     * @param recipe the {@code Recipe to add}
     */
    public void addRecipe(Recipe recipe) {
        Preconditions.checkArgument(recipe != null);

        DatabaseReference idRef = getFirebaseDatabase().getReference("id");
        //Get the last ID used in the database
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value
                id = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //Change the value of the ID in the database
        id += 1;
        idRef.setValue(id);
        DatabaseReference myRef = getFirebaseDatabase().getReference("recipe");
        myRef.child(Integer.toString(id)).setValue(recipe);
    }

    /*public static void readRecipeFromFirebase(UUID Uuid){
        DatabaseReference idRef = firebaseInstance.getReference("id");
        //Get the last ID used in the database
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value
                id=dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        for(int i=1;i<id+1;i++) {
            DatabaseReference myRef = firebaseInstance.getReference("recipe").child(Integer.toString(i)).child("uuid");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    UUID value = dataSnapshot.getValue(UUID.class);
                    if (value.equals(Uuid)) {
                        myRef.getParent().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                                //call a method that display a recipe
                                Log.w(TAG, recipe.toString());
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }*/

    /**
     * Get a {@code Recipe} from the storage.
     *
     * @param id the id of the {@code Recipe} to get
     * @param ch the {@code CallHandler} to call on success or failure
     */
    public void readRecipe(int id, CallHandler<Recipe> ch) {
        DatabaseReference myRef = getFirebaseDatabase().getReference("recipe").child(Integer.toString(id));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Recipe value = dataSnapshot.getValue(Recipe.class);
                if (value == null) {
                    ch.onFailure();
                } else {
                    ch.onSuccess(value);
                }
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
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

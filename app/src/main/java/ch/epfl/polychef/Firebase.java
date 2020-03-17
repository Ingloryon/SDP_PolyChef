package ch.epfl.polychef;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;
import java.util.function.Function;

import ch.epfl.polychef.recipe.Recipe;

public class Firebase {
    private static final String TAG="Firebase";
    private static int id;

    public static void addRecipeToFirebase(Recipe recipe){
        //Get an instance of the firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idRef = database.getReference("id");
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
        //Change the value of the ID in the database
        idRef.setValue(id+1);
        id+=1;
        DatabaseReference myRef = database.getReference("recipe");
        myRef.child(Integer.toString(id)).setValue(recipe);
    }

    public static void readRecipeFromFirebase(UUID Uuid){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference idRef = database.getReference("id");
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
            DatabaseReference myRef = database.getReference("recipe").child(Integer.toString(i)).child("uuid");
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
    }
    public static void readRecipeFromFirebase(int id, FireHandler ch){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe").child(Integer.toString(id));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Recipe value = dataSnapshot.getValue(Recipe.class);
                ch.onSuccess(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                ch.onFailure();
            }
        });
    }
}

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
import ch.epfl.polychef.utils.Preconditions;

/**
 * Uploader and downloader of {@code Recipe} from the storage.
 */
public class RecipeStorage implements Serializable {

    private static RecipeStorage INSTANCE=new RecipeStorage();

    private static final String TAG = "Firebase";
    private static final String DB_NAME = "recipes";
    private static final String DB_ID = "id";
    public static final String OLDEST_RECIPE = "2020/01/01 00:00:00";

    public static RecipeStorage getInstance(){
        return INSTANCE;
    }

    /**
     * Create a string for the current date
     *
     * @return the current date
     */
    public String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
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
                getFirebaseDatabase().getReference(DB_NAME).push().setValue(recipe);
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
                                getSingleRecipeFromSnapshot(child, ch);
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

    public void getSingleRecipeFromSnapshot(DataSnapshot snapshot, CallHandler<Recipe> ch){
        Recipe recipe = snapshot.getValue(Recipe.class);
        if (recipe == null) {
            ch.onFailure();
        } else {
            ch.onSuccess(recipe);
        }
    }

    public void getNRecipes(int n, String startDate, String endDate, boolean newest, CallHandler<List<Recipe>> caller){
        Preconditions.checkArgument(endDate != null);
        Preconditions.checkArgument(n > 0, "Number of recipe to get should "
                + "be positive");
        Preconditions.checkArgument(caller != null, "Call handler should not be null");

        DatabaseReference myRef = getFirebaseDatabase().getReference(DB_NAME);

        Query query = myRef.orderByChild("date").startAt(startDate).endAt(endDate);

        if(newest){
            query = query.limitToFirst(n);
        } else {
            query = query.limitToLast(n);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    caller.onFailure();

                } else {
                    List<Recipe> recipes = new ArrayList<>();
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        recipes.add(child.getValue(Recipe.class));
                    }

                    caller.onSuccess(recipes);
                }
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
}

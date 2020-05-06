package ch.epfl.polychef.users;

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

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.utils.Similarity;

public class SearchUser {

    private static final String TAG = "SearchUser";

    private static final SearchUser INSTANCE = new SearchUser();

    private SearchUser() {
    }

    public static SearchUser getInstance() {
        return INSTANCE;
    }

    public void searchForUser(String query, CallHandler<List<Miniatures>> caller) {
        searchUser(query, this::compareSimilarity, caller);
    }

    private void searchUser(String query, BiFunction<String, User, Boolean> comparator, CallHandler<List<Miniatures>> caller) {
        DatabaseReference nameRef = getDatabase().getReference(UserStorage.DB_NAME);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<Miniatures> users = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        User value = d.getValue(User.class);
                        if (comparator.apply(query, value)) {
                            users.add(value);
                        }
                    }
                    caller.onSuccess(users);
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

    private boolean compareSimilarity(String query, User value) {
        return Similarity.similarity(query,value.getUsername())>0.1;
    }

    /**
    private boolean compareName(String query, User value) {
        String searchInput = query;
        searchInput = searchInput.toLowerCase();
        String name = value.getUsername().toLowerCase();
        return searchInput.contains(name) || name.contains(searchInput);
    }
     */

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

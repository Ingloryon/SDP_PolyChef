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
import ch.epfl.polychef.Search;
import ch.epfl.polychef.utils.Similarity;

public class SearchUser extends Search<User> {

    @Override
    protected String getTAG() {
        return "SearchUser";
    }

    @Override
    protected User getValue(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(User.class);
    }

    private static final SearchUser INSTANCE = new SearchUser();

    private SearchUser() {
    }

    public static SearchUser getInstance() {
        return INSTANCE;
    }

    public void searchForUser(String query, CallHandler<List<Miniatures>> caller) {
        search(query, this::compareSimilarity, caller);
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
    @Override
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

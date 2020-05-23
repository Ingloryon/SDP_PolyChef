package ch.epfl.polychef.users;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.Search;
import ch.epfl.polychef.utils.Similarity;

/**
 * A class representing the search of users, subclass of Search<T>.
 */
public class SearchUser extends Search<User> {

    private static final SearchUser INSTANCE = new SearchUser();

    /**
     * Gets the instance of the search user.
     * @return the instance of the search user
     */
    public static SearchUser getInstance() {
        return INSTANCE;
    }

    /**
     * Action that searches for the users corresponding to the query.
     * @param query the searched keywords
     * @param caller the caller of the method
     */
    public void searchForUser(String query, CallHandler<List<Miniatures>> caller) {
        search(query, this::compareSimilarity, caller);
    }

    @Override
    protected String getTag() {
        return "SearchUser";
    }

    @Override
    protected String getDbName() {
        return UserStorage.DB_NAME;
    }

    @Override
    protected User getValue(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(User.class);
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

    private SearchUser() {
    }

    private boolean compareSimilarity(String query, User value) {
        return Similarity.similarity(query,value.getUsername())>0.1;
    }
}

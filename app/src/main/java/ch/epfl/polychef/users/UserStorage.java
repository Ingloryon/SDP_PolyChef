package ch.epfl.polychef.users;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.utils.FavouritesUtils;
import ch.epfl.polychef.utils.Preconditions;

public class UserStorage {

    private static UserStorage INSTANCE = new UserStorage();
    public static final String DB_NAME = "users";

    private User user = null;
    private String userKey = null;

    public static UserStorage getInstance() {
        return INSTANCE;
    }

    private UserStorage() {
    }

    public void initializeUserFromAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();

        getDatabase()
                .getReference(UserStorage.DB_NAME)
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long childrenCount = dataSnapshot.getChildrenCount();

                        if (childrenCount == 0) {
                            initializeNewUser(email);

                        } else if (childrenCount == 1) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                initializeExistingUser(child);
                            }

                        } else {
                            throw new IllegalStateException("Inconsistent result: multiple user with the same email.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //TODO: Find good exception to throw
                        throw new IllegalArgumentException("Query cancelled");
                    }
                });
    }

    private void initializeNewUser(String email) {
        String username = getAuthenticatedUserName();
        user = new User(email, username);

        //TODO: Add OnSuccess and OnFailure listener
        DatabaseReference ref = getDatabase()
                .getReference(UserStorage.DB_NAME)
                .push();

        userKey=ref.getKey();
        user.setKey(ref.getKey());
        ref.setValue(user);
    }

    private void initializeExistingUser(DataSnapshot snap) {

        if (snap.exists()) {
            user = snap.getValue(User.class);
            user.removeNullFromLists();
            userKey = snap.getKey();
            user.setKey(snap.getKey());
            FavouritesUtils.getInstance().setOfflineFavourites(user);
        } else {
            //TODO: Find good exception to throw
            throw new IllegalArgumentException("Unable to reconstruct the user from the JSON.");
        }
    }

    public void updateUserInfo() {
        updateUserInfo(user, userKey);
    }

    /**
     * Update another {@code User}, even if this is not the connected user.
     * <p>
     * Warning: this method assume that {@link User#getKey()} will not return null <br>
     * (see {@link #getUserByEmail(String email, CallHandler caller)})
     * </p>
     *
     * @param other the other user
     */
    public void updateUserInfo(User other) {
        Preconditions.checkArgument(other != null, "User can not be null");
        updateUserInfo(other, other.getKey());
    }

    private void updateUserInfo(User userToUpdate, String userToUpdateKey) {
        if (userToUpdate != null && userToUpdateKey != null) {
            getDatabase()
                    .getReference(UserStorage.DB_NAME + "/" + userToUpdateKey)
                    .setValue(userToUpdate);
        } else {
            throw new IllegalStateException("The user has not been initialized");
        }
    }

    private String getAuthenticatedUserEmail() {
        return getAuthenticatedUser().getEmail();
    }

    private String getAuthenticatedUserName() {
        return getAuthenticatedUser().getDisplayName();
    }

    public FirebaseUser getAuthenticatedUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public User getPolyChefUser() {
        return user;
    }

    /**
     * Get a {@code User} from an email.
     *
     * @param email  the email of the user
     * @param caller the caller to call on success and failure
     */
    public void getUserByEmail(String email, CallHandler<User> caller) {
        getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 1) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                if (child.exists()) {
                                    User user = child.getValue(User.class);
                                    user.setKey(child.getKey());
                                    caller.onSuccess(user);
                                } else {
                                    caller.onFailure();
                                }
                            }
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
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }
}

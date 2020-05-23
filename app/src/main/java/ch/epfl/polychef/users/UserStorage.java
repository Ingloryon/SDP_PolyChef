package ch.epfl.polychef.users;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.CancellationException;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.utils.FavouritesUtils;
import ch.epfl.polychef.utils.Preconditions;

/**
 * The storage for users associated with Firebase.
 */
public class UserStorage {

    private static UserStorage INSTANCE = new UserStorage();
    public static final String DB_NAME = "users";

    private User user = null;
    private String userKey = null;

    private UserStorage() {
    }

    /**
     * Gets the instance of UserStorage.
     * @return the instance of UserStorage
     */
    public static UserStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the current polychef chef in storage.
     * @return the current polychef chef in storage
     */
    public User getPolyChefUser() {
        return user;
    }

    /**
     * Initializes all the user information from the authenticated user.
     * @param caller the caller of the method
     */
    public void initializeUserFromAuthenticatedUser(CallHandler<User> caller) {
        String email = getAuthenticatedUser().getEmail();

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
                            caller.onFailure();
                            throw new IllegalStateException("Inconsistent result: multiple user with the same email.");
                        }

                        //We have received the user, so we send it back to the caller
                        caller.onSuccess(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw new CancellationException("Query cancelled");
                    }
                });
    }

    /**
     * Forces the update of the stored user.
     */
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

    /**
     * Get a {@code User} from an email.
     *
     * @param email  the email of the user
     * @param caller the caller to call on success and failure
     */
    public void getUserByEmail(String email, CallHandler<User> caller) {
        getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(getUniqueUser(caller));
    }

    /**
     * Get a {@code User} by its ID in Firebase database.
     *
     * @param userID the id of the user
     * @param caller the caller to call on success and failure
     */
    public void getUserByID(String userID, CallHandler<User> caller) {
        getDatabase()
                .getReference(UserStorage.DB_NAME)
                .child(userID)
                .addListenerForSingleValueEvent(getUniqueUser(caller));
    }

    /**
     * Gets the unique user from the given data snapshot.
     * @param dataSnapshot the data snapshot to get the user from
     * @param caller the caller of the method
     */
    public void getUniqueUserFromDataSnapshot(@NonNull DataSnapshot dataSnapshot,@NonNull CallHandler<User> caller) {
        if (dataSnapshot.getChildrenCount() == 1) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                if (child.exists()) {
                    getUserFromDataSnapshot(child, caller);
                } else {
                    caller.onFailure();
                }
            }
        } else {
            getUserFromDataSnapshot(dataSnapshot, caller);
        }
    }

    /**
     * Get the current instance of the {@code FirebaseDatabase}.
     *
     * @return the current instance of the {@code FirebaseDatabase}
     */
    public FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    /**
     * Gets the current authenticated user in Firebase authentication.
     * @return the current authenticated user in Firebase authentication
     */
    public FirebaseUser getAuthenticatedUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Gets the search user instance.
     * @return the search user instance
     */
    public SearchUser getSearch() {
        return SearchUser.getInstance();
    }

    private void updateUserInfo(User userToUpdate, String userToUpdateKey) {
        if (userToUpdate == null || userToUpdateKey == null) {
            throw new IllegalStateException("The user has not been initialized");
        }

        getDatabase()
                .getReference(UserStorage.DB_NAME + "/" + userToUpdateKey)
                .setValue(userToUpdate);
    }

    private void initializeNewUser(String email) {
        String username = getAuthenticatedUser().getDisplayName();
        user = new User(email, username);

        // Add OnSuccess and OnFailure listener ?
        DatabaseReference ref = getDatabase()
                .getReference(UserStorage.DB_NAME)
                .push();

        userKey = ref.getKey();
        user.setKey(ref.getKey());
        ref.setValue(user);
    }

    private void initializeExistingUser(DataSnapshot snap) {
        Preconditions.checkArgument(snap.exists(), "Unable to reconstruct the user from the JSON.");

        user = snap.getValue(User.class);
        user.removeNullFromLists();
        userKey = snap.getKey();
        user.setKey(snap.getKey());
        FavouritesUtils.getInstance().setOfflineFavourites(user);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    private ValueEventListener getUniqueUser(CallHandler<User> caller) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getUniqueUserFromDataSnapshot(dataSnapshot, caller);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                caller.onFailure();
            }
        };
    }

    private void getUserFromDataSnapshot(DataSnapshot dataSnapshot, CallHandler<User> caller) {
        User user = dataSnapshot.getValue(User.class);
        if(user != null) {
            user.setKey(dataSnapshot.getKey());
            caller.onSuccess(user);
        } else {
            caller.onFailure();
        }
    }
}

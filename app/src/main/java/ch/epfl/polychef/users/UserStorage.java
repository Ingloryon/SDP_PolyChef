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

public class UserStorage {

    private static UserStorage INSTANCE=new UserStorage();

    private User user=null;
    private String userKey=null;

    public static UserStorage getInstance(){
        return INSTANCE;
    }

    private UserStorage(){
    }

    public void initializeUserFromAuthenticatedUser() {
        String email=getAuthenticatedUserEmail();

        getDatabase()
                .getReference("users")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long childrenCount = dataSnapshot.getChildrenCount();

                        if(childrenCount == 0) {
                            initializeNewUser(email);

                        } else if(childrenCount == 1) {
                            for(DataSnapshot child: dataSnapshot.getChildren()){
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
                .getReference("users")
                .push();

        ref.setValue(user);

        userKey = ref.getKey();
    }

    private void initializeExistingUser(DataSnapshot snap){

        if(snap.exists()){
            user = snap.getValue(User.class);
            user.removeNullFromLists();
            userKey = snap.getKey();
            FavouritesUtils.getInstance().setOfflineFavourites(user);
        } else {
            //TODO: Find good exception to throw
            throw new IllegalArgumentException("Unable to reconstruct the user from the JSON.");
        }
    }

    public void updateUserInfo() {
        if (user != null && userKey != null) {
            getDatabase()
                    .getReference("users/" + userKey)
                    .setValue(user);
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

    public void getUserByEmail(String email, CallHandler<User> caller) {
        getDatabase()
                .getReference("users")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() == 1) {
                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                if(child.exists()){
                                    caller.onSuccess(child.getValue(User.class));
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

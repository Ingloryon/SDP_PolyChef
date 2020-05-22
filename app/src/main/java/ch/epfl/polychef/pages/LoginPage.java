package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * The page where the user can login.
 */
public class LoginPage extends AppCompatActivity implements CallHandler<User> {
    private static final int RC_SIGN_IN = 123;

    /**
     * Creates the intent to sign in.
     * @param view the current view
     */
    public void createSignInIntent(View view) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Gets the firebase authentication current user instance.
     * @return the current user
     */
    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Gets the instance of the user storage.
     * @return the instance of the user storage
     */
    public UserStorage getUserStorage(){
        return UserStorage.getInstance();
    }

    @Override
    public void onFailure() {
        // see user story #214
    }

    @Override
    public void onSuccess(User data) {
        startActivity(new Intent(this, HomePage.class));
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SignInButton googleButton = findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                createSignInIntent(googleButton);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = getUser();
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK && user != null) {
            //prepares next activity
            getUserStorage().initializeUserFromAuthenticatedUser(this);
        } else {
            Toast.makeText(this, getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
        }
    }

}

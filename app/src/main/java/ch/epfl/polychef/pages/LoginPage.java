package ch.epfl.polychef.pages;

import android.content.Intent;
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

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;

public class LoginPage extends AppCompatActivity {

    SignInButton googleButton;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        googleButton = findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                createSignInIntent(googleButton);
            }
        });
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = getUser();
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK && user != null) {
            startActivity(new Intent(this, HomePage.class));
        } else {
            Toast.makeText(this, getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called when the user taps the log button.
     */
    public void tequilaLogin(View view) {
        createSignInIntent(view);
    }

    /**
     * Called when the user taps the log button.
     */
    public void googleLogin(View view) {
        createSignInIntent(view);
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}

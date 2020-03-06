package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginPage extends AppCompatActivity {

    Button tequilaButton;
    Button googleButton;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        tequilaButton = findViewById(R.id.tequilaButton);
        googleButton = findViewById(R.id.googleButton);

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

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                startActivity(new Intent(this, HomePage.class));
            } else {
                Toast.makeText(this, getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
            }
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
}

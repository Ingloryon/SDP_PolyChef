package ch.epfl.polychef.users;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polychef.pages.EntryPage;

/** 
 * Class for connected Activity that test on creation if the user is logged in.
 * It can also log out a user.
 */
public abstract class ConnectedActivity extends AppCompatActivity {

    /**
     * Gets the currently connected user.
     * @return the connected user
     */
    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Signs out the user, brings back on Entry page.
     */
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener( task -> startActivity(new Intent(getApplicationContext(), EntryPage.class)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected();
    }

    private void isConnected() {
        FirebaseUser user = getUser();
        if (user == null) {
            startActivity(new Intent(this, EntryPage.class));
        }
    }
}

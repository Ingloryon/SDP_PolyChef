package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.fragments.OfflineMiniaturesFragment;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class EntryPage extends AppCompatActivity implements CallHandler<User> {

    private Button logButton;
    private OfflineMiniaturesFragment miniFrag = new OfflineMiniaturesFragment();

    public static final String LOG_IN = "Log in";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_entry_fragment, miniFrag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_entry_fragment, miniFrag)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        goHomeIfConnected();
        logButton.setText(LOG_IN);
    }

    protected void goHomeIfConnected() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startActivity(new Intent(this, HomePage.class));
            UserStorage.getInstance().initializeUserFromAuthenticatedUser(this);
        }
    }

    /** Called when the user taps the log button. */
    public void login(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onSuccess(User data) {
        startActivity(new Intent(this, HomePage.class));
    }
}

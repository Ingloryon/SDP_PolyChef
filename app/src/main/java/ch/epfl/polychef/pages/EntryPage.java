package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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
        if(getFireBaseAuth().getCurrentUser() != null) {
            findViewById(R.id.autoLoginBackground).setVisibility(View.VISIBLE);
            findViewById(R.id.autoLoginProgress).setVisibility(View.VISIBLE);

            preventInteractions();

            Toast.makeText(this, "Auto connect" , Toast.LENGTH_SHORT).show();

            getUserStorage().initializeUserFromAuthenticatedUser(this);
        }
    }

    private void preventInteractions(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void allowInteractions(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
    public synchronized void onSuccess(User data) {
        try{
            wait(500);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        allowInteractions();

        findViewById(R.id.autoLoginProgress).setVisibility(View.GONE);
        findViewById(R.id.autoLoginBackground).setVisibility(View.GONE);
        startNextActivity();
    }

    public void startNextActivity(){
        startActivity(new Intent(this, HomePage.class));
    }

    public FirebaseAuth getFireBaseAuth(){
        return FirebaseAuth.getInstance();
    }

    public UserStorage getUserStorage(){
        return UserStorage.getInstance();
    }
}

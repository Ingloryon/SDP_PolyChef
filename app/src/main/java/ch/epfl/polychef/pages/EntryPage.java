package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.fragments.OfflineMiniaturesFragment;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * The page where the non-connected user is redirected.
 */
public class EntryPage extends AppCompatActivity implements CallHandler<User> {
    private Button logButton;
    private OfflineMiniaturesFragment miniFrag = new OfflineMiniaturesFragment();

    private static final String TAG = "EntryPage-TAG";
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
        
        setThemeLightDark();
    }

    private void setThemeLightDark() {
        SharedPreferences sharedPref = getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        String darkMode = sharedPref.getString("darkMode", "");
        if(!darkMode.isEmpty()) {
            if(darkMode.equals("true")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else if((getUiMode() & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Get the current UI mode configuration.
     *
     * @return the current UI mode configuration
     */
    protected int getUiMode() {
        return getResources().getConfiguration().uiMode;
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

    @Override
    public void onFailure() {
        Log.e(TAG, "Unable to initialise the PolyChef User");
        stopLoading();
    }

    @Override
    public synchronized void onSuccess(User data) {

        //Small delay to make it feel intentional
        try{
            wait(500);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        stopLoading();

        startNextActivity();
    }

    /**
     * Called when the user taps the log button.
     * @param view the view
     */
    public void login(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    /**
     * Starts the new activity, the home page.
     */
    public void startNextActivity(){
        startActivity(new Intent(this, HomePage.class));
    }

    /**
     * Returns the instance of the firebase authentication.
     * @return the instance of the firebase authentication
     */
    public FirebaseAuth getFireBaseAuth(){
        return FirebaseAuth.getInstance();
    }

    /**
     * Gets the user storage instance.
     * @return the user storage instance
     */
    public UserStorage getUserStorage(){
        return UserStorage.getInstance();
    }

    /**
     * Navigates towards the home page if the user is connected.
     */
    protected void goHomeIfConnected() {
        if(!isNetworkConnected()){
            Toast.makeText(this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        }else {
            if (getFireBaseAuth().getCurrentUser() != null) {

                startLoading();

                Toast.makeText(this, "Auto connect", Toast.LENGTH_SHORT).show();

                getUserStorage().initializeUserFromAuthenticatedUser(this);
            }
        }
    }

    private void startLoading(){
        findViewById(R.id.autoLoginBackground).setVisibility(View.VISIBLE);
        findViewById(R.id.autoLoginProgress).setVisibility(View.VISIBLE);

        preventInteractions();
    }

    private void stopLoading(){
        allowInteractions();

        findViewById(R.id.autoLoginProgress).setVisibility(View.GONE);
        findViewById(R.id.autoLoginBackground).setVisibility(View.GONE);
    }

    private void preventInteractions(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void allowInteractions(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @SuppressWarnings("ConstantConditions") //the null case is handled
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}

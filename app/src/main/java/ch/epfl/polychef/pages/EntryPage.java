package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.polychef.fragments.OfflineMiniaturesFragment;
import ch.epfl.polychef.R;

public class EntryPage extends AppCompatActivity {

    private Button logButton;

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

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        // Send the fragmentID of the fragment container to the recyclerView adapter
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_entry_fragment);
        OfflineMiniaturesFragment miniFrag = new OfflineMiniaturesFragment();
        miniFrag.setArguments(bundle);
        // Set the starting fragment inside the container with the miniatures
        fm.add(R.id.nav_entry_fragment, miniFrag);
        fm.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        goHomeIfConnected();
        logButton.setText(LOG_IN);
    }

    protected void goHomeIfConnected() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomePage.class));
        }
    }

    /** Called when the user taps the log button. */
    public void login(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

}

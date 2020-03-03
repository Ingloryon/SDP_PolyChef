package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;

import static androidx.navigation.fragment.NavHostFragment.findNavController;


public class HomePage extends AppCompatActivity {

    private Toolbar toolbar;
    private Button logButton;

    public static final String LOG_OUT = "Log out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        logButton.setText(LOG_OUT);
        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout(v);
            }
        });
    }

    /**
     * Called when the user taps the log button.
     */
    public void logout(View view) {
        Intent intent = new Intent(this, EntryPage.class);
        startActivity(intent);
    }


}

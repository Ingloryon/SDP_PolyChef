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

    public static final String LOG_OUT = "Log out";
    private Toolbar toolbar;
    private Button logButton;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavHostFragment hostFragment;
    private NavController navController;
    private MenuItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);
        drawer = findViewById(R.id.drawer);

        hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = findNavController(hostFragment);


        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem selectedItem) {
                        invalidateOptionsMenu();

                        if (currentItem != null) {
                            currentItem.setChecked(false);
                        }

                        selectedItem.setChecked(true);
                        currentItem = selectedItem;

                        int itemId = selectedItem.getItemId();
                        navController.navigate(getFragmentId(itemId));

                        drawer.closeDrawer(GravityCompat.START, true);

                        return false;
                    }
                }
        );

    }

    private int getFragmentId(int itemId) {
        if (itemId == R.id.nav_home) {
            return R.id.homeFragment;
        } else if (itemId == R.id.nav_fav) {
            return R.id.favouritesFragment;
        } else if (itemId == R.id.nav_subscribers) {
            return R.id.subscribersFragment;
        } else if (itemId == R.id.nav_subscriptions) {
            return R.id.subscriptionsFragment;
        } else {
            throw new IllegalArgumentException();
        }
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

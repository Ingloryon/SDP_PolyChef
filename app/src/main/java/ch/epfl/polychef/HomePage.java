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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;

public class HomePage extends AppCompatActivity {

    private Button logButton;
    private DrawerLayout drawer;

    private NavController navController;
    private MenuItem currentItem;


    public static final String LOG_OUT = "Log out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);
        drawer = findViewById(R.id.drawer);

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        navController = NavHostFragment.findNavController(hostFragment);

        setupNavigation();
    }
    
    private int getFragmentId(int itemId) {
        switch(itemId){
            case R.id.nav_home:
                return R.id.homeFragment;
            case R.id.nav_fav:
                return R.id.favouritesFragment;
            case R.id.nav_subscribers:
                return R.id.subscribersFragment;
            case R.id.nav_subscriptions:
                return R.id.subscriptionsFragment;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    private void setupNavigation(){
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem selectedItem) {

                        if (currentItem != null) {
                            currentItem.setChecked(false);
                        }

                        selectedItem.setChecked(true);
                        currentItem = selectedItem;

                        invalidateOptionsMenu();

                        int itemId = selectedItem.getItemId();
                        navController.navigate(getFragmentId(itemId));

                        drawer.closeDrawer(GravityCompat.START, true);

                        return false;
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        logButton.setText(LOG_OUT);
        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logout(view);
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

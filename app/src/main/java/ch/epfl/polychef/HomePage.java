package ch.epfl.polychef;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import ch.epfl.polychef.users.ConnectedActivity;

import com.google.android.material.navigation.NavigationView;

public class HomePage extends ConnectedActivity {

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

        drawer = findViewById(R.id.drawer);

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        navController = NavHostFragment.findNavController(hostFragment);

        // Create new Bundle containing the id of the container for the adapter
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_host_fragment);
        // Set this bundle to be an arguments of the startDestination using this trick
        navController.setGraph(R.navigation.nav_graph, bundle);
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button logButton = findViewById(R.id.logButton);
        logButton.setText(LOG_OUT);
        logButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private int getFragmentId(int itemId) {
        switch(itemId){
            case R.id.nav_home:
                return R.id.offlineMiniaturesFragment;
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
}

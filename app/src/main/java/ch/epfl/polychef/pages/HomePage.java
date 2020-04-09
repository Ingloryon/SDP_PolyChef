package ch.epfl.polychef.pages;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.ConnectedActivity;

import com.google.android.material.navigation.NavigationView;

import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class HomePage extends ConnectedActivity {

    private DrawerLayout drawer;

    private NavController navController;
    private NavigationView navView;
    private MenuItem currentItem;

    public static final String LOG_OUT = "Log out";
    private static final String TAG = "HomePage-TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //TODO should we let this line or not
        getUserStorage().initializeUserFromAuthenticatedUser();

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        navController = NavHostFragment.findNavController(hostFragment);

        navView = findViewById(R.id.navigationView);

        // Create new Bundle containing the id of the container for the adapter
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_host_fragment);

        // Set this bundle to be an arguments of the startDestination using this trick
        navController.setGraph(R.navigation.nav_graph, bundle);

        setupNavigation();
    }

    @Override
    public void onStart() {
        super.onStart();

        updateDrawerInfo(navView.getHeaderView(0));
    }

    public void updateDrawerInfo(View parentView) {
        ((TextView) parentView.findViewById(R.id.drawerEmailField)).setText(getUserStorage().getAuthenticatedUserEmail());
        ((TextView) parentView.findViewById(R.id.drawerUsernameField)).setText(getUserStorage().getAuthenticatedUserName());
    }

    public void setupUserProfileNavigation(View parentView){
        ImageView profileImage = parentView.findViewById(R.id.drawerProfileImage);

        profileImage.setOnClickListener((view) -> {
            setCurrentItemChecked(false);
            currentItem = null;
            navController.navigate(R.id.userProfileFragment);
            drawer.closeDrawer(GravityCompat.START, true);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button logButton = findViewById(R.id.logButton);
        logButton.setText(LOG_OUT);
        logButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                getUserStorage().updateUserInfo();
                signOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setCurrentItemChecked(false);

        int destination = navController.getCurrentDestination().getId();

        if(destination == R.id.userProfileFragment ||
                destination == R.id.fullRecipeFragment) {

            currentItem = null;
        } else {
            changeItem(navView.getMenu().findItem(getMenuItem(destination)));
        }

        drawer.closeDrawer(GravityCompat.START, true);
    }

    public void changeItem(MenuItem newItem){
        setCurrentItemChecked(false);
        currentItem = newItem;
        setCurrentItemChecked(true);
    }

    public void setCurrentItemChecked(Boolean bool){
        if(currentItem != null) {
            currentItem.setChecked(bool);
        }
    }

    private int getFragmentId(int itemId) {
        switch(itemId){
            case R.id.nav_home:
                return R.id.onlineMiniaturesFragment;
            case R.id.nav_fav:
                return R.id.favouritesFragment;
            case R.id.nav_subscribers:
                return R.id.subscribersFragment;
            case R.id.nav_subscriptions:
                return R.id.subscriptionsFragment;
            case R.id.nav_recipe:
                return R.id.postRecipeFragment;
            default:
                throw new IllegalArgumentException();
        }
    }

    private int getMenuItem(int fragmentId){
        switch(fragmentId){
            case R.id.onlineMiniaturesFragment:
                return R.id.nav_home;

            case R.id.favouritesFragment:
                return R.id.nav_fav;

            case R.id.subscribersFragment:
                return R.id.nav_subscribers;

            case R.id.subscriptionsFragment:
                return R.id.nav_subscriptions;

            case R.id.postRecipeFragment:
                return R.id.nav_recipe;

            default:
                throw new IllegalArgumentException();
        }
    }

    private void setupNavigation(){

        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem selectedItem) {

                        changeItem(selectedItem);

                        invalidateOptionsMenu();

                        Bundle bundle = new Bundle();
                        bundle.putInt("fragmentID", R.id.nav_host_fragment);

                        if(navController.getCurrentDestination().getId() != R.id.nav_host_fragment){
                            // This nav prevents to return to login screen when on home
                            navController.navigate(R.id.favouritesFragment, bundle);
                            // This returns to home frag so the navigation system can handle
                            HomePage.super.onBackPressed();
                        }

                        int itemId = selectedItem.getItemId();
                        navController.navigate(getFragmentId(itemId), bundle);

                        drawer.closeDrawer(GravityCompat.START, true);

                        return false;
                    }
                }
        );

        // TODO SHOULD WE LET THIS
        setupUserProfileNavigation(navView.getHeaderView(0));

        // TODO SHOULD WE LET THIS
        //Home should be checked initially
        currentItem = navView.getMenu().findItem(R.id.nav_home);
        currentItem.setChecked(true);
    }

    public UserStorage getUserStorage(){
        return UserStorage.getInstance();
    }

    public RecipeStorage getRecipeStorage(){
        return RecipeStorage.getInstance();
    }
}

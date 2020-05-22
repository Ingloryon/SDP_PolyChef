package ch.epfl.polychef.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.notifications.NotificationSender;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.ConnectedActivity;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * The main activity (HomePage) the user is on once connected.
 */
public class HomePage extends ConnectedActivity {
    private DrawerLayout drawer;
    private NavController navController;
    private NavigationView navView;
    private MenuItem currentItem;

    public static final String LOG_OUT = "Log out";
    public static final String TAG = "HomePage-TAG";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer);

        FragmentManager fragmentManager = getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        //noinspection ConstantConditions (the null case is handled, there is always a navController)
        navController = NavHostFragment.findNavController(hostFragment);
        navController.setGraph(R.navigation.nav_graph);

        navView = findViewById(R.id.navigationView);

        setupDrawer();

        if(getIntent().getExtras() != null) {
            Recipe toSendRecipe = (Recipe)getIntent().getExtras().getSerializable("RecipeToSend");
            if(toSendRecipe != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Recipe", toSendRecipe);
                navController.navigate(R.id.fullRecipeFragment, bundle);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button logButton = findViewById(R.id.logButton);
        logButton.setText(LOG_OUT);
        logButton.setOnClickListener( view -> {
            getUserStorage().updateUserInfo();
            signOut();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setCurrentItemChecked(false);


        //noinspection ConstantConditions (the null case is handled)
        int destination = navController.getCurrentDestination().getId();

        if(destination == R.id.userProfileFragment
                || destination == R.id.fullRecipeFragment
                || destination == R.id.rateRecipeFragment) {
            currentItem = null;
        } else {
            changeItem(navView.getMenu().findItem(getMenuItem(destination)));
        }

        drawer.closeDrawer(GravityCompat.START, true);
    }

    /**
     * Sets the profile picture of the connected user.
     */
    public void setupProfilePicture(){
        ImageView profileImage = navView.getHeaderView(0).findViewById(R.id.profilePicture);

        profileImage.setImageResource(User.getResourceImageFromUser(getPolychefUser()));
    }

    /**
     * Change the current item to the given menu item.
     * @param newItem the new menu item
     */
    public void changeItem(MenuItem newItem){
        setCurrentItemChecked(false);
        currentItem = newItem;
        setCurrentItemChecked(true);
    }

    /**
     * Check or uncheck the current menu item (depending on given boolean).
     * @param bool whether to check or uncheck the current menu item
     */
    public void setCurrentItemChecked(Boolean bool){
        if(currentItem != null) {
            currentItem.setChecked(bool);
        }
    }

    /**
     * Gets the instance of the user storage.
     * @return the instance of the user storage
     */
    public UserStorage getUserStorage(){
        return UserStorage.getInstance();
    }

    /**
     * Gets the instance of the recipe storage.
     * @return the instance of the recipe storage
     */
    public RecipeStorage getRecipeStorage(){
        return RecipeStorage.getInstance();
    }

    /**
     * Gets the instance of firebase.
     * @return the instance of firebase
     */
    public FirebaseDatabase getFireDatabase(){
        return FirebaseDatabase.getInstance();
    }

    /**
     * Gets the instance of the image storage.
     * @return the instance of the image storage
     */
    public ImageStorage getImageStorage(){
        return ImageStorage.getInstance();
    }

    /**
     * Gets the current connected user.
     * @return the current connected user
     */
    public User getPolychefUser(){
        return getUserStorage().getPolyChefUser();
    }

    /**
     * Gets the navigation controller.
     * @return the navigation controller
     */
    public NavController getNavController() {
        return navController;
    }

    /**
     * Determines whether the connection is active.
     * @return the connection state
     */
    public Boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //noinspection ConstantConditions (the null case is handled)
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Gets the instance of the notification sender.
     * @return the instance of the notification sender
     */
    public NotificationSender getNotificationSender() {
        return NotificationSender.getInstance();
    }

    private void setupDrawer(){
        View headerView = navView.getHeaderView(0);

        setupUserInfo(headerView);

        setupProfilePicture();

        setupProfileNavigation(headerView);

        setupNavigation();
    }

    private void setupUserInfo(View parentView) {
        ((TextView) parentView.findViewById(R.id.drawerEmailField)).setText(getPolychefUser().getEmail());
        ((TextView) parentView.findViewById(R.id.drawerUsernameField)).setText(getPolychefUser().getUsername());
    }

    private void setupProfileNavigation(View parentView){
        parentView.findViewById(R.id.profilePicture).setOnClickListener((view) -> {
            setCurrentItemChecked(false);
            currentItem = null;
            navController.navigate(R.id.userProfileFragment);
            drawer.closeDrawer(GravityCompat.START, true);
        });
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
        navView.setNavigationItemSelectedListener(selectedItem -> {
            changeItem(selectedItem);
            invalidateOptionsMenu();

            int itemId = selectedItem.getItemId();
            navController.navigate(getFragmentId(itemId));

            drawer.closeDrawer(GravityCompat.START, true);

            return false;
        } );

        //Home should be checked initially
        currentItem = navView.getMenu().findItem(R.id.nav_home);
        currentItem.setChecked(true);
    }

}

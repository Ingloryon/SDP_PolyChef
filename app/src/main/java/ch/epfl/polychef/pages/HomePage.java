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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.polychef.users.User;

public class HomePage extends ConnectedActivity {

    private DrawerLayout drawer;

    private NavController navController;
    private NavigationView navView;
    private MenuItem currentItem;
    private MenuItem previousItem;

    private RecipeStorage recipeStorage = new RecipeStorage();

    private User user;
    private User userToDisplay;
    private String userKey;

    public static final String LOG_OUT = "Log out";
    private static final String TAG = "HomePage-TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        String userEmail = getUserEmail();
        retrieveUserInfo(userEmail);

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

        bundle.putSerializable("RecipeStorage", getRecipeStorage());
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
        ((TextView) parentView.findViewById(R.id.drawerEmailField)).setText(getUserEmail());
        ((TextView) parentView.findViewById(R.id.drawerUsernameField)).setText(getUserName());
    }

    public void setupUserProfileNavigation(View parentView){
        ImageView profileImage = parentView.findViewById(R.id.drawerProfileImage);

        profileImage.setOnClickListener((view) -> {
            setCurrentItemChecked(false);
            currentItem = null;
            userToDisplay = user;
            navController.navigate(R.id.userProfileFragment);
            drawer.closeDrawer(GravityCompat.START, true);
        });
    }

    public User getUserToDisplay() {
        return userToDisplay;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button logButton = findViewById(R.id.logButton);
        logButton.setText(LOG_OUT);
        logButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                updateUserInfo();
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

                        int itemId = selectedItem.getItemId();

                        Bundle bundle = new Bundle();
                        bundle.putInt("fragmentID", R.id.nav_host_fragment);
                        bundle.putSerializable("RecipeStorage", getRecipeStorage());

                        navController.navigate(getFragmentId(itemId), bundle);

                        drawer.closeDrawer(GravityCompat.START, true);

                        return false;
                    }
                }
        );

        setupUserProfileNavigation(navView.getHeaderView(0));

        //Home should be checked initially
        currentItem = navView.getMenu().findItem(R.id.nav_home);
        currentItem.setChecked(true);
    }

    public RecipeStorage getRecipeStorage(){
        return recipeStorage;
    }

    protected void retrieveUserInfo(String email) {

        getDatabase()
                .getReference("users")
                .orderByChild("email")
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long childrenCount = dataSnapshot.getChildrenCount();

                        if(childrenCount == 0) {
                            newUser(email);

                        } else if(childrenCount == 1) {
                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                oldUser(child);
                            }

                        } else {
                            throw new IllegalStateException("Inconsistent result: multiple user with the same email.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //TODO: Find good exception to throw
                        throw new IllegalArgumentException("Query cancelled");
                    }
                });
    }

    protected void newUser(String email) {
        String username = getUserName();
        user = new User(email, username);

        //TODO: Integrate with the Firebase class
        //TODO: Add OnSuccess and OnFailure listener
        DatabaseReference ref = getDatabase()
                .getReference("users")
                .push();

        ref.setValue(user);

        userKey = ref.getKey();
    }

    protected void oldUser(DataSnapshot snap){

        if(snap.exists()){
            user = snap.getValue(User.class);
            userKey = snap.getKey();
        } else {
            //TODO: Find good exception to throw
            throw new IllegalArgumentException("Unable to reconstruct the user from the JSON.");
        }
    }

    protected void updateUserInfo(){
        getDatabase()
                .getReference("users/" + userKey)
                .setValue(user);
    }

    protected String getUserEmail() {
        return getUser().getEmail();
    }

    protected String getUserName() {
        return getUser().getDisplayName();
    }
    
    public FirebaseDatabase getDatabase(){
        return FirebaseDatabase.getInstance();
    }
}

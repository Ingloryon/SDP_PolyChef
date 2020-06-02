package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.gamification.Achievement;
import ch.epfl.polychef.gamification.AchievementsList;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;

/**
 * A simple {@link Fragment} subclass that represents the page of a user profile displayed.
 */
@SuppressWarnings("WeakerAccess")
public class UserProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment";
    public static final int NB_OF_RECIPES_LOADED_AT_A_TIME = 5;

    private HomePage hostActivity;
    private User userToDisplay;
    private List<Recipe> dynamicRecipeList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private ToggleButton toggleButton;

    private TextView noRecipeView;

    private boolean isLoading = false;
    private int currentIndex = 0;

    /**
     * Empty constructor for Firebase.
     */
    public UserProfileFragment() {
    }

    /**
     * Public User setter for Firebase.
     * @param user the user to display
     */
    @SuppressWarnings("unused")
    public UserProfileFragment(User user) {
        this.userToDisplay = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentIndex = 0;

        Bundle bundle = getArguments();
        if(bundle != null){
            userToDisplay = (User) bundle.getSerializable("User");
        }
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        noRecipeView = view.findViewById(R.id.no_recipe_user_text);

        userRecyclerView = view.findViewById(R.id.UserRecipesList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        userRecyclerView.setAdapter(
                new RecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, userRecyclerView,
                        container.getId(), hostActivity.getImageStorage(), hostActivity.getUserStorage()));

        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!isLoading && !recyclerView.canScrollVertically(1)){
                    getNextRecipes();
                }
            }
        });
        toggleButton = view.findViewById(R.id.subscribeButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!hostActivity.isOnline()){
            Toast.makeText(hostActivity, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        }

        currentIndex = 0;

        if (userToDisplay == null || userToDisplay.equals(hostActivity.getUserStorage().getPolyChefUser())) {
            toggleButton.setVisibility(View.GONE);
            userToDisplay = hostActivity.getUserStorage().getPolyChefUser();
        } else {
            toggleButton.setChecked(hostActivity.getUserStorage().getPolyChefUser().getSubscriptions().contains(userToDisplay.getEmail()));
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    hostActivity.getUserStorage().getPolyChefUser().addSubscription(userToDisplay.getEmail());
                    userToDisplay.addSubscriber(hostActivity.getUserStorage().getPolyChefUser().getEmail());
                    FirebaseMessaging.getInstance().subscribeToTopic("recipe_"+userToDisplay.getKey());
                } else {
                    hostActivity.getUserStorage().getPolyChefUser().removeSubscription(userToDisplay.getEmail());
                    userToDisplay.removeSubscriber(hostActivity.getUserStorage().getPolyChefUser().getEmail());
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("recipe_"+userToDisplay.getKey());
                }
                hostActivity.getUserStorage().updateUserInfo();
                hostActivity.getUserStorage().updateUserInfo(userToDisplay);
            });
        }

        ((TextView) requireView().findViewById(R.id.UserEmailDisplay)).setText(userToDisplay.getEmail());
        ((TextView) requireView().findViewById(R.id.UsernameDisplay)).setText(userToDisplay.getUsername());

        //Display the image of the user
        ImageView image = view.findViewById(R.id.profilePicture);
        image.setImageResource(User.getResourceImageFromUser(userToDisplay));

        determineAndDisplayAchievements(view);
        if(dynamicRecipeList.isEmpty()){
            getNextRecipes();
        }
        setupProfilePictureButton();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            hostActivity = (HomePage) context;
        } else {
            throw new IllegalArgumentException("The user profile fragment wasn't attached properly!");
        }
    }

    /**
     * Gets the next recipe for the current user.
     */
    public void getNextRecipes(){
        isLoading = true;
        int nbRecipes = userToDisplay.getRecipes().size();
        int threshold = Math.min(NB_OF_RECIPES_LOADED_AT_A_TIME + currentIndex, nbRecipes);
        int waitingFor = threshold - currentIndex;

        MultipleCallHandler<Recipe> handler = new MultipleCallHandler<Recipe>(waitingFor, (recipeList) -> {

            dynamicRecipeList.addAll(recipeList);
            dynamicRecipeList.sort(Recipe::compareTo);  //Sort from newest to oldest
            Objects.requireNonNull(userRecyclerView.getAdapter()).notifyDataSetChanged();
            currentIndex += waitingFor;
            isLoading = false;
        });

        for(int i = currentIndex; i < threshold; i++){
            String stringUuid = userToDisplay.getRecipes().get(nbRecipes - i - 1);
            hostActivity.getRecipeStorage().readRecipeFromUuid(stringUuid, handler);
        }
        if(userToDisplay.getRecipes().isEmpty()) {
            noRecipeView.setVisibility(View.VISIBLE);
        } else {
            noRecipeView.setVisibility(View.GONE);
        }
    }

    /**
     * Getter for the recycler view of the user.
     * @return the user recycler view
     */
    public RecyclerView getUserRecyclerView(){
        return userRecyclerView;
    }

    private void setupProfilePictureButton(){
        ImageView profilePict = requireView().findViewById(R.id.profilePicture);
        HomePage context = (HomePage) requireContext();

        profilePict.setOnClickListener(view -> {
            if(!hostActivity.isOnline()){
                Toast.makeText(hostActivity, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            }else {
                String userID = context.getUserStorage().getPolyChefUser().getKey();
                if (userToDisplay.getKey().equals(userID)) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("UserDisplayed", userToDisplay);

                    NavController navController = ((HomePage) requireActivity()).getNavController();
                    navController.navigate(R.id.userProfilePictureChoice, bundle);
                }
            }
        });
    }

    private void determineAndDisplayAchievements(View view){
        List<Achievement> achievementList = AchievementsList.getInstance().getAllAchievements();
        Context context= GlobalApplication.getAppContext();

        for (int i = 0 ; i < achievementList.size() ; ++i){
            Achievement achievement = achievementList.get(i);
            ImageView image = view.findViewById(getImageViewAchievementId(achievement.getName()));
            int achievementLevel = achievement.getLevel(userToDisplay);

            setAchievementToastOnClick(image, achievement.getLevelLabel(achievementLevel));

            int resourceImage = context.getResources().getIdentifier(achievement.getLevelImage(achievementLevel), "drawable", context.getPackageName());
            image.setImageResource(resourceImage);
        }
    }

    private int getImageViewAchievementId(String achievementName) {
        switch (achievementName) {
            case "cuistot":
                return R.id.cuistot_achievement;
            case "followed":
                return R.id.followed_achievement;
            case "favorite":
                return R.id.favorite_achievement;
            default:
                throw new IllegalArgumentException("There are no image view corresponding to this achievement name.");
        }
    }

    private void setAchievementToastOnClick(ImageView image, String achievementLabel) {
        image.setOnClickListener(view -> Toast.makeText(getActivity(), achievementLabel , Toast.LENGTH_SHORT).show());
    }

}

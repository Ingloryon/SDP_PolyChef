package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;


public class UserProfileFragment extends Fragment implements CallHandler<Recipe> {

    public UserProfileFragment() {
        // Required empty public constructor
    }
    private static final String TAG = "UserProfileFragment";
    private HomePage hostActivity;  //TODO use ConnectedActivity if possible
    private User userToDisplay;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private RecyclerView userRecyclerView;

    public static final int nbOfRecipesLoadedAtATime = 5;
    private boolean isLoading = false;
    private int waitingFor;

    private int currentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO add an isLoading variation

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentIndex = 0;
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userRecyclerView = view.findViewById(R.id.UserRecipesList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        userRecyclerView.setAdapter(
                new RecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, userRecyclerView,
                        container.getId(), hostActivity.getImageStorage()));

        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!isLoading && !recyclerView.canScrollVertically(1)){
                    getNextRecipes();
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentIndex = 0;

        userToDisplay = hostActivity.getUserStorage().getPolyChefUser();

        ((TextView) getView().findViewById(R.id.UserEmailDisplay)).setText(userToDisplay.getEmail());
        ((TextView) getView().findViewById(R.id.UsernameDisplay)).setText(userToDisplay.getUsername());

        getNextRecipes();
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

    @Override
    public void onSuccess(Recipe data) {
        --waitingFor;
        dynamicRecipeList.add(data);
        if(waitingFor == 0){
            dynamicRecipeList.sort(Recipe::compareTo);  //Sort from newest to oldest
            userRecyclerView.getAdapter().notifyDataSetChanged();
            isLoading = false;
        } else if(waitingFor < 0){
            Log.w(TAG, "Waiting for " + waitingFor);
        }
    }

    public void getNextRecipes(){
        isLoading = true;
        int nbRecipes = userToDisplay.getRecipes().size();
        int threshold = Math.min(nbOfRecipesLoadedAtATime + currentIndex, nbRecipes);

        waitingFor = threshold - currentIndex;
        for(int i = currentIndex; i < threshold; i++){
            String stringUuid = userToDisplay.getRecipes().get(nbRecipes - i - 1);
            hostActivity.getRecipeStorage().readRecipeFromUuid(stringUuid, UserProfileFragment.this);
        }
        currentIndex = threshold;
    }

    @Override
    public void onFailure() {
        --waitingFor;
    }

    public RecyclerView getUserRecyclerView(){
        return userRecyclerView;
    }
}

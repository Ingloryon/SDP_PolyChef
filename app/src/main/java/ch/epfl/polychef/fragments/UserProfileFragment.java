package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private HomePage hostActivity;  //TODO use ConnectedActivity if possible
    private RecipeStorage recipeStorage;
    private User userToDisplay;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private RecyclerView userRecyclerView;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private int currentIndex = 0;

    //private boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Check if this bundle is also part of this profile or only the startDestination profile
//        Bundle bundle = getArguments();
//        int fragmentID = bundle.getInt("fragmentID");

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        userRecyclerView = view.findViewById(R.id.UserRecipesList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        userRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, userRecyclerView, R.id.nav_host_fragment));

        userRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
//                    if(isLoading){
//                        return;
//                    }
                    for(int i = currentIndex; i < Math.min(nbOfRecipesLoadedAtATime + currentIndex, userToDisplay.getRecipes().size()); i++){
                        recipeStorage.readRecipeFromUUID(userToDisplay.getRecipes().get(i), UserProfileFragment.this);
                    }

                    currentIndex = Math.min(nbOfRecipesLoadedAtATime + currentIndex, userToDisplay.getRecipes().size());
                    //isLoading = true;
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userToDisplay = hostActivity.getUserToDisplay();

        ((TextView) getView().findViewById(R.id.UserEmailDisplay)).setText(userToDisplay.getEmail());
        ((TextView) getView().findViewById(R.id.UsernameDisplay)).setText(userToDisplay.getUsername());

        for(int i = 0; i < Math.min(nbOfRecipesLoadedAtATime, userToDisplay.getRecipes().size()); i++){
            recipeStorage.readRecipeFromUUID(userToDisplay.getRecipes().get(i), this);
        }
        currentIndex += Math.min(nbOfRecipesLoadedAtATime, userToDisplay.getRecipes().size());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            hostActivity = (HomePage) context;
            recipeStorage = hostActivity.getRecipeStorage();
        } else {
            throw new IllegalArgumentException("The user profile fragment wasn't attached properly!");
        }
    }

    @Override
    public void onSuccess(Recipe data) {
        //isLoading = false;
        dynamicRecipeList.add(data);
        userRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFailure() {
        //isLoading = false;
    }
}

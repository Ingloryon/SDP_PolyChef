package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;

import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.SearchRecipe;
import ch.epfl.polychef.users.SearchUser;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;
import ch.epfl.polychef.utils.UserMiniatureAdapter;

public class OnlineMiniaturesFragment extends Fragment implements CallHandler<List<Miniatures>>{

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;
    private RecyclerView userRecycleView;
    private static final int UP = -1;
    private static final int DOWN = 1;

    private SearchView searchView;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();
    private List<Recipe> searchRecipeList = new ArrayList<>();
    private List<User> searchUserList = new ArrayList<>();

    private String currentOldest;
    private String currentNewest;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;
    private boolean isSearching = false;

    private RecipeStorage recipeStorage;
    private ImageStorage imageStorage;
    private UserStorage userStorage;

    public OnlineMiniaturesFragment(){
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

        userRecycleView = view.findViewById(R.id.userList);
        userRecycleView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        UserMiniatureAdapter userMiniatureAdapter = new UserMiniatureAdapter(this.getActivity(),
                searchUserList, userRecycleView, container.getId(), imageStorage, userStorage);
        userRecycleView.setAdapter(userMiniatureAdapter);

        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecipeMiniatureAdapter adapter = new RecipeMiniatureAdapter(this.getActivity(),
                dynamicRecipeList, onlineRecyclerView, container.getId(), imageStorage, userStorage);

        onlineRecyclerView.setAdapter(adapter);
        // Add a scroll listener when we reach the end of the list we load new recipes from database
        onlineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if(!isLoading && !isSearching){
                    isLoading = true;
                    if(!recyclerView.canScrollVertically(DOWN)){
                        getNextRecipes();
                    } else if(!recyclerView.canScrollVertically(UP)){
                        getPreviousRecipes();
                    } else {
                        isLoading = false;
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            HomePage homePage = (HomePage) context;
            recipeStorage = homePage.getRecipeStorage();
            imageStorage = homePage.getImageStorage();
            userStorage = homePage.getUserStorage();
            Preconditions.checkArgument(recipeStorage != null && imageStorage != null && userStorage != null);
        } else {
            throw new IllegalArgumentException("The online miniature fragment wasn't attached properly!");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = getView().findViewById(R.id.searchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = true;
                searchView.clearFocus();
                searchRecipeList.clear();
                searchUserList.clear();
                userRecycleView.setVisibility(View.VISIBLE);
                ((RecipeMiniatureAdapter) onlineRecyclerView.getAdapter()).changeList(searchRecipeList);
                SearchRecipe.getInstance().searchForRecipe(query, OnlineMiniaturesFragment.this);
                SearchUser.getInstance().searchForUser(query, OnlineMiniaturesFragment.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchRecipeList.clear();
                searchUserList.clear();
                userRecycleView.setVisibility(View.INVISIBLE);
                ((RecipeMiniatureAdapter) onlineRecyclerView.getAdapter()).changeList(dynamicRecipeList);
                onlineRecyclerView.getAdapter().notifyDataSetChanged();
                isSearching = false;
                return false;
            }
        });
        if(dynamicRecipeList.isEmpty()) {
            initFirstNRecipes();
        }
    }

    private void initFirstNRecipes() {
        isLoading = true;
        dynamicRecipeList.clear();

        initDate();
        getNextRecipes();
    }

    private void initDate(){
        currentOldest = recipeStorage.getCurrentDate();
        currentNewest = recipeStorage.getCurrentDate();
    }

    private void getNextRecipes(){
        recipeStorage.getNRecipes(nbOfRecipesLoadedAtATime, recipeStorage.OLDEST_RECIPE, currentOldest, false, this);
    }

    private void getPreviousRecipes(){
        recipeStorage.getNRecipes(nbOfRecipesLoadedAtATime, currentNewest, recipeStorage.getCurrentDate(), true, this);
    }

    @Override
    public void onSuccess(List<Miniatures> data){

        if(isSearching){
            for (Miniatures miniature : data) {
                if(miniature.getClass().equals(Recipe.class)) {
                    searchRecipeList.add((Recipe)miniature);
                }else if(miniature.getClass().equals(User.class)){
                    searchUserList.add((User)miniature);
                }
            }
            Log.w(TAG, "list:"+searchUserList.toString());
            searchRecipeList.sort(Recipe::compareTo);
            onlineRecyclerView.getAdapter().notifyDataSetChanged();
            userRecycleView.getAdapter().notifyDataSetChanged();
            return;
        }

        List<Recipe> newRecipes = new ArrayList<>();
        for(Miniatures recipe : data){
            newRecipes.add((Recipe) recipe);
        }
        //Filter to avoid duplicates at the "edges"
        newRecipes = newRecipes.stream()
                .filter((recipe) -> !recipe.getDate().equals(currentOldest)
                        && !recipe.getDate().equals(currentNewest))
                .collect(Collectors.toList());

        dynamicRecipeList.addAll(newRecipes);
        dynamicRecipeList.sort(Recipe::compareTo);  //Sort from newest to oldest

        int size = dynamicRecipeList.size();
        if(size != 0){
            currentNewest = dynamicRecipeList.get(0).getDate();
            currentOldest = dynamicRecipeList.get(size - 1).getDate();
        } else {
            Log.w(TAG, "The list of recipes is empty despite adding some new recipes");
        }

        onlineRecyclerView.getAdapter().notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void onFailure() {
        isLoading = false;
        Log.w(TAG, "No Recipe found");
    }

    public RecyclerView getRecyclerView(){
        return onlineRecyclerView;
    }
}

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.SearchRecipe;

public class OnlineMiniaturesFragment extends Fragment implements CallNotifier<Recipe>, CallHandler<List<Recipe>> {

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;
    private static final int UP = -1;
    private static final int DOWN = 1;

    private SearchView searchView;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private String currentOldest;
    private String currentNewest;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;

    private RecipeStorage recipeStorage;
    private ImageStorage imageStorage;

    public OnlineMiniaturesFragment(){
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecipeMiniatureAdapter adapter = new RecipeMiniatureAdapter(this.getActivity(),
                dynamicRecipeList, onlineRecyclerView, container.getId(), imageStorage);

        onlineRecyclerView.setAdapter(adapter);
        // Add a scroll listener when we reach the end of the list we load new recipes from database
        onlineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if(!isLoading){
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
            if(recipeStorage == null || imageStorage == null){
                throw new IllegalStateException();
            }
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
                searchView.clearFocus();
                dynamicRecipeList.clear();
                SearchRecipe.getInstance().searchForRecipe(query, OnlineMiniaturesFragment.this);
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
                initFirstNRecipes();
                return false;
            }
        });
        initFirstNRecipes();
    }

    private void initFirstNRecipes() {
        isLoading = true;
        dynamicRecipeList.clear();

        initDate();
        getNextRecipes();
    }

    private synchronized void initDate(){
        try{
            //To make sure a newly posted recipe will would still be displayed
            wait(1001);
        } catch (InterruptedException interrupt) {

        } finally {
            currentOldest = recipeStorage.getCurrentDate();
            currentNewest = recipeStorage.getCurrentDate();
        }
    }

    private void getNextRecipes(){
        recipeStorage.getNRecipes(nbOfRecipesLoadedAtATime, recipeStorage.OLDEST_RECIPE, currentOldest, false, this);
    }

    private void getPreviousRecipes(){
        recipeStorage.getNRecipes(nbOfRecipesLoadedAtATime, currentNewest, recipeStorage.getCurrentDate(), true, this);
    }

    @Override
    public void notify(Recipe data) {
        dynamicRecipeList.add(data);
        onlineRecyclerView.getAdapter().notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void onSuccess(List<Recipe> data) {


        List<Recipe> newRecipes = data.stream()
                .filter((recipe) -> !recipe.getDate().equals(currentOldest)
                    && !recipe.getDate().equals(currentNewest))
                .collect(Collectors.toList());

        //Sort the recipes from newest to oldest
        Collections.reverse(newRecipes);

        if(newRecipes.size() != 0){

            String nextOldest = newRecipes.get(newRecipes.size() - 1).getDate();
            String nextNewest = newRecipes.get(0).getDate();

            if(leftIsOlder(nextOldest, currentOldest)){
                currentOldest = nextOldest;
                dynamicRecipeList.addAll(newRecipes);

            } else if(leftIsOlder(currentNewest, nextNewest)){
                currentNewest = nextNewest;
                dynamicRecipeList.addAll(0, newRecipes);

            } else {
                Log.d(TAG, "Don't know were to add the recipes");
            }

            onlineRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            Log.d(TAG, "no recipes to add");
        }

        isLoading = false;
    }

    private boolean leftIsOlder(String left, String right){
        return left.compareTo(right) < 0;
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

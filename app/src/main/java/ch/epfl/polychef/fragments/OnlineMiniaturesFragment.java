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
import java.util.Comparator;
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
import ch.epfl.polychef.utils.MiniatureAdapter;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;

public class OnlineMiniaturesFragment extends Fragment implements CallHandler<List<Miniatures>>{

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;
    private static final int UP = -1;
    private static final int DOWN = 1;

    private String actualQuery;

    private SearchView searchView;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();
    private List<Miniatures> searchList = new ArrayList<>();

    private MiniatureAdapter searchAdapter;
    private RecipeMiniatureAdapter adapter;

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

        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter = new RecipeMiniatureAdapter(this.getActivity(),
                dynamicRecipeList, onlineRecyclerView, container.getId(), imageStorage, userStorage);

        searchAdapter = new MiniatureAdapter(this.getActivity(),
                searchList, onlineRecyclerView, container.getId(), imageStorage, userStorage);

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
                actualQuery = query;
                searchView.clearFocus();
                searchList.clear();
                onlineRecyclerView.setAdapter(searchAdapter);
                ((MiniatureAdapter) onlineRecyclerView.getAdapter()).changeList(searchList);
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
                searchList.clear();
                onlineRecyclerView.setAdapter(adapter);
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
            Comparator<Miniatures> myComparator = (o1, o2) -> {
                String s1;
                String s2;
                if(o1.getClass().equals(Recipe.class)){
                    s1 = ((Recipe)o1).getName();
                }else{
                    s1 = ((User)o1).getUsername();
                }
                if(o2.getClass().equals(Recipe.class)){
                    s2 = ((Recipe)o2).getName();
                }else{
                    s2 = ((User)o2).getUsername();
                }
                if(similarity(s1,actualQuery)>similarity(s2,actualQuery)){
                    return -1;
                }else if(similarity(s1,actualQuery)==similarity(s2,actualQuery)){
                    return 0;
                }else {
                    return 1;
                }
            };
            searchList.addAll(data);
            Collections.sort(searchList,myComparator);
            onlineRecyclerView.getAdapter().notifyDataSetChanged();
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

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0;}
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }


    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}

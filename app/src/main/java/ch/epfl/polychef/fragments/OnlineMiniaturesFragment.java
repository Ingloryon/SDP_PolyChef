package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.MiniatureAdapter;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;
import ch.epfl.polychef.utils.Sort;

public class OnlineMiniaturesFragment extends Fragment implements CallHandler<List<Miniatures>>{

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;
    private static final int UP = -1;
    private static final int DOWN = 1;
    private static final int FILTER_RECIPE = 0;
    private static final int FILTER_USER = 1;
    private static final int FILTER_INGREDIENT = 2;
    private static final int FILTER_RATE = 3;

    private boolean isFilterIngredient = false;
    private boolean isFilterRate = false;
    private boolean isFilterUser = false;
    private boolean isFilterRecipe = false;

    private String actualQuery;

    private SearchView searchView;
    private LinearLayout filters;
    private Button ingredientsFilter;
    private Button usersFilter;
    private Button recipesFilter;
    private Button rateFilter;

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

        if(searchList.isEmpty()) {
            onlineRecyclerView.setAdapter(adapter);
        }else{
            onlineRecyclerView.setAdapter(searchAdapter);
        }
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
        filters = getView().findViewById(R.id.filters);
        ingredientsFilter = getView().findViewById(R.id.filter_ingre);
        recipesFilter = getView().findViewById(R.id.filter_recipe);
        usersFilter = getView().findViewById(R.id.filter_users);
        rateFilter = getView().findViewById(R.id.filter_rate);

        ingredientsFilter.setOnClickListener(setFilter(FILTER_INGREDIENT));
        usersFilter.setOnClickListener(setFilter(FILTER_USER));
        recipesFilter.setOnClickListener(setFilter(FILTER_RECIPE));
        rateFilter.setOnClickListener(setFilter(FILTER_RATE));

        if(!searchList.isEmpty()){
            filters.setVisibility(View.VISIBLE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = true;
                setAllBoolToFalse();
                filters.setVisibility(View.VISIBLE);
                actualQuery = query;
                searchView.clearFocus();
                searchList.clear();
                onlineRecyclerView.setAdapter(searchAdapter);
                ((MiniatureAdapter) onlineRecyclerView.getAdapter()).changeList(searchList);
                recipeStorage.getSearch().searchForRecipe(query, OnlineMiniaturesFragment.this);
                userStorage.getSearch().searchForUser(query, OnlineMiniaturesFragment.this);
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
                filters.setVisibility(View.GONE);
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

    private View.OnClickListener setFilter(int filter){
        return v -> {
            searchList.clear();
            if(filter == FILTER_RECIPE){
                setAllBoolToFalse();
                isFilterRecipe = true;
                recipeStorage.getSearch().searchForRecipe(actualQuery, OnlineMiniaturesFragment.this);
            }else if(filter == FILTER_USER){
                setAllBoolToFalse();
                isFilterUser = true;
                userStorage.getSearch().searchForUser(actualQuery, OnlineMiniaturesFragment.this);
            }else if (filter == FILTER_INGREDIENT){
                setAllBoolToFalse();
                isFilterIngredient = true;
                recipeStorage.getSearch().searchRecipeByIngredient(actualQuery, OnlineMiniaturesFragment.this);
            }else {
                isFilterRate = true;
                if(isFilterUser){
                    userStorage.getSearch().searchForUser(actualQuery, OnlineMiniaturesFragment.this);
                }else if(isFilterRecipe){
                    recipeStorage.getSearch().searchForRecipe(actualQuery, OnlineMiniaturesFragment.this);
                }else if(isFilterIngredient){
                    recipeStorage.getSearch().searchRecipeByIngredient(actualQuery, OnlineMiniaturesFragment.this);
                }else {
                    userStorage.getSearch().searchForUser(actualQuery, OnlineMiniaturesFragment.this);
                    recipeStorage.getSearch().searchForRecipe(actualQuery, OnlineMiniaturesFragment.this);
                }
            }
        };
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
            searchList.addAll(data);
            if(isFilterRate) {
                Sort.sortByRate(searchList);
            }else if(isFilterIngredient){
                Sort.sortByIngredientSimilarity(searchList,actualQuery);
            }else {
                Sort.sortBySimilarity(searchList,actualQuery);
            }
            removeDuplicate(searchList);
            onlineRecyclerView.getAdapter().notifyDataSetChanged();
            return;
        }

        List<Recipe> newRecipes = new ArrayList<>();
        for(Miniatures recipe : data){
            newRecipes.add((Recipe) recipe);
        }
        //Filter to avoid duplicates at the "edges"
        newRecipes = newRecipes.stream().filter((recipe) -> !recipe.getDate().equals(currentOldest)
                        && !recipe.getDate().equals(currentNewest)).collect(Collectors.toList());

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

    private void removeDuplicate(List<Miniatures> miniatures){
        List<Miniatures> toBeRemoved = new ArrayList<>();
        for(int i=0; i<miniatures.size()-1; i++){
            for(int j=i+1; j<miniatures.size(); j++){
                if(miniatures.get(i).getClass().equals(miniatures.get(j).getClass())){
                    if(miniatures.get(i).equals(miniatures.get(j))) {
                        toBeRemoved.add(miniatures.get(j));
                    }
                }
            }
        }
        miniatures.removeAll(toBeRemoved);
    }

    private  void setAllBoolToFalse(){
        isFilterRate = false;
        isFilterIngredient = false;
        isFilterRecipe = false;
        isFilterUser = false;
    }
}

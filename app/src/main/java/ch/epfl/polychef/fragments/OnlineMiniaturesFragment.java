package ch.epfl.polychef.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.MiniatureAdapter;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.Sort;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Class that represents the fragment displayed for the online Miniatures.
 */
@SuppressWarnings("ConstantConditions") //the null cases are handled locally
public class OnlineMiniaturesFragment extends Fragment implements CallHandler<List<Miniatures>>{

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;
    private static final int UP = -1;
    private static final int DOWN = 1;

    private enum Filter {RECIPE, USER, INGREDIENT, RATE}

    private String actualQuery;

    private SearchView searchView;
    private ConstraintLayout filters;

    private Map<Filter, Button> filterButtons = new HashMap<>(4);
    private Map<Filter, Boolean> filterStates = new HashMap<>(4);

    private List<Miniatures> dynamicRecipeList = new ArrayList<>();
    private List<Miniatures> searchList = new ArrayList<>();

    private String currentOldest;
    private String currentNewest;

    private boolean isLoading = false;
    private boolean isSearching = false;

    private RecipeStorage recipeStorage;
    private ImageStorage imageStorage;
    private UserStorage userStorage;
    private HomePage hostActivity;

    //package protected is enough for this constant
    static final int NB_OF_RECIPES_LOADED_AT_A_TIME = 5;

    /**
     * Required empty public constructor for Firebase.
     */
    public OnlineMiniaturesFragment(){
    }

    /**
     * Gets the recycler view of the online miniatures.
     * @return the online recycler view
     */
    public RecyclerView getRecyclerView(){
        return onlineRecyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        List<Miniatures> listToDisplay = searchList.isEmpty() ? dynamicRecipeList : searchList;

        MiniatureAdapter searchAdapter = new MiniatureAdapter(this.getActivity(),
                listToDisplay, onlineRecyclerView, imageStorage, userStorage);

        onlineRecyclerView.setAdapter(searchAdapter);

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
            hostActivity = (HomePage) context;
            recipeStorage = hostActivity.getRecipeStorage();
            imageStorage = hostActivity.getImageStorage();
            userStorage = hostActivity.getUserStorage();
            Preconditions.checkArgument(recipeStorage != null && imageStorage != null && userStorage != null);
        } else {
            throw new IllegalArgumentException("The online miniature fragment wasn't attached properly!");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!hostActivity.isOnline()){
            Toast.makeText(hostActivity, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        }

        searchView = requireView().findViewById(R.id.searchBar);
        filters = requireView().findViewById(R.id.filters);

        if(!searchList.isEmpty()){
            filters.setVisibility(View.VISIBLE);
        }

        setupFilters();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                isSearching = true;
                filters.setVisibility(View.VISIBLE);
                actualQuery = query;
                searchView.clearFocus();
                searchList.clear();
                ((MiniatureAdapter) Objects.requireNonNull(onlineRecyclerView.getAdapter())).changeList(searchList);
                search();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
                filters.setVisibility(View.GONE);
                searchList.clear();
                resetFilters();
                ((MiniatureAdapter) Objects.requireNonNull(onlineRecyclerView.getAdapter())).changeList(dynamicRecipeList);
                onlineRecyclerView.getAdapter().notifyDataSetChanged();
                isSearching = false;
                return false;
        });

        if(dynamicRecipeList.isEmpty()) {
            initFirstNRecipes();
        }
    }

    private void setupFilters(){

        refreshButtons();

        float ingredientsFilterSize = getFilterButton(Filter.INGREDIENT).getTextSize();

        getFilterButton(Filter.USER).setTextSize(COMPLEX_UNIT_PX, ingredientsFilterSize);
        getFilterButton(Filter.RECIPE).setTextSize(COMPLEX_UNIT_PX, ingredientsFilterSize);
        getFilterButton(Filter.RATE).setTextSize(COMPLEX_UNIT_PX, ingredientsFilterSize);

        setFilterOnClick(Filter.RECIPE);
        setFilterOnClick(Filter.USER);
        setFilterOnClick(Filter.INGREDIENT);

        getFilterButton(Filter.RATE).setOnClickListener(listener -> {
            setButton(Filter.RATE, !getFilterState(Filter.RATE));
            sortResults();
            Objects.requireNonNull(onlineRecyclerView.getAdapter()).notifyDataSetChanged();
        });
    }

    private void refreshButtons(){

        filterButtons.put(Filter.RECIPE, requireView().findViewById(R.id.filter_recipe));
        filterButtons.put(Filter.USER, requireView().findViewById(R.id.filter_users));
        filterButtons.put(Filter.INGREDIENT, requireView().findViewById(R.id.filter_ingre));
        filterButtons.put(Filter.RATE, requireView().findViewById(R.id.filter_rate));

        if(filterStates.isEmpty()) {
            for(Filter filter : Filter.values()){
                filterStates.put(filter, false);
            }
        } else {
            for(Filter filter: Filter.values()){
                setButton(filter, filterStates.get(filter));
            }
        }
    }

    private void setFilterOnClick(Filter filter){
        getFilterButton(filter).setOnClickListener(listener -> {
            if(!isLoading){
                resetOthers(filter);
                setButton(filter, !getFilterState(filter));
                search();
            }
        });
    }

    private void search(){
        searchList.clear();
        isLoading = true;
        if(getFilterState(Filter.INGREDIENT)){
            recipeStorage.getSearch().searchRecipeByIngredient(actualQuery, OnlineMiniaturesFragment.this);
        } else if(getFilterState(Filter.USER)){
            userStorage.getSearch().searchForUser(actualQuery, OnlineMiniaturesFragment.this);
        } else if(getFilterState(Filter.RECIPE)){
            recipeStorage.getSearch().searchForRecipe(actualQuery, OnlineMiniaturesFragment.this);
        } else {
            recipeStorage.getSearch().searchForRecipe(actualQuery, OnlineMiniaturesFragment.this);
            userStorage.getSearch().searchForUser(actualQuery, OnlineMiniaturesFragment.this);
            getFilterButton(Filter.RECIPE).setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            getFilterButton(Filter.USER).setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            getFilterButton(Filter.RECIPE).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            getFilterButton(Filter.USER).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }



    @Override
    public void onSuccess(List<Miniatures> data){
        if(isSearching){
            searchList.addAll(data);
            sortResults();
            removeDuplicate(searchList);
            Objects.requireNonNull(onlineRecyclerView.getAdapter()).notifyDataSetChanged();
            isLoading = false;
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
        dynamicRecipeList.sort((left, right) -> ((Recipe) left).compareTo((Recipe) right));

        int size = dynamicRecipeList.size();
        if(size != 0){
            currentNewest = ((Recipe) dynamicRecipeList.get(0)).getDate();
            currentOldest = ((Recipe) dynamicRecipeList.get(size - 1)).getDate();
        } else {
            Log.w(TAG, "The list of recipes is empty despite adding some new recipes");
        }

        Objects.requireNonNull(onlineRecyclerView.getAdapter()).notifyDataSetChanged();
        isLoading = false;
    }

    @Override
    public void onFailure() {
        isLoading = false;
        Log.w(TAG, "No Recipe found");
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
        recipeStorage.getNRecipes(NB_OF_RECIPES_LOADED_AT_A_TIME, RecipeStorage.OLDEST_RECIPE, currentOldest, false, this);
    }

    private void getPreviousRecipes(){
        recipeStorage.getNRecipes(NB_OF_RECIPES_LOADED_AT_A_TIME, currentNewest, recipeStorage.getCurrentDate(), true, this);
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

    private void sortResults(){
        if(getFilterState(Filter.RATE)) {
            Sort.sortByRate(searchList);
        }else if(getFilterState(Filter.INGREDIENT)){
            Sort.sortByIngredientSimilarity(searchList,actualQuery);
        }else {
            Sort.sortBySimilarity(searchList,actualQuery);
        }
    }

    @NonNull private Button getFilterButton(Filter filter){
        return Objects.requireNonNull(filterButtons.get(filter));
    }

    @NonNull private Boolean getFilterState(Filter filter){
        return Objects.requireNonNull(filterStates.get(filter));
    }

    private void setButton(Filter filter, Boolean setEnabled){
        Button filterButton = getFilterButton(filter);

        int nextColor = setEnabled ? R.color.colorPrimary : R.color.disabled;
        int nextFlag = setEnabled ? Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG : 0;

        filterButton.setBackgroundColor(getResources().getColor(nextColor, null));
        filterButton.setPaintFlags(nextFlag);
        filterStates.replace(filter, setEnabled);
    }

    private void resetOthers(Filter except){
        for(Filter filter : Filter.values()){
            if(filter != except){
                setButton(filter, false);
            }
        }
    }

    private  void resetFilters(){
        for(Filter filter : Filter.values()){
            setButton(filter, false);
        }
    }
}

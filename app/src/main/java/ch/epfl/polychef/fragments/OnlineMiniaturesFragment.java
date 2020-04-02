package ch.epfl.polychef.fragments;

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

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.R;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.SearchRecipe;

public class OnlineMiniaturesFragment extends Fragment implements CallNotifier<Recipe>, CallHandler<List<Recipe>> {

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;

    private SearchView searchView;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private int currentReadInt = 1;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;

    private RecipeStorage recipeStorage;

    public OnlineMiniaturesFragment(){
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

        Bundle bundle = getArguments();
        recipeStorage = (RecipeStorage) bundle.getSerializable("RecipeStorage");

        int fragmentID = bundle.getInt("fragmentID");
        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        onlineRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, onlineRecyclerView, fragmentID));
        // Add a scroll listener when we reach the end of the list we load new recipes from database
        onlineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isLoading){
                        return;
                    }
                    recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, currentReadInt, OnlineMiniaturesFragment.this);
                    currentReadInt += nbOfRecipesLoadedAtATime;
                    isLoading = true;
                }
            }
        });
        return view;
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
        // For now when we enter the page we load the offline recipes first
        // add a certain number of recipes at this end of the actual list
        dynamicRecipeList.clear();
        recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, 1, this);
        currentReadInt += nbOfRecipesLoadedAtATime;
    }

    @Override
    public void notify(Recipe data) {
        isLoading = false;
        dynamicRecipeList.add(data);
        onlineRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onSuccess(List<Recipe> data) {
        isLoading = false;
        dynamicRecipeList.addAll(data);
        onlineRecyclerView.getAdapter().notifyDataSetChanged();
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

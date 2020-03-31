package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.R;
import ch.epfl.polychef.adaptersrecyclerview.RecipeMiniatureAdapter;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.Recipe;

public class OnlineMiniaturesFragment extends MiniaturesFragment<HomePage> implements CallNotifier<Recipe> {

    private int currentReadInt = 1;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;

    private RecipeStorage recipeStorage;

    public OnlineMiniaturesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipes = new ArrayList<>();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateContent(RecyclerView recyclerView, int newState){
        if(!recyclerView.canScrollVertically(1)){
            if(isLoading){
                return;
            }
            recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, currentReadInt, OnlineMiniaturesFragment.this);
            currentReadInt += nbOfRecipesLoadedAtATime;
            isLoading = true;
        }
    }

    @Override
    public void attachPage(Context context) {
        if(context instanceof HomePage){
            hostActivity = (HomePage) context;
            recipeStorage = hostActivity.getRecipeStorage();
        } else {
            super.attachPage(context);
        }
    }

    @Override
    public void initialiseContent(){
        // For now when we enter the page we load the offline recipes first
        // add a certain number of recipes at this end of the actual list
        //We ignore the new state
        updateContent(recyclerView, 0);
    }

    @Override
    public void notify(Recipe data) {
        isLoading = false;
        recipes.add(data);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFailure() {
        isLoading = false;
    }
}

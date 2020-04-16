package ch.epfl.polychef.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
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

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.FavouritesUtils;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment implements CallHandler<Recipe> {

    private static final String TAG = "FavouritesFragment";
    private RecyclerView favouriteRecyclerView;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private int indexFavourites = 0;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;

    private RecipeStorage recipeStorage;
    private ImageStorage imageStorage;

    private UserStorage userStorage;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        favouriteRecyclerView = view.findViewById(R.id.miniaturesFavouriteList);
        favouriteRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RecipeMiniatureAdapter adapter = new RecipeMiniatureAdapter(this.getActivity(),
                dynamicRecipeList, favouriteRecyclerView, container.getId(), imageStorage, userStorage);

        favouriteRecyclerView.setAdapter(adapter);
        // Add a scroll listener when we reach the end of the list we load new recipes from database
        favouriteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isLoading){
                        return;
                    }
                    addNextRecipes();
                    isLoading = true;
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
            imageStorage = homePage.getImageStorage();
            userStorage = homePage.getUserStorage();
            recipeStorage = homePage.getRecipeStorage();
            Preconditions.checkArgument(recipeStorage == null || imageStorage == null || userStorage == null);
        } else {
            throw new IllegalArgumentException("The favourite miniature fragment wasn't attached properly!");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dynamicRecipeList.clear();
        indexFavourites = 0;
        addNextRecipes();
    }

    private boolean addNextRecipes() {
        if(isNetConnected()) {
            return getNextOnlineFavourites();
        } else {
            return getNextOfflineFavourites();
        }
    }

    private boolean getNextOfflineFavourites() {
        List<Recipe> favouritesList = FavouritesUtils.getOfflineFavourites(getActivity());
        if(indexFavourites + nbOfRecipesLoadedAtATime <= favouritesList.size()) {
            for(int i = indexFavourites; i < indexFavourites + nbOfRecipesLoadedAtATime; ++i) {
                dynamicRecipeList.add(favouritesList.get(i));
                favouriteRecyclerView.getAdapter().notifyDataSetChanged();
            }
            indexFavourites = indexFavourites + nbOfRecipesLoadedAtATime;
            return true;
        }
        if(indexFavourites < favouritesList.size()) {
            for(int i = indexFavourites; i < favouritesList.size(); ++i) {
                dynamicRecipeList.add(favouritesList.get(i));
                favouriteRecyclerView.getAdapter().notifyDataSetChanged();
            }
            indexFavourites = favouritesList.size();
            return true;
        }
        return false;
    }

    private boolean getNextOnlineFavourites() {
        List<String> favouritesList = userStorage.getPolyChefUser().getFavourites();
        if(indexFavourites + nbOfRecipesLoadedAtATime <= favouritesList.size()) {
            for(int i = indexFavourites; i < indexFavourites + nbOfRecipesLoadedAtATime; ++i) {
                recipeStorage.readRecipeFromUuid(favouritesList.get(i), this);
            }
            indexFavourites = indexFavourites + nbOfRecipesLoadedAtATime;
            return true;
        }
        if(indexFavourites < favouritesList.size()) {
            for(int i = indexFavourites; i < favouritesList.size(); ++i) {
                recipeStorage.readRecipeFromUuid(favouritesList.get(i), this);
            }
            indexFavourites = favouritesList.size();
            return true;
        }
        return false;
    }

    @Override
    public void onSuccess(Recipe data) {
        isLoading = false;
        dynamicRecipeList.add(data);
        favouriteRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFailure() {
        isLoading = false;
        Log.w(TAG, "No Recipe found");
    }

    private boolean isNetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.OfflineRecipes;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.utils.FavouritesUtils;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;

public final class OfflineMiniaturesFragment extends Fragment {
    private RecyclerView offlineRecyclerView;

    public OfflineMiniaturesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miniatures_offline, container, false);

        // Instantiate the recyclerView with the adapter and the layout manager
        offlineRecyclerView = view.findViewById(R.id.miniaturesOfflineList);
        offlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        List<Recipe> recipeList = new ArrayList<>(FavouritesUtils.getInstance().getOfflineFavourites());
        recipeList.addAll(OfflineRecipes.getInstance().getOfflineRecipes());
        offlineRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), recipeList, offlineRecyclerView, container.getId(), null));

        return view;
    }
}

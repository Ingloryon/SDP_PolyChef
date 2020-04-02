package ch.epfl.polychef.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.polychef.R;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.OfflineRecipes;

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
        offlineRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), OfflineRecipes.getInstance().getOfflineRecipes(), offlineRecyclerView, container.getId()));

        return view;
    }
}

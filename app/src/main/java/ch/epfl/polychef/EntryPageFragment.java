package ch.epfl.polychef;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.polychef.adaptersRecyclerView.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.OfflineRecipes;

public class EntryPageFragment extends Fragment {

    RecyclerView offlineRecyclerView;

    public EntryPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_page, container, false);
        offlineRecyclerView = view.findViewById(R.id.cardList);
        offlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        offlineRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), OfflineRecipes.offlineRecipes, offlineRecyclerView));

        // Inflate the layout for this fragment
        return view;
    }
}

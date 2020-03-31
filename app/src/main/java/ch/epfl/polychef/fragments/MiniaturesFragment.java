package ch.epfl.polychef.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.adaptersrecyclerview.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.Recipe;


public abstract class MiniaturesFragment<T extends AppCompatActivity> extends Fragment {

    protected T hostActivity;  //TODO use ConnectedActivity if possible
    protected List<Recipe> recipes;

    protected RecyclerView recyclerView;

    public MiniaturesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);

        recyclerView = view.findViewById(getRecyclerViewId());

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), recipes, recyclerView, container.getId()));

        // Add a scroll listener when we reach the end of the list we load new recipes from database
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recycler, int newState) {
                super.onScrollStateChanged(recycler, newState);

                updateContent(recycler, newState);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        attachPage(context);
    }

    public void attachPage(Context context) {
        throw new IllegalArgumentException("This fragment wasn't attached properly!");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialiseContent();
    }

    public abstract void updateContent(RecyclerView recycler, int newState);
    public abstract void initialiseContent();

    public int getRecyclerViewId(){
        return R.id.miniaturesList;
    }

    public int getLayoutId(){
        return R.layout.fragment_miniatures;
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }
}

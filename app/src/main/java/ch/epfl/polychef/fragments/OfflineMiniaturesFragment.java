package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ch.epfl.polychef.R;
import ch.epfl.polychef.adaptersrecyclerview.RecipeMiniatureAdapter;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.recipe.OfflineRecipes;

public final class OfflineMiniaturesFragment extends MiniaturesFragment<EntryPage> {
    //private RecyclerView offlineRecyclerView;

    public OfflineMiniaturesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recipes = OfflineRecipes.getInstance().getOfflineRecipes();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void updateContent(RecyclerView recycler, int newState) {

    }

    @Override
    public void initialiseContent() {

    }

    @Override
    public void attachPage(Context context) {
        if(context instanceof EntryPage){
            hostActivity = (EntryPage) context;
        } else {
            super.attachPage(context);
        }
    }
}

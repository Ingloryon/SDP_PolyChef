package ch.epfl.polychef;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.epfl.polychef.recipe.Recipe;

public class FullRecipeFragment extends Fragment {

    private Recipe currentRecipe;


    public FullRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_recipe, container, false);
    }
}

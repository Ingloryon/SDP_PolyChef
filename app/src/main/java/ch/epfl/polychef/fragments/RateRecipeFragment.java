package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ch.epfl.polychef.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateRecipeFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public RateRecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rate_recipe, container, false);
    }

    //TODO: Afficher dans error log les potentielles exceptions levées par Rate (si a deja voté par exemple) -> affiche egalement la note donnée


}

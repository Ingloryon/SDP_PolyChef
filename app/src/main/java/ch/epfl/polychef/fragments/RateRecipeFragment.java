package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Recipe;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateRecipeFragment extends Fragment {

    private Button postButton;
    private Recipe recipe;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipe = null; //TODO: get Recipe from Database using infos given by Bundle

        postButton = getView().findViewById(R.id.buttonSendRate);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSendRate();
            }
        });

    }


    private void checkAndSendRate(){
        Spinner spinner = getView().findViewById(R.id.RateChoices);
        int star_nb = spinner.getSelectedItemPosition() -1 ; //TODO: Verifier si index commence à 0 ou 1

        int userID = 0 ; //TODO: get "userID" from ConnectedUser

        try {
           recipe.getRating().addRate(userID, star_nb);

        } catch (IllegalArgumentException e){
            //TODO: Afficher dans error log les potentielles exceptions levées par Rate (si a deja voté par exemple) -> affiche egalement la note donnée
            TextView errorLog =  getView().findViewById(R.id.errorLogs);
            errorLog.setText(e.toString().substring(35));
            errorLog.setVisibility(View.VISIBLE);
            return;
        }

        //TODO: return to Recipe or Home menu ?

    }
}

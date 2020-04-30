package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

        Bundle bundle = this.getArguments();
        if(bundle != null){
            recipe = (Recipe) bundle.getSerializable("RecipeToRate");
        }

        String text = getActivity().getString(R.string.RateText) + " \"" + recipe.getName() + "\" ?";
        TextView rateText =  getView().findViewById(R.id.RateText);
        rateText.setText(text);


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
        // The index returned is the same as the nb of stars
        int starNb = spinner.getSelectedItemPosition() ;

        //TODO: remove -> the index returned = nb of stars
        /*String txt = Integer.toString(starNb);
        */

        String userID = "0" ; //TODO: get "userID" from ConnectedUser -> in Simon's branch user.getKey()

        int oldRating = recipe.getRating().addRate(userID, starNb);

        String alreadyRatedText = "You previously rated this recipe " + oldRating + " stars.\n";
        String newRatingText =  "Your new rating is " + starNb +"stars.";

        if(oldRating == -1) {
            Toast.makeText(getActivity(), alreadyRatedText + newRatingText , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), newRatingText , Toast.LENGTH_LONG).show();
        }

        //TODO: return to Recipe or Home menu ?
        /*
        HomePage act = (HomePage) getActivity();
        act.onBackPressed();
         */
    }
}

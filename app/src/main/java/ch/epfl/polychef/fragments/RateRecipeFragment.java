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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.UserStorage;

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

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * as returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * @throws NullPointerException if this.getArguments() is null or if bundle.getSerializable("RecipeToRate") is null
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        recipe = (Recipe) bundle.getSerializable("RecipeToRate");

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

        String userID = "0";
        //Should be UserStorage.getInstance().getPolyChefUser().getKey()

        int oldRating = recipe.getRating().addRate(userID, starNb);


        if(oldRating == -1 || oldRating==starNb) {
            String newRatingText =  "Your rating is " + starNb +" stars.";
            Toast.makeText(getActivity(), newRatingText , Toast.LENGTH_LONG).show();
        } else {
            String newRatingText =  "Your new rating is " + starNb +" stars. Your previous rating was "+oldRating;
            Toast.makeText(getActivity(), newRatingText , Toast.LENGTH_LONG).show();
        }

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference(RecipeStorage.DB_NAME).child(recipe.getKey());
        ref.setValue(recipe);

        getActivity().onBackPressed();
    }

    protected FirebaseDatabase getFireDatabase(){
        return FirebaseDatabase.getInstance();
    }
}

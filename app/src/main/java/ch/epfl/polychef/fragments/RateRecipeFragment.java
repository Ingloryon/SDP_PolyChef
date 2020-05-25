package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Opinion;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.Preconditions;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateRecipeFragment extends Fragment {
    private static final String TAG = "RateRecipeFragment";
    private Recipe recipe;
    private UserStorage userStorage;
    private RecipeStorage recipeStorage;
    private Spinner spinner;
    private EditText comment;

    /**
     * Required empty public constructor for Database.
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

        recipe = (Recipe) Objects.requireNonNull(bundle).getSerializable("RecipeToRate");

        spinner = requireView().findViewById(R.id.RateChoices);
        comment = requireView().findViewById(R.id.CommentText);

        if(recipe.getRating().getAllOpinion().containsKey(userStorage.getPolyChefUser().getKey())) {
            Opinion previousOpinion = recipe.getRating().getAllOpinion().get(userStorage.getPolyChefUser().getKey());
            spinner.setSelection(Objects.requireNonNull(previousOpinion).getRate());
            if(previousOpinion.getComment() != null && !previousOpinion.getComment().isEmpty()) {
                comment.setText(previousOpinion.getComment());
            }
        }

        String text = requireActivity().getString(R.string.RateText) + " \"" + recipe.getName() + "\" ?";
        TextView rateText =  requireView().findViewById(R.id.RateText);
        rateText.setText(text);

        Button postButton = requireView().findViewById(R.id.buttonSendRate);
        postButton.setOnClickListener(view1 -> checkAndSendOpinion());

    }

    private void checkAndSendOpinion(){
        // The index returned is the same as the nb of stars
        int starNb = spinner.getSelectedItemPosition();

        String commentString = comment.getText().toString().trim();

        String userKey = userStorage.getPolyChefUser().getKey();

        int oldRating = commentString.isEmpty() ? recipe.getRating().addOpinion(userKey, starNb)
                : recipe.getRating().addOpinion(userKey, starNb, commentString);

        if(oldRating == -1 || oldRating==starNb) {
            String newRatingText =  "Your rating is " + starNb +" stars.";
            Toast.makeText(getActivity(), newRatingText , Toast.LENGTH_SHORT).show();
        } else {
            String newRatingText =  "Your new rating is " + starNb +" stars. Your previous rating was "+oldRating;
            Toast.makeText(getActivity(), newRatingText , Toast.LENGTH_LONG).show();
        }

        CallHandler<User> callHandler = new CallHandler<User>() {
            @Override
            public void onSuccess(User data) {
                data.getRating().addOpinion(recipe.getRecipeUuid()+userKey, starNb);
                userStorage.updateUserInfo(data);
            }

            @Override
            public void onFailure() {
                Log.w(TAG,"No user found");
            }
        };

        userStorage.getUserByEmail(recipe.getAuthor(),callHandler);

        recipeStorage.updateRecipe(recipe);

        requireActivity().onBackPressed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            HomePage homePage = (HomePage) context;
            FirebaseDatabase fireDatabase = homePage.getFireDatabase();
            userStorage = homePage.getUserStorage();
            recipeStorage = homePage.getRecipeStorage();
            Preconditions.checkArgument(fireDatabase != null && userStorage!=null );
        } else {
            throw new IllegalArgumentException("The rate recipe fragment wasn't attached properly!");
        }
    }
}

package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;

public class PostRecipeFragment extends Fragment {
    private String name;
    private List<String> recipeInstructions;
    private List<Ingredient> ingredients;

    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;

    /**
     * Required empty public constructor.
     */
    public PostRecipeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_recipe, container, false);

    }

    private void getEnteredInputs(){
        EditText nameInput = getView().findViewById(R.id.nameInput);
        name = nameInput.getText().toString();

        EditText personNb = getView().findViewById(R.id.personNbInput);
        personNumber = Integer.parseInt(personNb.getText().toString());


        EditText editText = getView().findViewById(R.id.nameInput);
        name = editText.getText().toString();

    }

    private boolean sanitizeNumbers(){
        // TODO: Implement sanitization -> check entered number
        return true;
    }


}

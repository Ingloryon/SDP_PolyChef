package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Boolean> wrongInputs = new HashMap<>();

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

        // TODO: inputs ingredients, instructions, difficulty

        EditText personNb = getView().findViewById(R.id.personNbInput);
        String persNb = personNb.getText().toString();

        if (checkInputIsNumber(persNb)){

        } else {
            personNumber = Integer.parseInt(personNb.getText().toString());
        }


        EditText prepTimeInput = getView().findViewById(R.id.prepTimeInput);
        estimatedPreparationTime = Integer.parseInt(prepTimeInput.getText().toString());

        EditText cookTimeInput = getView().findViewById(R.id.cookTimeInput);
        estimatedCookingTime = Integer.parseInt(cookTimeInput.getText().toString());

    }

    private boolean checkInputIsNumber(String input){
        return android.text.TextUtils.isDigitsOnly(input);
    }

    private void initializeWronginputsMap(){
        wrongInputs.put("Title", false);
        wrongInputs.put("Ingredients", false);
        wrongInputs.put("Instructions", false);
        wrongInputs.put("PersNb", false);
        wrongInputs.put("PrepTime", false);
        wrongInputs.put("CookTime", false);
        wrongInputs.put("Difficulty", false);
    }


}

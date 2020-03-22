package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.polychef.R;
import ch.epfl.polychef.firebase.Firebase;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;

public class PostRecipeFragment extends Fragment {
    private final String TAG = "PostRecipeFragment";
    private final static int TITLE_MAX_CHAR = 80;
    private final static int MAX_PERS_NB = 100;
    private String name;
    private List<String> recipeInstructions;
    private List<Ingredient> ingredients;
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;

    private Button postButton;

    private Map<String, Boolean> wrongInputs;
    private List<String> errorLogs;

    private Spinner difficultyInput;

    /**
     * Required empty public constructor.
     */
    public PostRecipeFragment() {
    }

    private void initializeErrorDetectionAttributes() {
        errorLogs = new ArrayList<>();
        wrongInputs = new HashMap<>();
        wrongInputs.put("Title", false);
        wrongInputs.put("Ingredients", false);
        wrongInputs.put("Instructions", false);
        wrongInputs.put("PersNb", false);
        wrongInputs.put("PrepTime", false);
        wrongInputs.put("CookTime", false);
        wrongInputs.put("Difficulty", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeErrorDetectionAttributes();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postButton = getView().findViewById(R.id.postRecipe);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPostButton(view);
            }
        });
        difficultyInput = getView().findViewById(R.id.difficultyInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyInput.setAdapter(adapter);
    }

    /**
     * Called when user presses "post recipe", will parse and check the entered inputs.
     * If the inputs are correct it will post the corresponding Recipe on Firebase.
     * Otherwise it will update the View to display to wrong inputs.
     * @param view the current view
     */
    public void setPostButton(View view) {
        getEnteredInputs();
        buildRecipeAndPostToFirebase();
    }

    private void getEnteredInputs() {

        EditText nameInput = getView().findViewById(R.id.nameInput);
        String inputName = nameInput.getText().toString();
        if(inputName.length() > TITLE_MAX_CHAR) {
            errorLogs.add("Title: too long. The maximum length is " + TITLE_MAX_CHAR + " characters.");
        } else {
            wrongInputs.put("Title", true);
            name = inputName;
        }

        EditText ingredientsInput = getView().findViewById(R.id.ingredientsList);
        String ingre = ingredientsInput.getText().toString();
        if (parseIngredients(ingre)) {
            wrongInputs.put("Ingredients", true); // TODO: Use replace when set SDK min24
        }

        EditText instructionsInput = getView().findViewById(R.id.instructionsList);
        String instructions = instructionsInput.getText().toString();
        if (parseInstructions(instructions)) {
            wrongInputs.put("Instructions", true); // TODO: Use replace when set SDK min24
        }


        EditText personNb = getView().findViewById(R.id.personNbInput);
        String persNb = personNb.getText().toString();
        // checks are applied in order so parseInt is always valid
        // we only check persNb <= max since positiveness will already be check by builder
        if (checkInputIsNumber(persNb) && Integer.parseInt(persNb) <= MAX_PERS_NB) {
            wrongInputs.put("PersNb", true);  // TODO: Use replace when set SDK min24
            personNumber = Integer.parseInt(persNb);
        } else {
            errorLogs.add("Person number: should be a number between 0 and " + MAX_PERS_NB + ".");
        }


        EditText prepTimeInput = getView().findViewById(R.id.prepTimeInput);
        String prep = prepTimeInput.getText().toString();
        if (checkInputIsNumber(prep)) {
            wrongInputs.put("PrepTime", true);  // TODO: Use replace when set SDK min24
            estimatedPreparationTime = Integer.parseInt(prep);
        } else {
            errorLogs.add("Preparation time: should be a number.");
        }


        EditText cookTimeInput = getView().findViewById(R.id.cookTimeInput);
        String cook = cookTimeInput.getText().toString();
        if (checkInputIsNumber(cook)) {
            wrongInputs.put("CookTime", true);  // TODO: Use replace when set SDK min24
            estimatedCookingTime = Integer.parseInt(cook);
        } else {
            errorLogs.add("Cooking time: should be a number.");
        }

        recipeDifficulty = Recipe.Difficulty.values()[difficultyInput.getSelectedItemPosition()];
    }

    private boolean checkInputIsNumber(String input) {
        return android.text.TextUtils.isDigitsOnly(input);
    }

    private boolean parseIngredients(String toMatch) {
        List<String> allMatches = new ArrayList<>();
        ingredients = new ArrayList<>();
        Matcher m = Pattern.compile("\\{[ ]*[A-Za-z0-9]*[ ]*,[ ]*[0-9]*[ ]*,[ ]*[A-Za-z0-9]*[ ]*\\}")
                .matcher(toMatch);
        while (m.find()) {
            allMatches.add(m.group());
        }
        for (String s : allMatches) {
            String[] list = s.split(",");
            if (list.length != 3) {
                ingredients.clear();
                allMatches.clear();
                errorLogs.add("Ingredients: There should be 3 arguments entered as { a, b, c}");
                return false;
            }
            String name = list[0].trim().substring(1).trim();
            double quantity = Double.parseDouble(list[1].trim()); // TODO: check this method does not throw errors
            Ingredient.Unit unit = null;
            String unitString = list[2].trim().substring(0, list[2].trim().length() - 1).trim();
            for (Ingredient.Unit u : Ingredient.Unit.values()) {
                if (u.toString().toLowerCase().equals(unitString.toLowerCase())) {
                    unit = u;
                }
            }
            if (unit == null) {
                ingredients.clear();
                allMatches.clear();
                errorLogs.add("Ingredients: The entered unit is not part of the possible units (" + Ingredient.Unit.values() + ").");
                return false;
            }
            ingredients.add(new Ingredient(name, quantity, unit));
        }
        return true;
    }

    private boolean parseInstructions(String instructions) {
        final String SEPARATOR = Pattern.quote("},{");

        // TODO: Add more precise input checking of instructions
        if (!instructions.contains("{") || !instructions.contains("}")){
            errorLogs.add("Instructions: the entered instructions should match format {a},{b},... (no spaces)");
            return false;
        }

        recipeInstructions = new ArrayList<>();
        instructions = instructions.substring(1);
        String[] mots = instructions.split(SEPARATOR);
        for (int i = 0; i < mots.length - 1; i++) {
            recipeInstructions.add(mots[i]);
        }
        recipeInstructions.add(mots[mots.length - 1].substring(0, mots[mots.length - 1].length() - 1));
        return true;
    }

    private void buildRecipeAndPostToFirebase() {

        // TODO: Catch exceptions thrown by builder and set WrongInputsMap accordingly

        RecipeBuilder recipeBuilder = new RecipeBuilder();


        if (checkForIllegalInputs(recipeBuilder)) {
            Firebase.addRecipeToFirebase(recipeBuilder.build());
        } else {
            printWrongInputsToUser();
        }
    }

    private boolean checkForIllegalInputs(RecipeBuilder rb) {

        try {
            rb.setName(name)
                    .setEstimatedCookingTime(estimatedCookingTime)
                    .setPersonNumber(personNumber)
                    .setEstimatedPreparationTime(estimatedPreparationTime)
                    .addPicturePath(R.drawable.ovenbakedsalmon)
                    .setRecipeDifficulty(recipeDifficulty);
            for (int i = 0; i < recipeInstructions.size(); i++) {
                rb.addInstruction(recipeInstructions.get(i));
            }
            for (int i = 0; i < ingredients.size(); i++) {
                rb.addIngredient(ingredients.get(i));
            }

        } catch (IllegalArgumentException e) {
            findIllegalInputs();
            return false;
        }

        return true;
    }

    private void findIllegalInputs() {


    }

    private void printWrongInputsToUser(){

        //TODO: To implement, should update the Page so it displays the wrong inputs entered

        // So the errors are displayed by category
        Collections.sort(errorLogs);
    }
}

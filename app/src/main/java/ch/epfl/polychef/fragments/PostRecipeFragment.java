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
    private String name;
    private List<String> recipeInstructions;
    private List<Ingredient> ingredients;
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;

    private Button postButton;

    private Map<String, Boolean> wrongInputs;

    private Spinner difficultyInput;

    /**
     * Required empty public constructor.
     */
    public PostRecipeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeWrongInputsMap();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        postButton=getView().findViewById(R.id.postRecipe);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPostButton(view);
            }
        });
        difficultyInput = (Spinner) getView().findViewById(R.id.difficultyInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyInput.setAdapter(adapter);
    }

    private void initializeWrongInputsMap(){
        wrongInputs = new HashMap<>();
        wrongInputs.put("Title", false);
        wrongInputs.put("Ingredients", false);
        wrongInputs.put("Instructions", false);
        wrongInputs.put("PersNb", false);
        wrongInputs.put("PrepTime", false);
        wrongInputs.put("CookTime", false);
        wrongInputs.put("Difficulty", false);
    }

    private void getEnteredInputs(){
        EditText nameInput = getView().findViewById(R.id.nameInput);
        // TODO: Add sanitization -> check length, ect...
        name = nameInput.getText().toString();

        EditText ingredientsInput = getView().findViewById(R.id.ingredientsList);
        String ingre = ingredientsInput.getText().toString();
        if ( matchRegexIngredients(ingre) ){
            wrongInputs.put("Ingredients", true); // TODO: Use replace when set SDK min24
        }

        EditText instructionsInput = getView().findViewById(R.id.instructionsList);
        String instructions = instructionsInput.getText().toString();
        if (getInstructions(instructions)){
            wrongInputs.put("Instructions", true);
        }


        EditText personNb = getView().findViewById(R.id.personNbInput);
        String persNb = personNb.getText().toString();
        if (checkInputIsNumber(persNb)){
            wrongInputs.put("PersNb", true);  // TODO: Use replace when set SDK min24
            personNumber = Integer.parseInt(persNb);
        }


        EditText prepTimeInput = getView().findViewById(R.id.prepTimeInput);
        String prep = prepTimeInput.getText().toString();
        if (checkInputIsNumber(prep)){
            wrongInputs.put("PrepTime", true);  // TODO: Use replace when set SDK min24
            estimatedPreparationTime = Integer.parseInt(prep);
        }


        EditText cookTimeInput = getView().findViewById(R.id.cookTimeInput);
        String cook = cookTimeInput.getText().toString();
        if (checkInputIsNumber(cook)){
            wrongInputs.put("CookTime", true);  // TODO: Use replace when set SDK min24
            estimatedCookingTime = Integer.parseInt(cook);
        }

        recipeDifficulty= Recipe.Difficulty.values()[difficultyInput.getSelectedItemPosition()];
    }

    private boolean checkInputIsNumber(String input){
        return android.text.TextUtils.isDigitsOnly(input);
    }

    private boolean getInstructions(String instructions){
        final String SEPARATOR = Pattern.quote("}{");
        recipeInstructions = new ArrayList<>();
        instructions = instructions.substring(1);
        String mots[] = instructions.split(SEPARATOR);
        for(int i=0;i<mots.length-1;i++){
            recipeInstructions.add(mots[i]);
        }
        recipeInstructions.add(mots[mots.length-1].substring(0,mots[mots.length-1].length()-1));
        return true;
    }

    private boolean matchRegexIngredients(String toMatch) {
        List<String> allMatches = new ArrayList<>();
        ingredients = new ArrayList<>();
        Matcher m = Pattern.compile("\\{[ ]*[A-Za-z0-9]*[ ]*,[ ]*[0-9]*[ ]*,[ ]*[A-Za-z0-9]*[ ]*\\}")
                .matcher(toMatch);
        while(m.find()) {
            allMatches.add(m.group());
        }
        for(String s: allMatches) {
            String[] list = s.split(",");
            if(list.length != 3) {
                ingredients.clear();
                allMatches.clear();
                return false;
            }
            String name = list[0].trim().substring(1).trim();
            double quantity = Double.parseDouble(list[1].trim()); // TODO: check this method does not throw errors
            Ingredient.Unit unit = null;
            String unitString = list[2].trim().substring(0, list[2].trim().length() - 1).trim();
            for (Ingredient.Unit u : Ingredient.Unit.values()) {
                if(u.toString().toLowerCase().equals(unitString.toLowerCase())) {
                    unit = u;
                }
            }
            if(unit == null) {
                ingredients.clear();
                allMatches.clear();
                return false;
            }
            ingredients.add(new Ingredient(name, quantity, unit));
        }
        return true;
    }

    private void buildRecipeAndPostToFirebase(){

        // TODO: Catch exceptions thrown by builder and set WrongInputsMap accordingly

        RecipeBuilder recipeBuilder = new RecipeBuilder()
                .setName(name)
                .setEstimatedCookingTime(estimatedCookingTime)
                .setPersonNumber(personNumber)
                .setEstimatedPreparationTime(estimatedPreparationTime)
                .addPicturePath(R.drawable.ovenbakedsalmon)
                .setRecipeDifficulty(recipeDifficulty);
        for(int i=0;i<recipeInstructions.size();i++){
            recipeBuilder.addInstruction(recipeInstructions.get(i));
        }
        for(int i=0;i<ingredients.size();i++){
            recipeBuilder.addIngredient(ingredients.get(i));
        }
        Firebase.addRecipeToFirebase(recipeBuilder.build());
    }

    public void setPostButton(View view) {
        getEnteredInputs();
        buildRecipeAndPostToFirebase();
    }
}

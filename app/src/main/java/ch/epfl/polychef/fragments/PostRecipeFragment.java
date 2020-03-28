package ch.epfl.polychef.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageHandler;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;

public class PostRecipeFragment extends Fragment {
    private final String tag = "PostRecipeFragment";
    private final int miniatureFactor = 1;
    private final int mealPicturesFactor = 10;
    private final int titleMaxChar = 80;
    private final int titleMinChar = 3;
    private final int maxPersNb = 100;
    private String name;
    private List<String> recipeInstructions;
    private List<Ingredient> ingredients;
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;
    private Recipe postedRecipe;

    private Button postButton;

    private Button addMiniature;
    private Button addPictures;

    private Uri currentMiniature = null;
    private String miniatureName = UUID.randomUUID().toString();
    private ImageView imageMiniaturePreview;

    private List<Uri> currentMealPictures = new ArrayList<>();
    private TextView mealPicturesText;

    private ImageHandler imageHandler;

    private Map<String, Boolean> wrongInputs;
    private List<String> errorLogs = new ArrayList<>();

    private Spinner difficultyInput;

    private RecipeStorage recipeStorage = new RecipeStorage();

    /**
     * Required empty public constructor.
     */
    public PostRecipeFragment() {
    }

    private void initializeWrongInputs() {
        wrongInputs = new HashMap<>();
        wrongInputs.put("Title", false);
        wrongInputs.put("Ingredients", false);
        wrongInputs.put("Instructions", false);
        wrongInputs.put("Person Number", false);
        wrongInputs.put("Preparation Time", false);
        wrongInputs.put("Cooking Time", false);
        wrongInputs.put("Difficulty", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeWrongInputs();

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

        // Image handling
        imageHandler = new ImageHandler(getActivity());
        addMiniature = getView().findViewById(R.id.miniature);
        imageMiniaturePreview = getView().findViewById(R.id.miniaturePreview);
        addPictures = getView().findViewById(R.id.pictures);
        mealPicturesText = getView().findViewById(R.id.mealPicturesText);
        addMiniature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPictureDialog(miniatureFactor);
            }
        });
        addPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPictureDialog(mealPicturesFactor);
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
        if(!buildRecipeAndPostToFirebase()){
            printWrongInputsToUser();
        }else{
            Intent intent = new Intent(getActivity(), HomePage.class);
            startActivity(intent);
        }
    }

    private void getEnteredInputs() {
        String inputName = ((EditText)getView().findViewById(R.id.nameInput)).getText().toString();
        if(inputName.length() > titleMaxChar || inputName.length() < titleMinChar) {
            errorLogs.add("Title: too long or too short. Need to be between " + titleMinChar + " and " + titleMaxChar + " characters.");
        } else {
            wrongInputs.put("Title", true);
            name = inputName;
        }

        String ingre = ((EditText) getView().findViewById(R.id.ingredientsList)).getText().toString();
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
        if (persNb.length()!=0 && android.text.TextUtils.isDigitsOnly(persNb) && Integer.parseInt(persNb) <= maxPersNb){
            wrongInputs.put("Person Number", true);  // TODO: Use replace when set SDK min24
            personNumber = Integer.parseInt(persNb);
        } else {
            errorLogs.add("Person number: should be a number between 0 and " + maxPersNb + ".");
        }

        EditText prepTimeInput = getView().findViewById(R.id.prepTimeInput);
        String prep = prepTimeInput.getText().toString();
        estimatedPreparationTime = getAndCheckTime(prep,"Preparation Time");

        EditText cookTimeInput = getView().findViewById(R.id.cookTimeInput);
        String cook = cookTimeInput.getText().toString();
        estimatedCookingTime = getAndCheckTime(cook,"Cooking Time");

        recipeDifficulty = Recipe.Difficulty.values()[difficultyInput.getSelectedItemPosition()];
        wrongInputs.put("Difficulty", true);
    }

    private int getAndCheckTime(String input, String message){
        if (input.length()!=0 && android.text.TextUtils.isDigitsOnly(input)) {
            wrongInputs.put(message, true);  // TODO: Use replace when set SDK min24
            return Integer.parseInt(input);
        } else {
            errorLogs.add(message+": should be a positive number.");
            return 0;
        }
    }

    private boolean parseIngredients(String toMatch) {
        List<String> allMatches = new ArrayList<>();
        ingredients = new ArrayList<>();
        Matcher mat = Pattern.compile("\\{[ ]*[A-Za-z0-9]*[ ]*,[ ]*[0-9]*[ ]*,[ ]*[A-Za-z0-9]*[ ]*\\}")
                .matcher(toMatch);
        while (mat.find()) {
            allMatches.add(mat.group());
        }
        if(allMatches.size()==0){
            ingredients.clear();
            allMatches.clear();
            errorLogs.add("Ingredients: There should be 3 arguments entered as {a,b,c}");
            return false;
        }
        for (String s : allMatches) {
            String[] list = s.split(",");
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
                errorLogs.add("Ingredients: The entered unit is not part of the possible units " + Arrays.asList(Ingredient.Unit.values()) + ".");
                return false;
            }

            try{
                ingredients.add(new Ingredient(name, quantity, unit));
            } catch (IllegalArgumentException e){
                errorLogs.add("Ingredients: " + e.toString().substring(35));
            }
        }
        return true;
    }

    private boolean parseInstructions(String instructions) {
        // TODO: Add more precise input checking of instructions
        if (instructions.length()<3 || !instructions.contains("{") || !instructions.contains("}")){
            errorLogs.add("Instructions: the entered instructions should match format {a},{b},... (no spaces)");
            return false;
        }

        recipeInstructions = new ArrayList<>();
        instructions = instructions.substring(1);
        String separator = Pattern.quote("},{");
        String[] mots = instructions.split(separator);
        for (int i = 0; i < mots.length - 1; i++) {
            recipeInstructions.add(mots[i]);
        }
        recipeInstructions.add(mots[mots.length - 1].substring(0, mots[mots.length - 1].length() - 1));
        return true;
    }

    private boolean buildRecipeAndPostToFirebase() {
        RecipeBuilder recipeBuilder = new RecipeBuilder();

        // By first checking the parsing part is right first we avoid the second checking part (would fail due to the errors in parsing)
        if (wrongInputs.values().contains(false) || !checkForIllegalInputs(recipeBuilder)) {
            return false;
        } else {
            if(currentMiniature != null) {
                imageHandler.uploadFromUri(currentMiniature, miniatureName, "TODO:USER", postedRecipe.getUuid().toString());
            }
            for(int i = 1; i <= currentMealPictures.size(); ++i) {
                imageHandler.uploadFromUri(currentMealPictures.get(i-1), postedRecipe.getUuid().toString() + "_" + i, "TODO:USER", postedRecipe.getUuid().toString());
            }
            recipeStorage.addRecipe(postedRecipe);
            return true;
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
            rb.build();

        } catch (IllegalArgumentException e) {
            findIllegalInputs(new RecipeBuilder());
            return false;
        }

        if(currentMiniature != null) {
            rb.setMiniatureFromPath(miniatureName);
        }

        for(int i = 1; i <= currentMealPictures.size(); ++i) {
            rb.addPicturePath(i);
        }

        postedRecipe = rb.build();
        return true;
    }

    private void findIllegalInputs(RecipeBuilder rb) {

        try{
            rb.setPersonNumber(personNumber);
        }  catch (IllegalArgumentException e){
            errorLogs.add("Person number: " + e.toString().substring(35));
        }

        try{
            rb.setEstimatedPreparationTime(estimatedPreparationTime);
        }  catch (IllegalArgumentException e){
            errorLogs.add("Preparation time: " + e.toString().substring(35));
        }

        // All the other exceptions cannot be raised, they are checked while parsing
    }

    private void printWrongInputsToUser(){
        TextView errorLog =  getView().findViewById(R.id.errorLogs);
        errorLog.setText(createErrorMessage());
        errorLog.setVisibility(View.VISIBLE);

        initializeWrongInputs();
        errorLogs.clear();
    }

    private String createErrorMessage(){
        StringBuilder sb = new StringBuilder();
        // So the errors are displayed by category
        Collections.sort(errorLogs);

        sb.append("There are errors in the given inputs :");
        for(int i = 0 ; i < errorLogs.size() ; ++i){
            sb.append("\n");
            sb.append(errorLogs.get(i));
        }

        return sb.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode / mealPicturesFactor > 0) {
            Uri uri = imageHandler.handleActivityResult(requestCode / mealPicturesFactor, resultCode, data);
            if(uri != null) {
                currentMealPictures.add(uri);
                mealPicturesText.setText(currentMealPictures.size() + " to upload");
            }
        } else {
            currentMiniature = imageHandler.handleActivityResult(requestCode, resultCode, data);
            if(currentMiniature != null) {
                try {
                    Bitmap oldBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), currentMiniature);
                    if(oldBitmap != null) {
                        double newWidth = getView().findViewById(R.id.miniatureLayout).getWidth();
                        double newHeight = oldBitmap.getHeight() * (newWidth / oldBitmap.getWidth());
                        Bitmap newBitmap = Bitmap.createScaledBitmap(oldBitmap, (int)newWidth, (int)newHeight, true);
                        imageMiniaturePreview.setImageBitmap(newBitmap);
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), getString(R.string.ErrorOccurred), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void addPictureDialog(int factor) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                startActivityForResult(imageHandler.getCameraIntent(), ImageHandler.REQUEST_IMAGE_CAPTURE * factor);
            } else if (options[item].equals("Choose from Gallery")) {
                startActivityForResult(imageHandler.getGalleryIntent(), ImageHandler.REQUEST_IMAGE_FROM_GALLERY * factor);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}

//TODO: Refactor by adding a new class RecipeInputSanitization in package Recipe ? Would contain findIllegalInputs, checkForIllegalInputs, parseInstructions, parseIngredients, getEnteredInputs ?

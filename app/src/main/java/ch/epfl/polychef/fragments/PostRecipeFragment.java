package ch.epfl.polychef.fragments;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageHandler;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class PostRecipeFragment extends Fragment {
    private static final String TAG = "PostRecipeFragment";
    private static final int MINIATURE_FACTOR = 1;
    private static final int MEAL_PICTURES_FACTOR = 10;
    private static final int TITLE_MAX_CHAR = 80;
    private static final int TITLE_MIN_CHAR = 3;
    private static final int MAX_PERSON_NUMBER = 100;
    private static final int MAX_INSTRUCTIONS = 20;
    private static final int MAX_INGREDIENTS = 20;
    private int numberOfInstruction = 1;
    private int numberOfIngredients = 1;
    private String name;
    private List<Integer> instructionsId = new ArrayList<>();
    private List<String> recipeInstructions = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();
    private int personNumber;
    private int estimatedPreparationTime;
    private int estimatedCookingTime;
    private Recipe.Difficulty recipeDifficulty;
    private Recipe postedRecipe;
    private LinearLayout instructionLayout;
    private LinearLayout ingredientLayout;
    private EditText instructionText;
    private Button addIngredientButton;
    private Button addInstructionButton;
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

    private HomePage hostActivity;

    /**
     * Required empty public constructor.
     */
    public PostRecipeFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            hostActivity = (HomePage) context;
        } else {
            throw new IllegalArgumentException("The user profile fragment wasn't attached properly!");
        }
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

        instructionLayout = getView().findViewById(R.id.listOfInstructions);
        ingredientLayout = getView().findViewById(R.id.listOfIngredients);
        instructionText = getView().findViewById(R.id.instruction0);
        instructionsId.add(instructionText.getId());


        addInstructionButton = getView().findViewById(R.id.buttonAddInstr);
        addInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddInstructionButton(view);
            }
        });
        addIngredientButton = getView().findViewById(R.id.buttonAddIngre);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddIngredientButton(view);
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
                addPictureDialog(MINIATURE_FACTOR);
            }
        });
        addPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPictureDialog(MEAL_PICTURES_FACTOR);
            }
        });

        difficultyInput = getView().findViewById(R.id.difficultyInput);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.difficulty_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultyInput.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        Recipe originalRecipe = (Recipe) bundle.getSerializable("ModifyRecipe");
        if(originalRecipe!=null){
            initializeFromOriginalRecipe(originalRecipe);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode / MEAL_PICTURES_FACTOR > 0) {
            Uri uri = imageHandler.handleActivityResult(requestCode / MEAL_PICTURES_FACTOR, resultCode, data);
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

    /**
     * Called when user presses "post recipe", will parse and check the entered inputs.
     * If the inputs are correct it will post the corresponding Recipe on Firebase.
     * Otherwise it will update the View to display to wrong inputs.
     * @param view the current view
     */
    public synchronized void setPostButton(View view) {
        getAndCheckEnteredInputs();
        if(!buildRecipeAndPostToFirebase()){
            printWrongInputsToUser();
        }else{
            try{
                wait(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            // Send notification to all users subscribed to the current user
            User currentUser = hostActivity.getUserStorage().getPolyChefUser();
            hostActivity.getNotificationSender().sendNewRecipe(currentUser.getKey(), currentUser.getUsername(), postedRecipe);

            Intent intent = new Intent(getActivity(), HomePage.class);
            startActivity(intent);
        }
    }

    private boolean buildRecipeAndPostToFirebase() {
        RecipeBuilder recipeBuilder = new RecipeBuilder();

        // By first checking the parsing part is right first we avoid the second checking part (would fail due to the errors in parsing)
        if (wrongInputs.values().contains(false) || !checkForIllegalInputs(recipeBuilder)) {
            return false;
        } else {
            postedRecipe=recipeBuilder.build();
            if(currentMiniature != null) {
                imageHandler.uploadFromUri(currentMiniature, miniatureName, getUserEmail(), postedRecipe.getRecipeUuid());
            }
            for(int i = 0; i < currentMealPictures.size(); ++i) {
                imageHandler.uploadFromUri(currentMealPictures.get(i), postedRecipe.getPicturesPath().get(i), getUserEmail(), postedRecipe.getRecipeUuid());
            }
            hostActivity.getRecipeStorage().addRecipe(postedRecipe);
            hostActivity.getUserStorage().getPolyChefUser().addRecipe(postedRecipe.getRecipeUuid()); //TODO need to check that the recipe was successfully added
            hostActivity.getUserStorage().updateUserInfo();

            return true;
        }
    }

    private void getAndCheckEnteredInputs() {
        String inputName = ((EditText)getView().findViewById(R.id.nameInput)).getText().toString();
        if(inputName.length() > TITLE_MAX_CHAR || inputName.length() < TITLE_MIN_CHAR) {
            errorLogs.add("Title: should be a string between " + TITLE_MIN_CHAR + " and " + TITLE_MAX_CHAR + " characters.");
        } else {
            wrongInputs.put("Title", true);
            name = inputName;
        }

        getAndCheckInstructions();

        getAndCheckIngredients();

        EditText personNb = getView().findViewById(R.id.personNbInput);
        String persNb = personNb.getText().toString();

        // checks are applied in order so parseInt is always valid
        // we only check persNb <= max since positiveness will already be check by builder
        if (persNb.length()!=0 && android.text.TextUtils.isDigitsOnly(persNb) && Integer.parseInt(persNb) <= MAX_PERSON_NUMBER){
            wrongInputs.replace("Person Number", true);
            personNumber = Integer.parseInt(persNb);
        } else {
            errorLogs.add("Number of Person: should be a number between 0 and " + MAX_PERSON_NUMBER + ".");
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
            wrongInputs.replace(message, true);
            return Integer.parseInt(input);
        } else {
            errorLogs.add(message+": should be a positive number.");
            return 0;
        }
    }

    private boolean checkForIllegalInputs(RecipeBuilder rb) {

        try {
            rb.setName(name)
                    .setEstimatedCookingTime(estimatedCookingTime)
                    .setPersonNumber(personNumber)
                    .setEstimatedPreparationTime(estimatedPreparationTime)
                    .setRecipeDifficulty(recipeDifficulty);
            for (int i = 0; i < recipeInstructions.size(); i++) {
                rb.addInstruction(recipeInstructions.get(i));
            }
            for (int i = 0; i < ingredients.size(); i++) {
                rb.addIngredient(ingredients.get(i));
            }
            if(currentMiniature != null) {
                rb.setMiniatureFromPath(miniatureName);
            }
            String uuidPath = miniatureName + "_";
            for(int i = 1; i <= currentMealPictures.size(); ++i) {
                rb.addPicturePath(uuidPath + i);
            }
            rb.setAuthor(getUserEmail()).build();

        } catch (IllegalArgumentException e) {
            findIllegalInputs(new RecipeBuilder());
            return false;
        }

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

        try{
            rb.setAuthor(getUserEmail());
        } catch (IllegalArgumentException e){
            errorLogs.add("User: " + e.toString().substring(35));
        }
        // All the other exceptions cannot be raised, they are checked while parsing
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

    private void printWrongInputsToUser(){
        TextView errorLog =  getView().findViewById(R.id.errorLogs);
        errorLog.setText(createErrorMessage());
        errorLog.setVisibility(View.VISIBLE);

        initializeWrongInputs();
        errorLogs.clear();
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

    private void setAddInstructionButton(View view) {
        //TODO: add message when trying to add to many instructions
        if(numberOfInstruction< MAX_INSTRUCTIONS){
            final ViewGroup.LayoutParams lparams = instructionText.getLayoutParams();
            final EditText textView = new EditText(getActivity());
            textView.setLayoutParams(lparams);
            numberOfInstruction++;
            textView.setHint("Instruction " + numberOfInstruction);
            int id = View.generateViewId();
            instructionsId.add(id);
            textView.setId(id);
            instructionLayout.addView(textView);
        }
    }

    private void setAddIngredientButton(View view) {
        //TODO: print a message when trying to add too many ingredients
        if(numberOfIngredients< MAX_INGREDIENTS){
            numberOfIngredients++;

            ConstraintLayout newIngredient = (ConstraintLayout) LayoutInflater.from(getContext()).inflate(R.layout.ingredient_field, null);
            ((TextView) newIngredient.getChildAt(0)).setHint("Ingredient " + numberOfIngredients);

            ingredientLayout.addView(newIngredient);
        }
    }

    private void getAndCheckInstructions(){
        recipeInstructions.clear();
        for (int i = 0; i < numberOfInstruction; i++) {
            String instruction1 = ((EditText) getView().findViewById(instructionsId.get(i))).getText().toString();
            if (instruction1.length() != 0) {
                recipeInstructions.add(instruction1);
            }
        }
        if(recipeInstructions.size()==0){
            errorLogs.add("Instruction: the number of instructions can't be 0");
            return;
        }
        wrongInputs.put("Instructions", true);
    }


    private void getAndCheckIngredients() {
        Double quantity;
        ingredients.clear();
        for (int i = 0; i < numberOfIngredients; i++) {
            ConstraintLayout currentIngredient = (ConstraintLayout) ingredientLayout.getChildAt(i);

            String ingredient1 = ((TextView) currentIngredient.getChildAt(0)).getText().toString();
            String quantity1 = ((TextView) currentIngredient.getChildAt(1)).getText().toString();
            Ingredient.Unit unit1 = Ingredient.Unit.values()[((Spinner) currentIngredient.getChildAt(2)).getSelectedItemPosition()];

            if (ingredient1.length() == 0 && quantity1.length() != 0) {
                errorLogs.add("Ingredient: the ingredient shouldn't be empty");
                return;
            } else if (ingredient1.length() != 0 && quantity1.length() == 0) {
                errorLogs.add("Ingredient: the quantity needs to be a positive number");
                return;
            } else if (ingredient1.length() != 0 && quantity1.length() != 0){
                quantity = Double.parseDouble(quantity1);
                ingredients.add(new Ingredient(ingredient1, quantity, unit1));
            }
        }
        if (ingredients.size() == 0) {
            errorLogs.add("Ingredient: the number of ingredients can't be 0");
            return;
        }
        wrongInputs.put("Ingredients", true);
    }


    private void initializeFromOriginalRecipe(Recipe originalRecipe) {
        EditText prepTimeInput = getView().findViewById(R.id.prepTimeInput);
        prepTimeInput.setText(Integer.toString(originalRecipe.getEstimatedPreparationTime()));

        EditText cookTimeInput = getView().findViewById(R.id.cookTimeInput);
        cookTimeInput.setText(Integer.toString(originalRecipe.getEstimatedCookingTime()));

        EditText personNb = getView().findViewById(R.id.personNbInput);
        personNb.setText(Integer.toString(originalRecipe.getPersonNumber()));

        EditText title=getView().findViewById(R.id.nameInput);
        title.setText(originalRecipe.getName());

        difficultyInput.setSelection(originalRecipe.getRecipeDifficulty().ordinal());

        List<String> instructions=originalRecipe.getRecipeInstructions();

        instructionText.setText(instructions.get(0));

        for (int i=1;i<instructions.size();++i){
            setAddInstructionButton(getView());
            ((TextView)getView().findViewById(instructionsId.get(i))).setText(instructions.get(i));
        }


    }

    public String getUserEmail(){
        return hostActivity.getUserStorage().getPolyChefUser().getEmail();
    }

    protected RecipeStorage getRecipeStorage() {
        return RecipeStorage.getInstance();
    }

    protected UserStorage getUserStorage() {
        return UserStorage.getInstance();
    }
}
package ch.epfl.polychef.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ToggleButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class FavouriteUtilsTest {

    private Recipe recipe = getRecipe("test");

    private Recipe recipe2 = getRecipe("test2");

    private Recipe getRecipe(String name) {
        return new RecipeBuilder()
                .setName(name)
                .addInstruction("test instruction")
                .setPersonNumber(4)
                .setEstimatedCookingTime(35)
                .setEstimatedPreparationTime(40)
                .addIngredient("test", 1.0, Ingredient.Unit.CUP)
                .setMiniatureFromPath("test_path")
                .setRecipeDifficulty(Recipe.Difficulty.EASY)
                .build();
    }

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            false);

    private FavouritesUtils fakeFavouritesUtils = Mockito.mock(FavouritesUtils.class, CALLS_REAL_METHODS);
    private RecipeStorage fakeRecipeStorage = Mockito.mock(RecipeStorage.class);
    private UserStorage fakeUserStorage = Mockito.mock(UserStorage.class);
    private User fakeUser = Mockito.mock(User.class);

    @Before
    public void initIntent() {
        Intents.init();
        when(fakeFavouritesUtils.getRecipeStorage()).thenReturn(fakeRecipeStorage);
        intentsTestRule.launchActivity(new Intent());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    @Test
    public void nullRecipeOrButtonThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> FavouritesUtils.getInstance().setFavouriteButton(null, new ToggleButton(intentsTestRule.getActivity()), null));
        assertThrows(IllegalArgumentException.class, () -> FavouritesUtils.getInstance().setFavouriteButton(null, null, new Recipe()));
    }

    @Test
    public void nullStorageSetButtonGone() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity());
        toggleButton.setText("TEST");
        FavouritesUtils.getInstance().setFavouriteButton(null, toggleButton, recipe);
        assertThat(toggleButton.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void canGetAFavouriteFromOnline() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton.setText("TEST");
        when(fakeUserStorage.getPolyChefUser()).thenReturn(fakeUser);
        List<String> list = new ArrayList<>();
        list.add(recipe.getRecipeUuid());
        when(fakeUser.getFavourites()).thenReturn(list);
        intentsTestRule.getActivity().runOnUiThread(() -> {
            ConstraintLayout constraintLayout = intentsTestRule.getActivity().findViewById(R.id.offlineMiniaturesFragment);
            constraintLayout.addView(toggleButton);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            assertThat(toggleButton.isChecked(), equalTo(true));
        });
    }

    @Test
    public void multipleButtonCanHaveDifferentState() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton.setText("TEST");
        ToggleButton toggleButton2 = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton2.setText("TEST2");
        when(fakeUserStorage.getPolyChefUser()).thenReturn(fakeUser);
        List<String> list = new ArrayList<>();
        list.add(recipe.getRecipeUuid());
        when(fakeUser.getFavourites()).thenReturn(list);
        intentsTestRule.getActivity().runOnUiThread(() -> {
            ConstraintLayout constraintLayout = intentsTestRule.getActivity().findViewById(R.id.offlineMiniaturesFragment);
            constraintLayout.addView(toggleButton);
            constraintLayout.addView(toggleButton2);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton2, recipe2);
            assertThat(toggleButton.isChecked(), equalTo(true));
            assertThat(toggleButton2.isChecked(), equalTo(false));
            assertThat(fakeUser.getFavourites(), hasItem(recipe.getRecipeUuid()));
            assertThat(fakeUser.getFavourites(), hasSize(1));
        });
    }

    @Test
    public void onClickCanAddToFavourites() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton.setText("TEST");
        when(fakeUserStorage.getPolyChefUser()).thenReturn(fakeUser);
        intentsTestRule.getActivity().runOnUiThread(() -> {
            ConstraintLayout constraintLayout = intentsTestRule.getActivity().findViewById(R.id.offlineMiniaturesFragment);
            constraintLayout.addView(toggleButton);
            assertThat(toggleButton.isChecked(), equalTo(false));
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            assertThat(toggleButton.isChecked(), equalTo(false));
            toggleButton.performClick();
            assertThat(toggleButton.isChecked(), equalTo(true));
            verify(fakeUserStorage.getPolyChefUser()).addFavourite(recipe.getRecipeUuid());
            verify(fakeUserStorage).updateUserInfo();
            assertThat(fakeUser.getFavourites(), is(empty()));
        });
    }

    @Test
    public void onClickCanRemoveFromFavourites() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton.setText("TEST");
        when(fakeUserStorage.getPolyChefUser()).thenReturn(fakeUser);
        List<String> list = new ArrayList<>();
        list.add(recipe.getRecipeUuid());
        when(fakeUser.getFavourites()).thenReturn(list);
        intentsTestRule.getActivity().runOnUiThread(() -> {
            ConstraintLayout constraintLayout = intentsTestRule.getActivity().findViewById(R.id.offlineMiniaturesFragment);
            constraintLayout.addView(toggleButton);
            assertThat(toggleButton.isChecked(), equalTo(false));
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            assertThat(toggleButton.isChecked(), equalTo(true));
            toggleButton.performClick();
            assertThat(toggleButton.isChecked(), equalTo(false));
            verify(fakeUserStorage.getPolyChefUser()).removeFavourite(recipe.getRecipeUuid());
            verify(fakeUserStorage).updateUserInfo();
        });
    }

    @Test
    public void addingFavouriteAddAlsoLocally() {
        ToggleButton toggleButton = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton.setText("TEST");
        ToggleButton toggleButton2 = new ToggleButton(intentsTestRule.getActivity().getApplicationContext());
        toggleButton2.setText("TEST2");
        when(fakeUserStorage.getPolyChefUser()).thenReturn(fakeUser);
        setSharedPref(new ArrayList<>());
        intentsTestRule.getActivity().runOnUiThread(() -> {
            assertThat(FavouritesUtils.getInstance().getOfflineFavourites(), is(empty()));
            ConstraintLayout constraintLayout = intentsTestRule.getActivity().findViewById(R.id.offlineMiniaturesFragment);
            constraintLayout.addView(toggleButton);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton, recipe);
            FavouritesUtils.getInstance().setFavouriteButton(fakeUserStorage, toggleButton2, recipe2);
            toggleButton.performClick();
            toggleButton2.performClick();
            assertThat(FavouritesUtils.getInstance().getOfflineFavourites(), hasItems(recipe, recipe2));
            assertThat(FavouritesUtils.getInstance().getOfflineFavourites(), hasSize(2));
            toggleButton.performClick();
            assertThat(FavouritesUtils.getInstance().getOfflineFavourites(), hasItem(recipe2));
            assertThat(FavouritesUtils.getInstance().getOfflineFavourites(), hasSize(1));
        });
    }

    @Test
    public void canNotSetOfflineFavouritesIfUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> FavouritesUtils.getInstance().setOfflineFavourites(null));
    }

    public static void setSharedPref(List<Recipe> recipes) {
        SharedPreferences sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences("FavouriteList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String recipesJson = gson.toJson(recipes);
        editor.putString("favourites", recipesJson);
        editor.apply();
    }
}

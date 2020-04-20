package ch.epfl.polychef.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class FavouritesFragmentTest {


    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    private FakeFavouriteFragment fakeFavouriteFragment = new FakeFavouriteFragment();

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Mock
    private RecipeStorage fakeRecipeStorage;

    @Mock
    FirebaseDatabase firebaseInstance;

    @Mock
    UserStorage mockUserStorage;

    @Mock
    User mockPolyChefUser;

    @Before
    public void initMockAndStorage() {
        MockitoAnnotations.initMocks(this);
        when(fakeRecipeStorage.getFirebaseDatabase()).thenReturn(firebaseInstance);
        when(mockUserStorage.getAuthenticatedUser()).thenReturn(mock(FirebaseUser.class));
        when(mockUserStorage.getPolyChefUser()).thenReturn(mockPolyChefUser);
    }

    public synchronized void setup() {
        intentsTestRule.launchActivity(new Intent());
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_host_fragment);
        Fragment fragment = fakeFavouriteFragment;
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.onlineMiniaturesFragment, fragment).addToBackStack(null);
        transaction.commit();
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Recipe getRecipe(String name) {
        return new RecipeBuilder().setName(name)
                .setRecipeDifficulty(Recipe.Difficulty.EASY)
                .addInstruction("test1instruction").setPersonNumber(4)
                .setEstimatedCookingTime(30).setEstimatedPreparationTime(30)
                .addIngredient("test1", 1.0, Ingredient.Unit.CUP)
                .build();
    }

     @Test
     public void emptyFavouritesOnlineShowNoRecipe() {
        setup();
        fakeFavouriteFragment.isOnline = true;
        when(mockPolyChefUser.getFavourites()).thenReturn(new ArrayList<>());
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(0));
     }

    @Test
    public void emptyFavouritesOfflineShowNoRecipe() {
        fakeFavouriteFragment.isOnline = false;
        setSharedPref(new ArrayList<>());
        setup();
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(0));
    }

    @Test
    public void oneFavouriteCanBeShownOnline() {
        fakeFavouriteFragment.isOnline = true;
        Map<String, Recipe> recipesInFavourite = new HashMap<>();
        Recipe recipe = getRecipe("test");
        recipesInFavourite.put(recipe.getRecipeUuid(), recipe);
        when(mockPolyChefUser.getFavourites()).thenReturn(new ArrayList<>(recipesInFavourite.keySet()));
        doAnswer((call) -> {
            CallHandler<Recipe> ch = call.getArgument(1);
            ch.onSuccess(recipe);
            return null;
        }).when(fakeRecipeStorage).readRecipeFromUuid(eq(recipe.getRecipeUuid()), any(CallHandler.class));
        setup();
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(1));
    }

    @Test
    public void oneFavouriteCanBeShownOffline() {
        fakeFavouriteFragment.isOnline = false;
        Recipe recipe = getRecipe("test");
        setSharedPref(Collections.singletonList(recipe));
        setup();
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(1));
    }

    @Test
    public synchronized void moreRecipesChargeOnlyFiveOfflineAndAddMoreOnScroll() throws InterruptedException {
        fakeFavouriteFragment.isOnline = false;
        List<Recipe> recipesInFavourite = new ArrayList<>();
        for(int i = 0; i < 13; ++i) {
            recipesInFavourite.add(getRecipe("test"+i));
        }
        setSharedPref(recipesInFavourite);
        setup();
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(5));
        onView(ViewMatchers.withId(R.id.miniaturesFavouriteList))
                .perform(RecyclerViewActions.scrollToPosition(4))
                .perform(ViewActions.swipeUp());
        wait(1000);
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(10));
        onView(ViewMatchers.withId(R.id.miniaturesFavouriteList))
                .perform(RecyclerViewActions.scrollToPosition(9))
                .perform(ViewActions.swipeUp());
        wait(1000);
        assertThat(fakeFavouriteFragment.getRecyclerView().getAdapter().getItemCount(), is(13));
    }

    private void setSharedPref(List<Recipe> recipes) {
        SharedPreferences sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences("FavouriteList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String recipesJson = gson.toJson(recipes);
        editor.putString("favourites", recipesJson);
        editor.apply();
    }

    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return mock(FirebaseUser.class);
        }


        @Override
        public UserStorage getUserStorage(){
            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            return fakeRecipeStorage;
        }
    }

    public static class FakeFavouriteFragment extends FavouritesFragment {
        public boolean isOnline = true;

        @Override
        public boolean isOnline() {
            return isOnline;
        }
    }
}

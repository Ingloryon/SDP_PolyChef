package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuantityTest {

    private User mockUser;

    private FragmentTestUtils fragUtils = new FragmentTestUtils();

    private RecipeBuilder recipeBuilder = new RecipeBuilder()
            .addInstruction("test instruction")
            .setPersonNumber(4)
            .setEstimatedCookingTime(35)
            .setEstimatedPreparationTime(40)
            .addIngredient("test", 1.0, Ingredient.Unit.CUP)
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setDate("20/05/01 12:00:00")
            .setAuthor("testAuthor");

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public synchronized void initTest() {
        mockUser = new User("mock@email.com", "mockUsername");
        Intents.init();
        intentsTestRule.launchActivity(new Intent());
    }

    private class FakeHomePage extends HomePage {

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = mock(UserStorage.class);
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser);

            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = mock(RecipeStorage.class);

            when(mockRecipeStorage.getCurrentDate()).thenReturn(RecipeStorage.OLDEST_RECIPE);

            doAnswer((call) -> {
                CallHandler<List<Recipe>> ch = call.getArgument(4);
                List<Recipe> results = new ArrayList<>();
                results.add(recipeBuilder.setName("test1").build());
                ch.onSuccess(results);
                return null;
            }).when(mockRecipeStorage).getNRecipes(any(Integer.class), any(String.class), any(String.class), any(Boolean.class), any(CallHandler.class));

            return mockRecipeStorage;
        }

        @Override
        public ImageStorage getImageStorage() {
            return mock(ImageStorage.class);
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = mock(FirebaseUser.class);
            return mockUser;
        }

    }
    @Test
    public void defaultQuantityNumberIsCorrect(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + currentFragment.getCurrentRecipe().getPersonNumber())));
    }

    @Test
    public void writtingNothingWorks(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.quantityinput)).perform(clearText());
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        assertEquals(1, currentFragment.getCurrentRecipe().getPersonNumber());
    }
    @Test
    public void changeQuantityActuallyChangeQuantity(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.quantityinput)).perform(clearText());
        onView(withId(R.id.quantityinput)).perform(typeText("" + 9));
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + currentFragment.getCurrentRecipe().getPersonNumber())));
    }
}

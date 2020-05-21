package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.images.RecipeMiniatureImageTest;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuantityTest {

    private User mockUser;

    private FragmentTestUtils fragUtils = new FragmentTestUtils();

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

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    private class FakeHomePage extends HomePage {

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = RecipeMiniatureImageTest.getMockUserStorage(mockUser);
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
                results.add(RecipeMiniatureImageTest.recipeBuilder.setName("test1").build());
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
    public void exceedQuantityDisplayAToast() throws InterruptedException {
        textClear();
        wait(3000);
        onView(withId(R.id.quantityinput)).perform(typeText("" + FullRecipeFragment.QUANTITY_LIMIT + 1));
        onView(withText("The quantity limit is : " + FullRecipeFragment.QUANTITY_LIMIT))
                .inRoot(withDecorView(not(is(intentsTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void zeroQuantityDisplayAToast() throws InterruptedException {
        textClear();
        wait(3000);
        onView(withId(R.id.quantityinput)).perform(typeText("" + 0));
        onView(withText("The quantity can't be 0"))
                .inRoot(withDecorView(not(is(intentsTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void defaultQuantityNumberIsCorrect(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + currentFragment.getCurrentRecipe().getPersonNumber())));
    }

    @Test
    public void writingNothingWorks(){
        textClear();
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        assertEquals(1, currentFragment.getCurrentRecipe().getPersonNumber());
    }

    @Test
    public void changeQuantityActuallyChangeQuantity(){
        textClear();
        onView(withId(R.id.quantityinput)).perform(typeText("" + 9));
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + currentFragment.getCurrentRecipe().getPersonNumber())));
    }

    @Test
    public void exceedQuantityChangeInputToMaxOne(){
        textClear();
        onView(withId(R.id.quantityinput)).perform(typeText("" + FullRecipeFragment.QUANTITY_LIMIT + 1));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + FullRecipeFragment.QUANTITY_LIMIT)));
    }

    @Test
    public void zeroQuantityPutToOne(){
        textClear();
        onView(withId(R.id.quantityinput)).perform(typeText("" + 0));
        onView(withId(R.id.quantityinput)).check(matches(withText("" + 1)));
    }

    private void textClear(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        FullRecipeFragment currentFragment = ((FullRecipeFragment) fragUtils.getTestedFragment(intentsTestRule));
        try {
            intentsTestRule.runOnUiThread(() -> {
                EditText quantityText = currentFragment.getView().findViewById(R.id.quantityinput);
                quantityText.setText("");
            });
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

}

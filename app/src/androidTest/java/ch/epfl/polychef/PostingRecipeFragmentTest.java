package ch.epfl.polychef;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.polychef.fragments.PostRecipeFragment;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.HomePage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PostingRecipeFragmentTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new PostingRecipeFragmentTest.FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Before
    public void initActivity(){
        intentsTestRule.launchActivity(new Intent());
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_recipe));
        onView(withId(R.id.drawer)).perform(DrawerActions.close());
    }

    @Test
    public void checkThatFragmentIsDisplayed() {
        onView(withId(R.id.postRecipeFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickPostRecipeWithEmptyDisplaysErrorLogs() {
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(click());
        onView(withId(R.id.errorLogs)).check(matches(isDisplayed()));
        (onView(withId(R.id.errorLogs))).check(matches(withText("There are errors in the given inputs :" +
                "\nCooking Time: should be a positive number." +
                "\nIngredients: There should be 3 arguments entered as {a,b,c}" +
                "\nInstructions: the entered instructions should match format {a},{b},... (no spaces)" +
                "\nPerson number: should be a number between 0 and 100." +
                "\nPreparation Time: should be a positive number." +
                "\nTitle: too long or too short. Need to be between 3 and 80 characters.")));
    }

    //@Test
    //public void validInputsAreSentToFirebase() {
        //TODO
    //}

    //@Test
    //public void rejectsTooLongTitles() {
        //TODO: change the EditText of title to a long value and check displays string "blabla name too long" in onView(withId(R.id.errorLogs))
    //}

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }
    }
}

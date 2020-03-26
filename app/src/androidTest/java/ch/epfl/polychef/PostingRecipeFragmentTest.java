package ch.epfl.polychef;

import android.content.Intent;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
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
        checkErrorLog("There are errors in the given inputs :" +
                "\nCooking Time: should be a positive number." +
                "\nIngredients: There should be 3 arguments entered as {a,b,c}" +
                "\nInstructions: the entered instructions should match format {a},{b},... (no spaces)" +
                "\nPerson number: should be a number between 0 and 100." +
                "\nPreparation Time: should be a positive number." +
                "\nTitle: too long or too short. Need to be between 3 and 80 characters.");
    }

    @Test
    public void onClickPostRecipeWithEverythingButNameDisplaysErrorLogs() {
        writeRecipe("","{a,1,gram},{b,2,cup}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :" +
                "\nTitle: too long or too short. Need to be between 3 and 80 characters.");
    }

    @Test
    public void nullUnitInIngredientDisplaysErrorLogs() {
        writeRecipe("Cake","{a,1,null},{a,1}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :" +
                "\nIngredients: The entered unit is not part of the possible units " +
                "[TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM, CUP, OUNCE, NO_UNIT, NONE].");
        }

    @Test
    public void noSeparatorInIngredientDisplaysErrorLogs() {
        writeRecipe("Cake","{a}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :" +
                "\nIngredients: There should be 3 arguments entered as {a,b,c}");
    }

    @Test
    public void negativeQuantityDisplaysErrorLogs() {
        writeRecipe("Cake","{a,-1,gram}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :" +
                "\nIngredients: There should be 3 arguments entered as {a,b,c}");
    }

    @Test
    public void testOnACompleteRecipe() {
        writeRecipe("aaaa","{a,1,gram},{b,2,cup}","{a},{b}","10","10", "10");
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(scrollTo(),click());
    }

    private void writeRecipe(String name, String ingre, String instru, String personNb, String prep, String cook){
        onView(withId(R.id.nameInput)).perform(typeText(name));
        onView(withId(R.id.ingredientsList)).perform(typeText(ingre));
        onView(withId(R.id.instructionsList)).perform(typeText(instru));
        onView(withId(R.id.personNbInput)).perform(typeText(personNb));
        onView(withId(R.id.prepTimeInput)).perform(typeText(prep));
        onView(withId(R.id.cookTimeInput)).perform(typeText(cook));
    }

    private void checkErrorLog(String expected){
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(scrollTo(), click());;
        onView(withId(R.id.errorLogs)).check(matches(isDisplayed()));
        (onView(withId(R.id.errorLogs))).check(matches(withText(expected)));
    }

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }
    }
}

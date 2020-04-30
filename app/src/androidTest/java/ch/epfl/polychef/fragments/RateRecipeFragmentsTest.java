package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.pages.HomePageTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class RateRecipeFragmentsTest {

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    /*private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new HomePageTest.FakeHomePage();
            return activity;
        }
    };*/

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            true);


    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateButtonIsDisplayedAndDisplayCorrectText(){
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonRate)).check(matches(withText(R.id.buttonRate)));
    }

    /*public void rateSpinnerHasTheRightTexts() {
        onView(withId(R.id.RateChoices)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(selectionText))).perform(click());
        onView(withId(R.id.RateChoices)).check(matches(withSpinnerText(containsString(selectionText))));
    }*/

    @Test
    public void toastIsDisplayedIfTryToRateWhileNotLoggedIn(){
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(click());

    }


}

package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class RateRecipeFragmentsEntryTest {

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRuleEntry = new ActivityTestRule<>(fakeEntryPage, false,
            true);

    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateButtonIsDisplayedAndDisplayCorrectText(){

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(NestedScrollViewHelper.nestedScrollTo(),click());
        onView(withId(R.id.buttonRate)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonRate)).check(matches(withText(R.string.RateButton)));
    }

    @Test
    public void toastIsDisplayedIfTryToRateWhileNotLoggedIn(){

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(NestedScrollViewHelper.nestedScrollTo(),click());
        onView(withId(R.id.buttonRate)).perform(click());
        onView(withText(R.string.errorOnlineFeature))
                .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleEntry.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

}

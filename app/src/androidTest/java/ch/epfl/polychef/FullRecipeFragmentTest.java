package ch.epfl.polychef;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FullRecipeFragmentTest {
    @Rule
    public IntentsTestRule<EntryPage> intentsTestRule = new IntentsTestRule<>(EntryPage.class);

    @Test
    public void fragmentIsVisible() {
        onView(withId(R.id.miniaturesOfflineList)).check(matches(isDisplayed()));
    }

    @Test
    public void canClickOnFirstElement() {
        onView(withId(R.id.fullRecipeFragment)).check(doesNotExist());
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.fullRecipeFragment)).check(matches(isDisplayed()));
    }
}

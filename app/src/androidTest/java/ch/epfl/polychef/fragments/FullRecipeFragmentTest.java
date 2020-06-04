package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class FullRecipeFragmentTest {

    private FullRecipeFragment fragment;

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
            true);

    @Test
    public void fragmentIsVisible() {
        onView(ViewMatchers.withId(R.id.miniaturesOfflineList)).check(matches(isDisplayed()));
    }

    @Test
    public void canClickOnFirstElement() {
        onView(withId(R.id.miniaturesOfflineList)).check(matches(isDisplayed()));
        onView(withId(R.id.fullRecipeFragment)).check(doesNotExist());
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.fullRecipeFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void switchIsDisplayedCorrectly(){
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.voiceRecognitionSwitch)).check(matches(isDisplayed()));
        onView(withId(R.id.voiceRecognitionSwitch)).check(matches(withText("Voice Recognition")));
    }

    @Test
    public void clickOnSwitch(){
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.voiceRecognitionSwitch)).perform(click());
        onView(withId(R.id.voiceRecognitionSwitch)).check(matches(isChecked()));
        onView(withId(R.id.voiceRecognitionSwitch)).perform(click());
        onView(withId(R.id.voiceRecognitionSwitch)).check(matches(isNotChecked()));
    }

    @Test
    public void notifyTest(){
        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();
        Fragment fragment= fragmentManager.getFragments().get(0);
        FullRecipeFragment fullRecipeFragment = (FullRecipeFragment) fragment;
        fullRecipeFragment.notify("next");
        assertThat(fullRecipeFragment.getIndexOfInstruction(),is(0));
        fullRecipeFragment.notify("next");
        assertThat(fullRecipeFragment.getIndexOfInstruction(),is(1));
        fullRecipeFragment.notify("previous");
        assertThat(fullRecipeFragment.getIndexOfInstruction(),is(0));
        fullRecipeFragment.notify("next");
        fullRecipeFragment.notify("next");
    }
}

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
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


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

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new HomePageTest.FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule_Entry = new ActivityTestRule<>(fakeEntryPage, false,
            false);
    @Rule
    public ActivityTestRule<HomePage> intentsTestRule_Home = new ActivityTestRule<>(fakeHomePage, false,
            false);


    private void launchFakeEntryPage() {
        intentsTestRule_Entry.launchActivity(new Intent());
    }

    private void launchFakeHomePage() {
        intentsTestRule_Home.launchActivity(new Intent());
    }

    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateButtonIsDisplayedAndDisplayCorrectText(){
        launchFakeEntryPage();

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonRate)).check(matches(withText(R.string.RateButton)));
    }

    @Test
    public void toastIsDisplayedIfTryToRateWhileNotLoggedIn(){
        launchFakeEntryPage();

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(click());
        onView(withText(R.string.errorOnlineFeature))
                .inRoot(RootMatchers.withDecorView(not(is(intentsTestRule_Entry.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void rateSpinnerCanBeClickedOn() {
        launchFakeHomePage();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(click());
        onView(withId(R.id.RateChoices)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("0 star"))).perform(click());
        onView(withId(R.id.RateChoices)).check(matches(withSpinnerText(containsString("0 star"))));
    }


    @Test
    public void test(){
        //FullRecipeFragment mockFullRecipeFragment= Mockito.mock(FullRecipeFragment.class);

        RateRecipeFragment rrf=new RateRecipeFragment();

        //mockFullRecipeFragment.
        //onViewCreated

        //navController.navigate(R.id.rateRecipeFragment, bundle);
    }

}

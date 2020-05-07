package ch.epfl.polychef.pages;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.R;
import ch.epfl.polychef.notifications.NotificationSenderTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FullRecipeHomePageTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new HomePageTest.FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public void initActivity() {
        Intent intent = new Intent();
        intent.putExtra("RecipeToSend", NotificationSenderTest.fakeRecipe);
        intentsTestRule.launchActivity(intent);
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    @Test
    public void checkCanDirectlyOpenFullRecipe() {
        onView(withId(R.id.fullRecipeFragment)).check(matches(isDisplayed()));
    }
}

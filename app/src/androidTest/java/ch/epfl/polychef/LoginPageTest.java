package ch.epfl.polychef;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {
    @Rule
    public IntentsTestRule<LoginPage> intentsTestRule = new IntentsTestRule<>(LoginPage.class);

    @Test
    public void canClickOnTequilaButton() {
        onView(withId(R.id.tequilaButton)).perform(click());
    }

    @Test
    public void canClickOnGoogleButton() {
        onView(withId(R.id.googleButton)).perform(click());
    }
}

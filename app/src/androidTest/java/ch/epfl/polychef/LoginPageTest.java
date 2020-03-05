package ch.epfl.polychef;

import androidx.test.espresso.Espresso;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.AuthUI;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {
    @Rule
    public IntentsTestRule<LoginPage> intentsTestRule = new IntentsTestRule<>(LoginPage.class);

    @Test
    public void canClickOnTequilaButton() {
        onView(withId(R.id.tequilaButton)).check(matches(isEnabled()));
    }

    @Test
    public void canClickOnGoogleButton() {
        onView(withId(R.id.googleButton)).check(matches(isEnabled()));
    }
}

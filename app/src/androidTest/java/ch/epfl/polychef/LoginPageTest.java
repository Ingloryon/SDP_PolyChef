package ch.epfl.polychef;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {

    private SingleActivityFactory<LoginPage> fakeLoginPage = new SingleActivityFactory<LoginPage>(
            LoginPage.class) {
        @Override
        protected LoginPage create(Intent intent) {
            LoginPage activity = new FakeLogin();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<LoginPage> activityTestRule = new ActivityTestRule<>(fakeLoginPage, true,
            true);

    @Before
    public void initActivity() {
        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(activityTestRule.getActivity());
    }

    @Test
    public void canClickOnTequilaButton() {
        onView(withId(R.id.tequilaButton)).check(matches(isEnabled()));
    }

    @Test
    public void canClickOnGoogleButton() {
        onView(withId(R.id.googleButton)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnGoogleButtonRaiseNoError() {
        onView(withId(R.id.googleButton)).perform(click());
    }

    @Test
    public void clickOnTequilaButtonRaiseNoError() {
        onView(withId(R.id.tequilaButton)).perform(click());
    }

    private class FakeLogin extends LoginPage {
        private static final int RC_SIGN_IN = 123;

        @Override
        public void createSignInIntent(View view) {
            onActivityResult(RC_SIGN_IN, RESULT_OK, null);
        }

        @Override
        public FirebaseUser getUser() {
            return mock(FirebaseUser.class);
        }
    }
}

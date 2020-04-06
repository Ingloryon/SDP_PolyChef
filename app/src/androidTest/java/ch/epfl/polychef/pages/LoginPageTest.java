package ch.epfl.polychef.pages;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.LoginPage;

import com.google.firebase.auth.FirebaseUser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
    public void canClickOnGoogleButton() {
        onView(ViewMatchers.withId(R.id.googleButton)).check(matches(isEnabled()));
    }

    @Test
    public void clickOnGoogleButtonRaiseNoError() {
        onView(withId(R.id.googleButton)).perform(click());
    }

    @Test
    public void wrongResultCodeShouldDisplayToast(){
        FakeLogin fakeLogin=(FakeLogin)activityTestRule.getActivity();
        fakeLogin.setResultCodeOnActivityResult(Activity.RESULT_CANCELED);

        onView(withId(R.id.googleButton)).perform(click());

        onView(withText(R.string.ErrorOccurred))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void wrongRequestCodeShouldDisplayToast(){
        FakeLogin fakeLogin=(FakeLogin)activityTestRule.getActivity();
        fakeLogin.setRequestCodeOnActivityResult(1);

        onView(withId(R.id.googleButton)).perform(click());

        onView(withText(R.string.ErrorOccurred))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    private class FakeLogin extends LoginPage {
        private int requestCodeOnActivityResult=123;
        private int resultCodeOnActivityResult=RESULT_OK;

        private void setResultCodeOnActivityResult(int result){
            this.resultCodeOnActivityResult=result;
        }

        private void setRequestCodeOnActivityResult(int request){
            this.requestCodeOnActivityResult=request;
        }

        @Override
        public void createSignInIntent(View view) {
            onActivityResult(requestCodeOnActivityResult, resultCodeOnActivityResult, null);
        }

        @Override
        public FirebaseUser getUser() {
            return mock(FirebaseUser.class);
        }

        @Override
        public void startNextActivity() {
            //Shouldn't start Homepage when testing the LoginPage
            //startActivity(new Intent(this, fakeHomePage.getActivityClassToIntercept()));
        }
    }
}

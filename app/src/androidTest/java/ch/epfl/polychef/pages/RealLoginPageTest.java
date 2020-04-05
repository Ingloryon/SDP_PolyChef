package ch.epfl.polychef.pages;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.firebase.ui.auth.AuthUI;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import ch.epfl.polychef.pages.LoginPage;

@RunWith(AndroidJUnit4.class)
public class RealLoginPageTest {

    @Rule
    public ActivityTestRule<LoginPage> activityTestRule = new ActivityTestRule<>(LoginPage.class);

    @Before
    public void initIntent() {
        Intents.init();
    }

    @After
    public void releaseIntent() {
        Intents.release();
    }

    @Test
    public void canCallCreateSignInIntent() {
        Intent action = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build();
        Intent resultData = new Intent(action);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasComponent(action.getComponent())).respondWith(result);
        activityTestRule.getActivity().createSignInIntent(null);
    }

    @Test
    public void getUserReturnNullWhenNotLoggedIn() {
        assertNull(activityTestRule.getActivity().getUser());
    }
}

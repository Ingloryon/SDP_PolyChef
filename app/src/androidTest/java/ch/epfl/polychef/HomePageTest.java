package ch.epfl.polychef;

import android.content.Intent;

import androidx.annotation.NonNull;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.espresso.contrib.DrawerActions;

import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;

import androidx.test.espresso.contrib.NavigationViewActions;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomePageTest {
    @Rule
    public IntentsTestRule<HomePage> intentsTestRule = new IntentsTestRule<>(HomePage.class,
            true, false);

    private boolean isInProgress = true;

    @Before
    public void createFakeConnectedUser() {
        final IdlingResource waitUser = new WaitForUser();
        IdlingRegistry.getInstance().register(waitUser);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("test@test.com", "testtest")
                .addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        isInProgress = false;
                    }
                });
        onIdle();
        intentsTestRule.launchActivity(new Intent());
    }

    @Test
    public void buttonTextIsLogout() {
        onView(withId(R.id.logButton)).check(matches(withText("Log out")));
    }

    @Test
    public void onClickLogoutGoToEntryPage() {
        onView(withId(R.id.logButton)).perform(click());
        intended(hasComponent(EntryPage.class.getName()));
    }

    @Test
    public void onClickHomeGoesToHome() {
        testNavButton(R.id.nav_home, R.id.homeFragment);
    }

    @Test
    public void onClickFavGoesToFav() {
        testNavButton(R.id.nav_fav, R.id.favouritesFragment);
    }

    @Test
    public void onClickSubscribersGoesToSubscribers() {
        testNavButton(R.id.nav_subscribers, R.id.subscribersFragment);
    }

    @Test
    public void onClickSubscriptionsGoesToSubscriptions() {
        testNavButton(R.id.nav_subscriptions, R.id.subscriptionsFragment);
    }

    @Test
    public void appNavCanBeOpened() {
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.drawer)).check(matches(isOpen()));
        onView(withId(R.id.drawer)).perform(DrawerActions.close());
        onView(withId(R.id.drawer)).check(matches(isClosed()));
    }

    private void testNavButton(int idButton, int idFragment) {
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(idButton));
        onView(withId(idFragment)).check(matches(isDisplayed()));
    }

    private class WaitForUser implements IdlingResource {

        private IdlingResource.ResourceCallback resourceCallback = null;
        private boolean currIdle = true;

        @Override
        public String getName() {
            return WaitForUser.class.toString();
        }

        @Override
        public boolean isIdleNow() {
            if (currIdle != isInProgress) {
                currIdle = isInProgress;
                resourceCallback.onTransitionToIdle();
            }
            return !isInProgress;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }
    }
}
package ch.epfl.polychef;

import android.content.Intent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.contrib.DrawerActions;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import androidx.test.espresso.contrib.NavigationViewActions;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class HomePageTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Before
    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    @Test
    public void buttonTextIsLogoutAndCanClick() {
        onView(withId(R.id.logButton)).check(matches(withText("Log out")));
        onView(withId(R.id.logButton)).perform(click());
    }
/*
    @Test
    public void onClickHomeGoesToHome() {
        testNavButton(R.id.nav_home, R.id.miniaturesOfflineFragment);
    }*/

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

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }
    }
}

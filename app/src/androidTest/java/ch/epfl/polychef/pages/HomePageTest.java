package ch.epfl.polychef.pages;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class HomePageTest {

    private HomePage activity;
    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            activity = new FakeHomePage();
            return activity;
        }
    }; 

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    @Test
    public void buttonTextIsLogoutAndClickLogsOut() {
        onView(ViewMatchers.withId(R.id.logButton)).check(matches(withText("Log out")));
        onView(withId(R.id.logButton)).perform(click());
    }

    @Test
    public void onClickHomeGoesToHome() {
        testNavButton(R.id.nav_home, R.id.onlineMiniaturesFragment);
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

    @Test
    public synchronized void backButtonTest() throws InterruptedException {
        int repetition = 6;
        int nbClickableElements = 6;
        List<Integer> menuItems = new ArrayList<>(nbClickableElements - 1);
        menuItems.add(R.id.nav_home);
        menuItems.add(R.id.nav_recipe);
        menuItems.add(R.id.nav_fav);
        menuItems.add(R.id.nav_subscribers);
        menuItems.add(R.id.nav_subscriptions);

        List<Integer> sequence = new ArrayList<>(repetition * nbClickableElements);
        for(int i = 0; i < repetition; ++i){
            sequence.addAll(menuItems);
            sequence.add(R.id.drawerProfileImage);
        }

        Collections.shuffle(sequence);

        NavigationView navView = intentsTestRule.getActivity().findViewById(R.id.navigationView);
        Menu menu = navView.getMenu();

        for(Integer id : sequence){
            onView(withId(R.id.drawer)).perform(DrawerActions.open());
            wait(100);

            navigateTo(id);
            wait(100);

            assertTrue(isTheOnlyOneChecked(id, menuItems, menu));
        }

        for(int i = sequence.size() - 1; i >= 0; --i){
            assertTrue(isTheOnlyOneChecked(sequence.get(i), menuItems, menu));
            Espresso.pressBack();
            wait(100);
        }
    }

    @Test
    public synchronized void canChangeDarkLightTheme() throws InterruptedException {
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        wait(100);
        int previous = AppCompatDelegate.getDefaultNightMode();
        onView(withId(R.id.nightModeSwitch)).perform(click());
        assertNotEquals(previous, AppCompatDelegate.getDefaultNightMode());
        previous = AppCompatDelegate.getDefaultNightMode();
        onView(withId(R.id.nightModeSwitch)).perform(click());
        assertNotEquals(previous, AppCompatDelegate.getDefaultNightMode());
    }

    public Boolean isTheOnlyOneChecked(int id, List<Integer> options, Menu menu){
        return options.stream().allMatch(optionId -> {
            MenuItem item = menu.findItem(optionId);
            return (optionId == id && item.isChecked()) || !item.isChecked();
        });
    }

    public void navigateTo(int id){
        if(id == R.id.drawerProfileImage){
            onView(withId(id)).perform(click());
        } else {
            onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(id));
            onView(withId(R.id.drawer)).perform(DrawerActions.close());
        }
    }

    public static class FakeHomePage extends HomePage {

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            User connectedUser = new User("TestUser@PolyChef.com", "TestUser");
            connectedUser.setKey("test key");
            when(mockUserStorage.getPolyChefUser()).thenReturn(connectedUser);
            return mockUserStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            when(mockUser.getEmail()).thenReturn("test@epfl.ch");
            when(mockUser.getDisplayName()).thenReturn("TestUsername");
            return mockUser;
        }
    }
}

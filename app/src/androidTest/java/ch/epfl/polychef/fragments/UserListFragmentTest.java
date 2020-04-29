package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(JUnit4.class)
public class UserListFragmentTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    private FakeUserListFragment fakeUserListFragment;

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Mock
    UserStorage mockUserStorage;

    @Mock
    User mockPolyChefUser;

    private User mainUserTest = new User("fake@email.com", "Fake");

    @Before
    public void initMockAndStorage() {
        MockitoAnnotations.initMocks(this);
        when(mockUserStorage.getAuthenticatedUser()).thenReturn(mock(FirebaseUser.class));
        when(mockUserStorage.getPolyChefUser()).thenReturn(mockPolyChefUser);
    }

    private synchronized void setup(Function<User, List<String>> userListFunction, int fragmentId) {
        intentsTestRule.launchActivity(new Intent());
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_host_fragment);
        fakeUserListFragment = new FakeUserListFragment(userListFunction, fragmentId);
        Fragment fragment = fakeUserListFragment;
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.onlineMiniaturesFragment, fragment).addToBackStack(null);
        transaction.commit();
        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private User getUser(String email, String name) {
        return new User(email, name);
    }

    private void getUserFromStorageWhen(User user) {
        doAnswer((call) -> {
            CallHandler<User> ch = call.getArgument(1);
            ch.onSuccess(user);
            return null;
        }).when(mockUserStorage).getUserByEmail(eq(user.getEmail()), any(CallHandler.class));
    }

    private List<String> getUserList() {
        List<String> users = new ArrayList<>();
        users.add(mainUserTest.getEmail());
        getUserFromStorageWhen(mainUserTest);
        users.add("fake1@email.com");
        getUserFromStorageWhen(getUser("fake1@email.com", "Fake1"));
        users.add("fake2@email.com");
        getUserFromStorageWhen(getUser("fake2@email.com", "Fake2"));
        return users;
    }

    @Test
    public void emptyListShowNothing() {
        setup((user) -> new ArrayList<>(), R.layout.fragment_subscriptions);
        assertThat(fakeUserListFragment.getRecyclerView().getAdapter().getItemCount(), is(0));
    }

    @Test
    public void multipleUserCanBeShown() {
        setup((user) -> getUserList(), R.layout.fragment_subscriptions);
        assertThat(fakeUserListFragment.getRecyclerView().getAdapter().getItemCount(), is(3));
    }

    @Test
    public void onClickGoesToUserProfile() {
        setup((user) -> getUserList(), R.layout.fragment_subscriptions);
        onView(withId(R.id.miniatureUserList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.UsernameDisplay)).check(matches(isDisplayed()));
        onView(withId(R.id.UsernameDisplay)).check(matches(withText("Fake")));
    }

    @Test
    public void canSubscribeAndUnsubscribe() {
        setup((user) -> getUserList(), R.layout.fragment_subscriptions);
        onView(withId(R.id.miniatureUserList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.subscribeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.subscribeButton)).check(matches(withText("subscribe")));
        onView(withId(R.id.subscribeButton)).perform(click());
        onView(withId(R.id.subscribeButton)).check(matches(withText("subscribed")));
        onView(withId(R.id.subscribeButton)).perform(click());
        onView(withId(R.id.subscribeButton)).check(matches(withText("subscribe")));
        verify(mockUserStorage, times(2)).updateUserInfo();
        verify(mockUserStorage, times(2)).updateUserInfo(eq(mainUserTest));
    }

    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return mock(FirebaseUser.class);
        }


        @Override
        public UserStorage getUserStorage(){
            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            return mock(RecipeStorage.class);
        }
    }

    public static class FakeUserListFragment extends UserListFragment {

        public FakeUserListFragment(Function<User, List<String>> userListFunction, int fragmentId) {
            super(userListFunction, fragmentId);
        }
    }
}

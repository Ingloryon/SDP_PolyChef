package ch.epfl.polychef.pages;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AutoConnectTest {

    private static boolean wasCalled = false;
    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new AutoConnectTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            true);

    @Test
    public void testAutoConnect() {
        assertTrue(wasCalled);
    }

    public class FakeEntryPage extends EntryPage {
        @Override
        public FirebaseAuth getFireBaseAuth() {
            FirebaseAuth mockFireBaseAuth = mock(FirebaseAuth.class);
            when(mockFireBaseAuth.getCurrentUser()).thenReturn(mock(FirebaseUser.class));
            return mockFireBaseAuth;
        }

        @Override
        public UserStorage getUserStorage() {
            UserStorage mockUserStorage = mock(UserStorage.class);
            doAnswer(invocation -> {
                wasCalled = true;

                //Espresso is unable to test progress bars because of animations
//                onView(withId(R.id.autoLoginProgress)).check(matches(isDisplayed()));
//                onView(withId(R.id.autoLoginBackground)).check(matches(isDisplayed()));

                CallHandler<User> caller = invocation.getArgument(0);
                caller.onSuccess((mock(User.class)));

                return null;
            }).when(mockUserStorage).initializeUserFromAuthenticatedUser(any(CallHandler.class));

            return mockUserStorage;
        }

        @Override
        public void startNextActivity() {
//            onView(withId(R.id.autoLoginProgress)).check(matches(not(isDisplayed())));
//            onView(withId(R.id.autoLoginBackground)).check(matches(not(isDisplayed())));
        }
    }
}

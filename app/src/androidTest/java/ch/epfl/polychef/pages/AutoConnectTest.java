package ch.epfl.polychef.pages;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

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

                CallHandler<User> caller = invocation.getArgument(0);
                caller.onSuccess((mock(User.class)));

                return null;
            }).when(mockUserStorage).initializeUserFromAuthenticatedUser(any(CallHandler.class));

            return mockUserStorage;
        }

        @Override
        public void startNextActivity() {

        }
    }
}

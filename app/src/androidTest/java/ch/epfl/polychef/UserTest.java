package ch.epfl.polychef;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.users.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Mock
    FirebaseUser mockFirebaseUser;

    @Mock
    FirebaseDatabase mockDatabase;

    @Mock
    DatabaseReference mockReference;

    @Mock
    Query mockQuery;

    @Mock
    DataSnapshot mockSnapshot;

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new UserTest.FakeHomePage();
            return activity;
        }
    };

    @Before
    public void setupMock(){
        MockitoAnnotations.initMocks(this);

        when(mockFirebaseUser.getEmail()).thenReturn("testUser@epfl.ch");
        when(mockDatabase.getReference(any(String.class))).thenReturn(mockReference);
        when(mockReference.orderByChild(any(String.class))).thenReturn(mockReference);
        when(mockReference.equalTo(any(String.class))).thenReturn(mockQuery);

        Mockito.doAnswer(
                (invocation) -> {
                    ValueEventListener listener =  invocation.getArgument(0);
                    listener.onDataChange(mockSnapshot);
                    return null;
                }
        ).when(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));

    }

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Test
    public void createNewUserWhenNewUserLogsIn() {

        //Return no user from the database => should create a new user
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 0);

        //Launch HomePage
        intentsTestRule.launchActivity(new Intent());
        //Check the user field has the right values
        //logout
        //Check user is updated and sent back to the database
    }

    @Test
    public void retrieveUserInfoWhenOldUserLogsIn() {
        //Return 1 user from the database => retrieve all his data
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 1);

        //Launch HomePage
        intentsTestRule.launchActivity(new Intent());
        //Check the user field has the right values
        //logout
        //Check user is updated and sent back to the database
    }

    @Test
    public void throwExceptionWhenMultipleUsersExist() {

        //Return multiple users => error
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 2);

        //Launch HomePage
        Assertions.assertThrows(IllegalStateException.class, () -> intentsTestRule.launchActivity(new Intent()));
    }

    @Test
    public void gettersWorkOnNewUser() {

        String email = "Alice@epfl.ch";
        String username = "Alice";
        List<String> emptyLst = new ArrayList<>();
        User alice = new User(email, username);

        Assertions.assertEquals(email, alice.getEmail());
        Assertions.assertEquals(username, alice.getUsername());
        Assertions.assertEquals(emptyLst, alice.getRecipes());
        Assertions.assertEquals(emptyLst, alice.getFavourites());
        Assertions.assertEquals(emptyLst, alice.getSubscribers());
        Assertions.assertEquals(emptyLst, alice.getSubscriptions());
    }

    @Test
    public void gettersWorkOnEmptyUser() {
        User noOne = new User();

        List<String> emptyLst = new ArrayList<>();

        Assertions.assertNull(noOne.getEmail());
        Assertions.assertNull(noOne.getUsername());
        Assertions.assertEquals(emptyLst, noOne.getRecipes());
        Assertions.assertEquals(emptyLst, noOne.getFavourites());
        Assertions.assertEquals(emptyLst, noOne.getSubscribers());
        Assertions.assertEquals(emptyLst, noOne.getSubscriptions());
    }

    @Test
    public void canAddFavouriteRecipes() {
        String email = "Alice@epfl.ch";
        String username = "Alice";
        User alice = new User(email, username);

        String recipe1 = "Recipe_1";
        String recipe2 = "Recipe_2";
        String recipe3 = "Recipe_3";
        String recipe4 = "Recipe_4";

        alice.addFavourite(recipe1);
        Assertions.assertEquals(alice.getFavourites().size(), 1);
        Assertions.assertTrue(alice.getFavourites().contains(recipe1));

        alice.addFavourite(recipe2);
        Assertions.assertEquals(alice.getFavourites().size(), 2);
        Assertions.assertTrue(alice.getFavourites().contains(recipe1));
        Assertions.assertTrue(alice.getFavourites().contains(recipe2));

        alice.addFavourite(recipe3);
        Assertions.assertEquals(alice.getFavourites().size(), 3);
        Assertions.assertTrue(alice.getFavourites().contains(recipe1));
        Assertions.assertTrue(alice.getFavourites().contains(recipe2));
        Assertions.assertTrue(alice.getFavourites().contains(recipe3));

        alice.addFavourite(recipe4);
        Assertions.assertEquals(alice.getFavourites().size(), 4);
        Assertions.assertTrue(alice.getFavourites().contains(recipe1));
        Assertions.assertTrue(alice.getFavourites().contains(recipe2));
        Assertions.assertTrue(alice.getFavourites().contains(recipe3));
        Assertions.assertTrue(alice.getFavourites().contains(recipe4));
    }

    @Test
    public void canAddSubscribers() {
        String email = "Alice@epfl.ch";
        String username = "Alice";
        User alice = new User(email, username);

        String subscriber1 = "Subscriber_1";
        String subscriber2 = "Subscriber_2";
        String subscriber3 = "Subscriber_3";
        String subscriber4 = "Subscriber_4";

        alice.addFavourite(subscriber1);
        Assertions.assertEquals(alice.getSubscribers().size(), 1);
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber1));

        alice.addFavourite(subscriber2);
        Assertions.assertEquals(alice.getSubscribers().size(), 2);
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber1));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber2));

        alice.addFavourite(subscriber3);
        Assertions.assertEquals(alice.getSubscribers().size(), 3);
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber1));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber2));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber3));

        alice.addFavourite(subscriber4);
        Assertions.assertEquals(alice.getSubscribers().size(), 4);
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber1));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber2));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber3));
        Assertions.assertTrue(alice.getSubscribers().contains(subscriber4));
    }

    @Test
    public void canAddSubscriptions() {
        String email = "Alice@epfl.ch";
        String username = "Alice";
        User alice = new User(email, username);

        String subscription1 = "Subscription_1";
        String subscription2 = "Subscription_2";
        String subscription3 = "Subscription_3";
        String subscription4 = "Subscription_4";

        alice.addFavourite(subscription1);
        Assertions.assertEquals(alice.getSubscriptions().size(), 1);
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription1));

        alice.addFavourite(subscription2);
        Assertions.assertEquals(alice.getSubscriptions().size(), 2);
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription1));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription2));

        alice.addFavourite(subscription3);
        Assertions.assertEquals(alice.getSubscriptions().size(), 3);
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription1));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription2));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription3));

        alice.addFavourite(subscription4);
        Assertions.assertEquals(alice.getSubscriptions().size(), 4);
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription1));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription2));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription3));
        Assertions.assertTrue(alice.getSubscriptions().contains(subscription4));
    }

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return mockFirebaseUser;
        }

        @Override
        public FirebaseDatabase getDatabase() {
            return mockDatabase;
        }
    }
}

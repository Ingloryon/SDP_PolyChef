package ch.epfl.polychef;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.users.User;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    @Mock
    FirebaseUser mockFirebaseUser;

    String mockUserEmail;
    String mockUserName;

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
    public void setMock(){
        MockitoAnnotations.initMocks(this);

        mockUserEmail = "testUser@epfl.ch";
        mockUserName = "Alice InWonderland";

        when(mockFirebaseUser.getEmail()).thenReturn(mockUserEmail);
        when(mockFirebaseUser.getDisplayName()).thenReturn(mockUserName);

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
    public ActivityTestRule<HomePage> homePageActivityTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Test
    public void createNewUserWhenNewUserLogsIn() {

        //Return no user from the database => should create a new user
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 0);

        String userKey = "Random key";
        when(mockReference.getKey()).thenReturn(userKey);

        //Check that the user is put in the right location on the database
        when(mockDatabase.getReference(any(String.class))).thenAnswer((call) -> {
            String arg = call.getArgument(0);

            if(!arg.equals("users")) {
                assertEquals("users/" + userKey, arg);
            }

            return mockReference;
        });

        //Check the user sent to the database has the correct data
        when(mockReference.setValue(any(User.class))).then((call) -> {
            User userSentBack = call.getArgument(0);

            Assertions.assertNotNull(userSentBack);
            assertEquals(mockUserEmail, userSentBack.getEmail());
            assertEquals(mockUserName, userSentBack.getUsername());

            assertEquals(0, userSentBack.getRecipes().size());
            assertEquals(0, userSentBack.getFavourites().size());
            assertEquals(0, userSentBack.getSubscribers().size());
            assertEquals(0, userSentBack.getSubscriptions().size());

            return null;
        });

        //Launch HomePage
        homePageActivityTestRule.launchActivity(new Intent());

        //logout
        Espresso.onView(withId(R.id.logButton)).perform(click());
    }

    @Test
    public void retrieveUserInfoWhenOldUserLogsIn() {

        //Mock User
        User alice = new User(mockUserEmail, mockUserName);
        String fav1 = "First favourite recipe";
        alice.addFavourite(fav1);

        String fav2 = "Second favourite recipe";
        alice.addFavourite(fav2);

        String subscription = "Only one subscription";
        alice.addSubscriptions(subscription);

        String userKey = "Random key";

        //Return 1 user from the database => retrieve all his data
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 1);

        List<DataSnapshot> children = new ArrayList<>(1);
        children.add(mockSnapshot);
        when(mockSnapshot.getChildren()).thenReturn(children);
        when(mockSnapshot.getValue(User.class)).thenReturn(alice);

        when(mockSnapshot.getKey()).thenReturn(userKey);


        when(mockDatabase.getReference(any(String.class))).thenAnswer((call) -> {
            String arg = call.getArgument(0);
            if(!arg.equals("users")){
                assertEquals("users/" + userKey, arg);
            }
            return mockReference;
        });

        //Check the user sent to the database has the correct data
        when(mockReference.setValue(any(User.class))).then((call) -> {
            User userSentBack = call.getArgument(0);

            Assertions.assertNotNull(userSentBack);
            assertEquals(mockUserEmail, userSentBack.getEmail());
            assertEquals(mockUserName, userSentBack.getUsername());

            assertEquals(0, userSentBack.getRecipes().size());

            assertEquals(2, userSentBack.getFavourites().size());
            assertTrue(userSentBack.getFavourites().contains(fav1));
            assertTrue(userSentBack.getFavourites().contains(fav2));

            assertEquals(0, userSentBack.getSubscribers().size());

            assertEquals(1, userSentBack.getSubscriptions().size());
            assertTrue(userSentBack.getSubscriptions().contains(subscription));

            return null;
        });

        //Launch HomePage
        homePageActivityTestRule.launchActivity(new Intent());

        //logout
        Espresso.onView(withId(R.id.logButton)).perform(click());
    }

    @Test
    public void throwExceptionWhenMultipleUsersExist() {

        //Return multiple users => error
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 2);

        when(mockDatabase.getReference(any(String.class))).thenReturn(mockReference);

        Mockito.doAnswer(
                (call) -> {
                    ValueEventListener listener =  call.getArgument(0);
                    listener.onDataChange(mockSnapshot);
                    return null;
                }
        ).when(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //Launch HomePage
        Assertions.assertThrows(IllegalStateException.class, () -> homePageActivityTestRule.launchActivity(new Intent()));
    }

    @Test
    public void throwExceptionWhenQueryIsCanceled() {

        //Return multiple users => error
        when(mockSnapshot.getChildrenCount()).thenReturn((long) 2);

        when(mockDatabase.getReference(any(String.class))).thenReturn(mockReference);

        Mockito.doAnswer(
                (call) -> {
                    ValueEventListener listener =  call.getArgument(0);
                    listener.onCancelled(DatabaseError.fromException(new Exception()));
                    return null;
                }
        ).when(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //Launch HomePage
        Assertions.assertThrows(IllegalArgumentException.class, () -> homePageActivityTestRule.launchActivity(new Intent()));
    }

    @Test
    public void gettersWorkOnNewUser() {

        String email = "Alice@epfl.ch";
        String username = "Alice";
        User alice = new User(email, username);

        assertEquals(email, alice.getEmail());
        assertEquals(username, alice.getUsername());
        assertEquals(0, alice.getRecipes().size());
        assertEquals(0, alice.getFavourites().size());
        assertEquals(0, alice.getSubscribers().size());
        assertEquals(0, alice.getSubscriptions().size());
    }

    @Test
    public void gettersWorkOnEmptyUser() {
        User noOne = new User();

        Assertions.assertNull(noOne.getEmail());
        Assertions.assertNull(noOne.getUsername());
        assertEquals(0, noOne.getRecipes().size());
        assertEquals(0, noOne.getFavourites().size());
        assertEquals(0, noOne.getSubscribers().size());
        assertEquals(0, noOne.getSubscriptions().size());
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
        assertEquals(alice.getFavourites().size(), 1);
        assertTrue(alice.getFavourites().contains(recipe1));

        alice.addFavourite(recipe2);
        assertEquals(alice.getFavourites().size(), 2);
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));

        alice.addFavourite(recipe3);
        assertEquals(alice.getFavourites().size(), 3);
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));

        alice.addFavourite(recipe4);
        assertEquals(alice.getFavourites().size(), 4);
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));
        assertTrue(alice.getFavourites().contains(recipe4));
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
        assertEquals(alice.getSubscribers().size(), 1);
        assertTrue(alice.getSubscribers().contains(subscriber1));

        alice.addFavourite(subscriber2);
        assertEquals(alice.getSubscribers().size(), 2);
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));

        alice.addFavourite(subscriber3);
        assertEquals(alice.getSubscribers().size(), 3);
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));

        alice.addFavourite(subscriber4);
        assertEquals(alice.getSubscribers().size(), 4);
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));
        assertTrue(alice.getSubscribers().contains(subscriber4));
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
        assertEquals(alice.getSubscriptions().size(), 1);
        assertTrue(alice.getSubscriptions().contains(subscription1));

        alice.addFavourite(subscription2);
        assertEquals(alice.getSubscriptions().size(), 2);
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));

        alice.addFavourite(subscription3);
        assertEquals(alice.getSubscriptions().size(), 3);
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));

        alice.addFavourite(subscription4);
        assertEquals(alice.getSubscriptions().size(), 4);
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));
        assertTrue(alice.getSubscriptions().contains(subscription4));
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

        @Override
        protected String getUserEmail() { return "test@epfl.ch"; }
    }
}

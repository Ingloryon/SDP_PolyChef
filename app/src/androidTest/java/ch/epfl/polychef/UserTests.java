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
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class UserTests {

    private FirebaseDatabase mockDatabase;
    private DatabaseReference mockUsersRef;

    private Query mockOrderByEmail;
    private Query mockEqualToEmail;

    private DataSnapshot mockOnDataChangeSnapshot;

    private String mockUserEmail = "testUser@epfl.ch";
    private String mockUserName = "Alice InWonderland";
    private String mockUserKey = "UserKey";

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new UserTests.FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return mock(FirebaseUser.class);
        }

        @Override
        public FirebaseDatabase getDatabase() {
            return mockDatabase;
        }

        @Override
        protected String getUserEmail() { return mockUserEmail; }

        @Override
        protected String getUserName() { return mockUserName; }
    }

    @Before
    public void mockInit(){
        mockDatabase = mock(FirebaseDatabase.class);
        mockUsersRef = mock(DatabaseReference.class);
        mockOnDataChangeSnapshot = mock(DataSnapshot.class);
        mockOrderByEmail = mock(Query.class);
        mockEqualToEmail = mock(Query.class);

        when(mockDatabase.getReference("users")).thenAnswer(
                (call) -> {
                    return mockUsersRef;
                }
        );

        when(mockUsersRef.orderByChild("email")).thenReturn(mockOrderByEmail);

        when(mockOrderByEmail.equalTo(any(String.class))).thenReturn(mockEqualToEmail);

        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);
            listener.onDataChange(mockOnDataChangeSnapshot);
            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    public void assertNewUser(User user) {
        assertNotNull(user);
        assertEquals(mockUserEmail, user.getEmail());
        assertEquals(mockUserName, user.getUsername());

        assertEquals(0, user.getRecipes().size());
        assertEquals(0, user.getFavourites().size());
        assertEquals(0, user.getSubscribers().size());
        assertEquals(0, user.getSubscriptions().size());
    }

    @Test
    public void newUserTest() {

        DatabaseReference mockNewUserRef = mock(DatabaseReference.class);
        DatabaseReference mockTempRef = mock(DatabaseReference.class);

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 0);    //TODO number of children depends on test

        when(mockUsersRef.push()).thenReturn(mockTempRef);

        when(mockTempRef.setValue(any(User.class))).thenAnswer((call) -> {

            User userSentBack = call.getArgument(0);

            assertNewUser(userSentBack);

            return null;
        });

        when(mockTempRef.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockNewUserRef);

        when(mockNewUserRef.setValue(any(User.class))).thenAnswer((call) -> {
            User userSentBack = call.getArgument(0);

            assertNewUser(userSentBack);

            return null;
        });
    }

    @Test
    public void oldUserTest(){
        DatabaseReference mockOldUserRef = mock(DatabaseReference.class);
        DataSnapshot mockSnapshotChild = mock(DataSnapshot.class);

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 1);

        //OnDataChange mock requirements
        List<DataSnapshot> children = new ArrayList<>(1);
        children.add(mockSnapshotChild);
        when(mockOnDataChangeSnapshot.getChildren()).thenReturn(children);

        //oldUser method mock requirements
        User mockUser = new User(mockUserEmail, mockUserName);
        String fav1 = "First favourite recipe";
        mockUser.addFavourite(fav1);
        String fav2 = "Second favourite recipe";
        mockUser.addFavourite(fav2);
        String subscription = "Only one subscription";
        mockUser.addSubscriptions(subscription);

        when(mockSnapshotChild.exists()).thenReturn(true);
        when(mockSnapshotChild.getValue(User.class)).thenReturn(mockUser);
        when(mockSnapshotChild.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockOldUserRef);

        when(mockOldUserRef.setValue(any(User.class))).thenAnswer((call) -> {
            User userSentBack = call.getArgument(0);

            assertNotNull(userSentBack);
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
    }

    @Test
    public void gettersWorkOnNewUser() {

        String email = mockUserEmail;
        String username = mockUserName;
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

        assertNull(noOne.getEmail());
        assertNull(noOne.getUsername());
        assertEquals(0, noOne.getRecipes().size());
        assertEquals(0, noOne.getFavourites().size());
        assertEquals(0, noOne.getSubscribers().size());
        assertEquals(0, noOne.getSubscriptions().size());
    }

    @Test
    public void canAddFavouriteRecipes() {
        String email = mockUserEmail;
        String username = mockUserName;
        User alice = new User(email, username);

        String recipe1 = "Recipe_1";
        String recipe2 = "Recipe_2";
        String recipe3 = "Recipe_3";
        String recipe4 = "Recipe_4";

        alice.addFavourite(recipe1);
        assertEquals(1, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));

        alice.addFavourite(recipe2);
        assertEquals(2, alice.getFavourites().size());;
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));

        alice.addFavourite(recipe3);
        assertEquals(3, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));

        alice.addFavourite(recipe4);
        assertEquals(4, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));
        assertTrue(alice.getFavourites().contains(recipe4));
    }

    @Test
    public void canAddSubscribers() {
        String email = mockUserEmail;
        String username = mockUserName;
        User alice = new User(email, username);

        String subscriber1 = "Subscriber_1";
        String subscriber2 = "Subscriber_2";
        String subscriber3 = "Subscriber_3";
        String subscriber4 = "Subscriber_4";

        alice.addSubscriber(subscriber1);
        assertEquals(1, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));

        alice.addSubscriber(subscriber2);
        assertEquals(2, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));

        alice.addSubscriber(subscriber3);
        assertEquals(3, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));

        alice.addSubscriber(subscriber4);
        assertEquals(4, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));
        assertTrue(alice.getSubscribers().contains(subscriber4));
    }

    @Test
    public void canAddSubscriptions() {
        String email = mockUserEmail;
        String username = mockUserName;
        User alice = new User(email, username);

        String subscription1 = "Subscription_1";
        String subscription2 = "Subscription_2";
        String subscription3 = "Subscription_3";
        String subscription4 = "Subscription_4";

        alice.addSubscriptions(subscription1);
        assertEquals(1, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));

        alice.addSubscriptions(subscription2);
        assertEquals(2, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));

        alice.addSubscriptions(subscription3);
        assertEquals(3, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));

        alice.addSubscriptions(subscription4);
        assertEquals(4, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));
        assertTrue(alice.getSubscriptions().contains(subscription4));
    }
}


























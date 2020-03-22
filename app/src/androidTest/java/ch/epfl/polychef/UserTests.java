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
            //mockInit();
            //newUserTest();
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

    @Test
    public void newUserTest() {

        DatabaseReference mockNewUserRef = mock(DatabaseReference.class);
        DatabaseReference mockTempRef = mock(DatabaseReference.class);

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 0);    //TODO number of children depends on test

        when(mockUsersRef.push()).thenReturn(mockTempRef);

        when(mockTempRef.setValue(any(User.class))).thenAnswer((call) -> {

            User userSentBack = call.getArgument(0);

            assertNotNull(userSentBack);
            assertEquals(mockUserEmail, userSentBack.getEmail());
            assertEquals(mockUserName, userSentBack.getUsername());

            assertEquals(0, userSentBack.getRecipes().size());
            assertEquals(0, userSentBack.getFavourites().size());
            assertEquals(0, userSentBack.getSubscribers().size());
            assertEquals(0, userSentBack.getSubscriptions().size());

            return null;
        });

        when(mockTempRef.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockNewUserRef);

        when(mockNewUserRef.setValue(any(User.class))).thenAnswer((call) -> {
            User userSentBack = call.getArgument(0);

            assertNotNull(userSentBack);
            assertEquals(mockUserEmail, userSentBack.getEmail());
            assertEquals(mockUserName, userSentBack.getUsername());

            assertEquals(0, userSentBack.getRecipes().size());
            assertEquals(0, userSentBack.getFavourites().size());
            assertEquals(0, userSentBack.getSubscribers().size());
            assertEquals(0, userSentBack.getSubscriptions().size());

            return null;
        });
    }

    @Test
    public void testOldUser(){
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
}


























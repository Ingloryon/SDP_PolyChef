package ch.epfl.polychef;

import android.content.Intent;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class HomePageUserTest {

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
            HomePage activity = new HomePageUserTest.FakeHomePage();
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
        protected String getUserEmail() {
            return mockUserEmail;
        }

        @Override
        protected String getUserName() {
            return mockUserName;
        }
    }

    @Before
    public void sharedMockInit(){
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
    }

    @AfterEach
    public void launchActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    public void onDataChangeCallBack(){
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

        onDataChangeCallBack();

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 0);

        DatabaseReference mockTempRef = mock(DatabaseReference.class);

        when(mockUsersRef.push()).thenReturn(mockTempRef);

        when(mockTempRef.setValue(any(User.class))).thenAnswer((call) -> {

            User userSentBack = call.getArgument(0);

            assertNewUser(userSentBack);

            return null;
        });

        when(mockTempRef.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        DatabaseReference mockNewUserRef = mock(DatabaseReference.class);

        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockNewUserRef);

        when(mockNewUserRef.setValue(any(User.class))).thenAnswer((call) -> {
            User userSentBack = call.getArgument(0);

            assertNewUser(userSentBack);

            return null;
        });
    }

    @Test
    public void oldUserTest(){

        onDataChangeCallBack();

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 1);

        //OnDataChange mock requirements
        DataSnapshot mockSnapshotChild = mock(DataSnapshot.class);

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
        DatabaseReference mockOldUserRef = mock(DatabaseReference.class);

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
    public void throwsExceptionWhenMultipleUsersExist() {

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 2);

        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);

            assertThrows(IllegalStateException.class, () -> listener.onDataChange(mockOnDataChangeSnapshot));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    public void testJsonErrorThrowsException(){

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 1);

        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);

            assertThrows(IllegalArgumentException.class, () -> listener.onDataChange(mockOnDataChangeSnapshot));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //OnDataChange mock requirements
        DataSnapshot mockSnapshotChild = mock(DataSnapshot.class);

        List<DataSnapshot> children = new ArrayList<>(1);
        children.add(mockSnapshotChild);
        when(mockOnDataChangeSnapshot.getChildren()).thenReturn(children);

        //oldUser method mock requirements
        when(mockSnapshotChild.exists()).thenReturn(false);
    }

    @Test
    public void throwsExceptionWhenQueryCancelled() {
        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);

            assertThrows(IllegalStateException.class, () -> listener.onCancelled(DatabaseError.fromException(new Exception())));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }
}


























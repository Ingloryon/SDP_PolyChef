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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.pages.HomePage;
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
    private FirebaseUser mockFirebaseUser;
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
            return mockFirebaseUser;
        }

        @Override
        public FirebaseDatabase getDatabase() {
            return mockDatabase;
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

        mockFirebaseUser = mock(FirebaseUser.class);

        when(mockFirebaseUser.getEmail()).thenReturn(mockUserEmail);
        when(mockFirebaseUser.getDisplayName()).thenReturn(mockUserName);
    }

    @After
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

    public User mockUser() {
        User mockUser = new User(mockUserEmail, mockUserName);

        String fav1 = "First favourite recipe";
        mockUser.addFavourite(fav1);

        String fav2 = "Second favourite recipe";
        mockUser.addFavourite(fav2);

        String subscription = "Only one subscription";
        mockUser.addSubscription(subscription);

        return mockUser;
    }

    public void assertSendingBackCorrectUser(DatabaseReference ref, User expected){
        when(ref.setValue(any(User.class))).thenAnswer((call) -> {
            assertEquals(expected, call.getArgument(0));
            return null;
        });
    }

    @Test
    public void newUserTest() {

        onDataChangeCallBack();

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 0);

        DatabaseReference mockTempRef = mock(DatabaseReference.class);

        when(mockUsersRef.push()).thenReturn(mockTempRef);

        assertSendingBackCorrectUser(mockUsersRef, new User(mockUserEmail, mockUserName));

        when(mockTempRef.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        DatabaseReference mockNewUserRef = mock(DatabaseReference.class);

        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockNewUserRef);

        assertSendingBackCorrectUser(mockNewUserRef, new User(mockUserEmail, mockUserName));
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
        User mockUser = mockUser();

        when(mockSnapshotChild.exists()).thenReturn(true);
        when(mockSnapshotChild.getValue(User.class)).thenReturn(mockUser);
        when(mockSnapshotChild.getKey()).thenReturn(mockUserKey);

        //updateUser method mock requirements
        DatabaseReference mockOldUserRef = mock(DatabaseReference.class);

        when(mockDatabase.getReference("users/" + mockUserKey)).thenReturn(mockOldUserRef);

        assertSendingBackCorrectUser(mockUsersRef, mockUser);
    }

    public void assertOnDataChangeThrowsException(Exception exception){
        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);

            assertThrows(exception.getClass(), () -> listener.onDataChange(mockOnDataChangeSnapshot));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    public void throwsExceptionWhenMultipleUsersExist() {

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 2);

        assertOnDataChangeThrowsException(new IllegalStateException());
    }

    @Test
    public void testJsonErrorThrowsException(){

        when(mockOnDataChangeSnapshot.getChildrenCount()).thenReturn((long) 1);

        assertOnDataChangeThrowsException(new IllegalArgumentException());

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

            assertThrows(IllegalArgumentException.class, () -> listener.onCancelled(DatabaseError.fromException(new Exception())));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }
}

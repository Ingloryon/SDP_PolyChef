package ch.epfl.polychef.users;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.CallHandler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class UserStorageTest {

    private FirebaseDatabase mockDatabase;
    private FirebaseUser mockFirebaseUser;
    private DatabaseReference mockUsersRef;

    private Query mockOrderByEmail;
    private Query mockEqualToEmail;

    private DataSnapshot mockOnDataChangeSnapshot;

    private String mockUserEmail = "testUser@epfl.ch";
    private String mockUserName = "Alice InWonderland";
    private String mockUserKey = "UserKey";

    private UserStorage fakeUserStorage = Mockito.mock(UserStorage.class,CALLS_REAL_METHODS );

    @Mock
    private Query query;

    @Mock
    private DataSnapshot dataSnapshot;

    @Before
    public void initMockFirebaseDatabase() {
        MockitoAnnotations.initMocks(this);
        mockDatabase = mock(FirebaseDatabase.class);
        mockUsersRef = mock(DatabaseReference.class);
        mockOnDataChangeSnapshot = mock(DataSnapshot.class);
        mockOrderByEmail = mock(Query.class);
        mockEqualToEmail = mock(Query.class);
        mockFirebaseUser = mock(FirebaseUser.class);

        when(mockDatabase.getReference(UserStorage.DB_NAME)).thenAnswer(
                (call) -> {
                    return mockUsersRef;
                }
        );

        when(mockUsersRef.orderByChild("email")).thenReturn(mockOrderByEmail);

        when(mockOrderByEmail.equalTo(any(String.class))).thenReturn(mockEqualToEmail);


        when(mockFirebaseUser.getEmail()).thenReturn(mockUserEmail);
        when(mockFirebaseUser.getDisplayName()).thenReturn(mockUserName);

        when(fakeUserStorage.getDatabase()).thenReturn(mockDatabase);
        when(fakeUserStorage.getAuthenticatedUser()).thenReturn(mockFirebaseUser);
    }


    @After
    public void initializeFakeUserStorage() {
        fakeUserStorage.initializeUserFromAuthenticatedUser(mock(CallHandler.class));
    }

    @Test
    public void testMethodGetInstanceExist(){
        UserStorage.getInstance();
    }

    @Test
    public void testUpdateUserInfoThrowExceptionWhenNotInitialized(){

        assertThrows(IllegalStateException.class, () -> fakeUserStorage.updateUserInfo());
    }

    /*@Test
    public void testUpdateUserInfo(){

        when(mockDatabase.getReference(UserStorage.DB_NAME + "/" + mockUserKey)).thenReturn(mockNewUserRef);

    }*/

    /*public void updateUserInfo() {
        if (user != null && userKey != null) {
            getDatabase()
                    .getReference(UserStorage.DB_NAME + "/" + userKey)
                    .setValue(user);
        } else {
            throw new IllegalStateException("The user have not been initialized");
        }
    }*/

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

        when(mockDatabase.getReference(UserStorage.DB_NAME + "/" + mockUserKey)).thenReturn(mockNewUserRef);

        assertSendingBackCorrectUser(mockNewUserRef, new User(mockUserEmail, mockUserName));

        fakeUserStorage.initializeUserFromAuthenticatedUser(mock(CallHandler.class));
        //Need to launch updateUserInfo after initialization
        fakeUserStorage.updateUserInfo();
        //the initialization in the @After is superfluous
        doNothing().when(fakeUserStorage).initializeUserFromAuthenticatedUser(any(CallHandler.class));
    }


    @Test
    public void existingUserTest(){

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

        when(mockDatabase.getReference(UserStorage.DB_NAME + "/" + mockUserKey)).thenReturn(mockOldUserRef);

        assertSendingBackCorrectUser(mockUsersRef, mockUser);
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

    @Test
    public void canUpdateUserByEmail() {
        User user = new User();
        user.setKey("TEST");
        DatabaseReference mockUserRef = mock(DatabaseReference.class);
        when(mockDatabase.getReference(eq(UserStorage.DB_NAME + "/TEST"))).thenReturn(mockUserRef);
        fakeUserStorage.updateUserInfo(user);
        verify(mockUserRef).setValue(eq(user));
    }

    @Test
    public void nullUserThrowsIllegal() {
        assertThrows(IllegalArgumentException.class, () -> fakeUserStorage.updateUserInfo(null));
    }

    @Test
    public void canGetUserByEmail() {
        when(fakeUserStorage.getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(eq("fake@email.com"))).thenReturn(query);
        when(dataSnapshot.getChildrenCount()).thenReturn((long) 1);
        User user = new User();
        DataSnapshot dataSnapshot1 = mock(DataSnapshot.class);
        when(dataSnapshot.getChildren()).thenReturn(Collections.singletonList(dataSnapshot1));
        when(dataSnapshot1.exists()).thenReturn(true);
        when(dataSnapshot1.getValue(User.class)).thenReturn(user);
        when(dataSnapshot1.getKey()).thenReturn("TEST");
        mockResponse(true);
        fakeUserStorage.getUserByEmail("fake@email.com", new CallHandler<User>() {
            @Override
            public void onSuccess(User data) {
                assertThat(data, equalTo(user));
            }

            @Override
            public void onFailure() {
                fail("Should not fail");
            }
        });
    }

    @Test
    public void canGetUserByID() {
        DatabaseReference databaseReference = mock(DatabaseReference.class);
        when(fakeUserStorage.getDatabase().getReference(UserStorage.DB_NAME).child("userIDTest")).thenReturn(databaseReference);
        fakeUserStorage.getUserByID("userIDTest", mock(CallHandler.class));
        verify(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    public void wrongUserFailsOnGetByEmail() {
        when(fakeUserStorage.getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(eq("fake@email.com"))).thenReturn(query);
        when(dataSnapshot.getChildrenCount()).thenReturn((long) 1);
        DataSnapshot dataSnapshot1 = mock(DataSnapshot.class);
        when(dataSnapshot.getChildren()).thenReturn(Collections.singletonList(dataSnapshot1));
        when(dataSnapshot1.exists()).thenReturn(false);
        mockResponse(true);
        shouldFails();
    }

    @Test
    public void moreThanOneUserFailsEmail() {
        when(fakeUserStorage.getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(eq("fake@email.com"))).thenReturn(query);
        when(dataSnapshot.getChildrenCount()).thenReturn((long) 3);
        mockResponse(true);
        shouldFails();
    }

    @Test
    public void getEmailsCanFailsOnCancel() {
        when(fakeUserStorage.getDatabase().getReference(UserStorage.DB_NAME).orderByChild("email").equalTo(eq("fake@email.com"))).thenReturn(query);
        mockResponse(false);
        shouldFails();
    }

    private void shouldFails() {
        fakeUserStorage.getUserByEmail("fake@email.com", new CallHandler<User>() {
            @Override
            public void onSuccess(User data) {
                fail("Should fail");
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void mockResponse(boolean mockSuccess){
        doAnswer((call) -> {
            ValueEventListener listener = call.getArgument(0);

            if(mockSuccess){
                listener.onDataChange(dataSnapshot);
            } else {
                listener.onCancelled(mock(DatabaseError.class));
            }
            return null;
        }).when(query).addListenerForSingleValueEvent(any());
    }

    private void onDataChangeCallBack(){
        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);
            listener.onDataChange(mockOnDataChangeSnapshot);
            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    private void assertSendingBackCorrectUser(DatabaseReference ref, User expected){
        when(ref.setValue(any(User.class))).thenAnswer((call) -> {
            assertEquals(expected, call.getArgument(0));
            return null;
        });
    }

    private User mockUser() {
        User mockUser = new User(mockUserEmail, mockUserName);

        String fav1 = "First favourite recipe";
        mockUser.addFavourite(fav1);

        String fav2 = "Second favourite recipe";
        mockUser.addFavourite(fav2);

        String subscription = "Only one subscription";
        mockUser.addSubscription(subscription);

        return mockUser;
    }

    private void assertOnDataChangeThrowsException(Exception exception){
        doAnswer((call) -> {
            ValueEventListener listener =  call.getArgument(0);

            assertThrows(exception.getClass(), () -> listener.onDataChange(mockOnDataChangeSnapshot));

            return null;
        }).when(mockEqualToEmail).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }
}

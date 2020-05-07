package ch.epfl.polychef.users;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.mockito.Mockito;

import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.utils.SearchTest;

import static org.mockito.Mockito.when;

public class SearchUserTest extends SearchTest {

    SearchUser spyUserSearch;
    User[] users = {
            new User("test1", "123456"),
            new User("test2", "34"),
            new User("test3", "43-aBcD")};

    @Override
    public void initTests() {
        dbName = UserStorage.DB_NAME;

        super.initTests();

        spyUserSearch = Mockito.spy(SearchUser.getInstance());
        when(spyUserSearch.getDatabase()).thenReturn(mockDataBase);
    }

    @Override
    public void callSearch1(String query, CallHandler<List<Miniatures>> caller) {
        spyUserSearch.searchForUser(query, caller);
    }

    @Override
    public void callSearch2(String ingredient, CallHandler<List<Miniatures>> caller) {
        //SearchUser doesn't have a second search function
        mockDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public Class getMiniatureClass() {
        return User.class;
    }

    @Override
    public Miniatures getMiniature(int index) {
        if(0 <= index && index < 3){
            return users[index];
        }
        return new User("user_" + index, "test");
    }
}

package ch.epfl.polychef;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import ch.epfl.polychef.recipe.Recipe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireBaseTest {

    FakeFireBase fakeFireBase=new FakeFireBase();

    @Test
    public void testThatAddingARecipeWorks(){
        fakeFireBase.addRecipeToFirebase(null);

    }

    private static class FakeFireBase extends Firebase {
        static {
            firebaseInstance = mock(FirebaseDatabase.class);
            DatabaseReference fakeDatabaseReference=mock(DatabaseReference.class);
            when(fakeDatabaseReference.child(anyString())).thenReturn(fakeDatabaseReference);
            when(firebaseInstance.getReference(anyString())).thenReturn(fakeDatabaseReference);
        }
    }

}

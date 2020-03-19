package ch.epfl.polychef;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import ch.epfl.polychef.recipe.Recipe;

import static ch.epfl.polychef.Firebase.addRecipeToFirebase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireBaseTest {

    private FakeFireBase fakeFireBase=new FakeFireBase();

    @Test
    public void testThatAddingARecipeDoesNotThrowError(){
        addRecipeToFirebase(new Recipe());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatAddingANullRecipeThrowsError(){
        fakeFireBase.addRecipeToFirebase(null);
    }

    @Test
    public void testThatReadRecipeFromFirebaseDoesNotThrowError(){
        fakeFireBase.readRecipeFromFirebase(0,null);
    }

    public static class FakeFireBase extends Firebase {
        static {
            firebaseInstance = mock(FirebaseDatabase.class);
            DatabaseReference fakeDatabaseReference=mock(DatabaseReference.class);
            when(fakeDatabaseReference.child(anyString())).thenReturn(fakeDatabaseReference);
            when(firebaseInstance.getReference(anyString())).thenReturn(fakeDatabaseReference);
        }
    }

}

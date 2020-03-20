package ch.epfl.polychef;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;

//import static ch.epfl.polychef.recipe.Firebase.addRecipeToFirebase;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireBaseTest {

    private RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
    private FirebaseDatabase firebaseInstance;

    @Before
    public void initMockTest() {
        firebaseInstance = mock(FirebaseDatabase.class);
        DatabaseReference fakeDatabaseReference = mock(DatabaseReference.class);
        when(fakeDatabaseReference.child(anyString())).thenReturn(fakeDatabaseReference);
        when(firebaseInstance.getReference(anyString())).thenReturn(fakeDatabaseReference);
    }

    @Test
    public void testThatAddingARecipeDoesNotThrowError(){
        fakeRecipeStorage.addRecipe(new Recipe());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatAddingANullRecipeThrowsError(){
        fakeRecipeStorage.addRecipe(null);
    }

    @Test
    public void testThatReadRecipeFromFirebaseDoesNotThrowError(){
        CallHandler<Recipe> fireHandler=mock(CallHandler.class);
        fakeRecipeStorage.readRecipe(0,fireHandler);
    }

    public class FakeRecipeStorage extends RecipeStorage {
        @Override
        public FirebaseDatabase getFirebaseDatabase() {
            return firebaseInstance;
        }
    }

}

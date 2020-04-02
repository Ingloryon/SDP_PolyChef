package ch.epfl.polychef;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.polychef.recipe.SearchRecipe;

import static org.mockito.Mockito.when;

public class SearchRecipeTest {

    SearchRecipe mockSearchRecipe;
    FirebaseDatabase mockDataBase;
    DatabaseReference mockDatabaseReference;

    @Before
    public void initTests(){
        mockSearchRecipe= Mockito.mock(SearchRecipe.class,Mockito.CALLS_REAL_METHODS);
        mockDataBase= Mockito.mock(FirebaseDatabase.class);
        mockDatabaseReference=Mockito.mock(DatabaseReference.class);

        when(mockSearchRecipe.getDatabase()).thenReturn(mockDataBase);

        when(mockDataBase.getReference()).thenReturn(mockDatabaseReference);
    }

    @Test
    public void testSearchForRecipe(){//String query, CallHandler<List<Recipe>> caller) {

        //searchRecipe(query, this::compareName, caller);
    }
}

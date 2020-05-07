package ch.epfl.polychef.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.recipe.SearchRecipe;
import ch.epfl.polychef.users.SearchUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class SearchTest {

    protected FirebaseDatabase mockDataBase;
    protected DatabaseReference mockDatabaseReference;
    private DataSnapshot mockDataSnapshot;

    private DataSnapshot mockDataSnapshot1;
    private DataSnapshot mockDataSnapshot2;
    private DataSnapshot mockDataSnapshot3;

    private Miniatures mockMiniature1;
    private Miniatures mockMiniature2;
    private Miniatures mockMiniature3;

    protected String DB_NAME;
    abstract public Class getMiniatureClass();
    abstract public Miniatures getMiniature(int index);
    abstract public void callSearch1(String query, CallHandler<List<Miniatures>> caller);
    abstract public void callSearch2(String ingredient, CallHandler<List<Miniatures>> caller);

    private ValueEventListener givenValueEventListener;

    @Before
    public void initTests(){

        mockDataBase= Mockito.mock(FirebaseDatabase.class);
        mockDatabaseReference=Mockito.mock(DatabaseReference.class);
        mockDataSnapshot=Mockito.mock(DataSnapshot.class);

        givenValueEventListener=null;

        when(mockDataBase.getReference(DB_NAME)).thenReturn(mockDatabaseReference);

        doAnswer((call) -> {
            givenValueEventListener=  call.getArgument(0);
            return null;
        }).when(mockDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        initializeMock();
    }

    @Test
    public void methodGetInstanceExist(){
        SearchRecipe.getInstance();
        SearchUser.getInstance();
    }

    @Test
    public void testSearchForRecipeFailWithSnapshotNullValue(){

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(null,false);

        callSearch1("e",callHandlerChecker);

        when(mockDataSnapshot.getValue()).thenReturn(null);

        //trigger changes
        givenValueEventListener.onDataChange(mockDataSnapshot);

        callHandlerChecker.assertWasCalled();
    }

    @Test
    public void testSearchForRecipeFailOnCancel(){

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(null,false);

        callSearch1("e",callHandlerChecker);

        //trigger cancellation
        givenValueEventListener.onCancelled(Mockito.mock(DatabaseError.class));

        callHandlerChecker.assertWasCalled();
    }

    @Test
    public void testSearchForRecipeFindRecipeWithGivenWord(){
        List<Miniatures> expectedRecipeList=new ArrayList<>();
        expectedRecipeList.add(mockMiniature1);
        expectedRecipeList.add(mockMiniature2);
        expectedRecipeList.add(mockMiniature3);

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(expectedRecipeList ,true);

        callSearch1("3",callHandlerChecker);

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    @Test
    public void testSearchForRecipeFindNothing(){
        List<Miniatures> expectedRecipeList=new ArrayList<>();

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(expectedRecipeList ,true);

        callSearch1("8",callHandlerChecker);

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    @Test
    public void testSearchForRecipeFindOverString(){
        callSearch1("2345",expectAllMiniatures());

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    @Test
    public void testSearchForRecipeIsCaseInsensitive(){
        List<Miniatures> expected = new ArrayList<>();
        expected.add(mockMiniature3);

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(expected ,true);

        callSearch1("AbcD",callHandlerChecker);

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    @Test
    public void testSearchForIngredientIsCaseInsensitive(){

        callSearch2("moC",expectAllMiniatures());

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    @Test
    public void testSearchForIngredientFindParticularValue(){
        List<Miniatures> maListLaMailer=new ArrayList(Collections.singleton(mockMiniature2));

        CallHandlerChecker<List<Miniatures>> callHandlerChecker=new CallHandlerChecker<>(maListLaMailer ,true);

        callSearch2("ssssaltttt",callHandlerChecker);

        when(mockDataSnapshot.getValue()).thenReturn(0);

        givenValueEventListener.onDataChange(mockDataSnapshot);
    }

    private void initializeMock(){
        mockDataSnapshot1 =Mockito.mock(DataSnapshot.class);
        mockDataSnapshot2 =Mockito.mock(DataSnapshot.class);
        mockDataSnapshot3 =Mockito.mock(DataSnapshot.class);

        mockMiniature1 = getMiniature(0);

        mockMiniature2 = getMiniature(1);

        mockMiniature3 = getMiniature(2);

        when(mockDataSnapshot1.getValue(getMiniatureClass())).thenReturn(mockMiniature1);
        when(mockDataSnapshot2.getValue(getMiniatureClass())).thenReturn(mockMiniature2);
        when(mockDataSnapshot3.getValue(getMiniatureClass())).thenReturn(mockMiniature3);

        List<DataSnapshot> snapshotsList=new ArrayList<>();
        snapshotsList.add(mockDataSnapshot1);
        snapshotsList.add(mockDataSnapshot2);
        snapshotsList.add(mockDataSnapshot3);

        when(mockDataSnapshot.getChildren()).thenReturn(snapshotsList);
    }

    public CallHandlerChecker<List<Miniatures>> expectAllMiniatures(){
        List<Miniatures> list = new ArrayList<>();
        list.add(mockMiniature1);
        list.add(mockMiniature2);
        list.add(mockMiniature3);
        return new CallHandlerChecker<>(list ,true);
    }

}

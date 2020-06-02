package ch.epfl.polychef.recipe;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.utils.CallHandlerChecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeStorageTest {
    
    @Mock
    private FirebaseDatabase firebaseDatabase;
    @Mock
    private DatabaseReference databaseRecipeReference;
    @Mock
    private Query query;
    @Mock
    private DataSnapshot dataSnapshot;

    @Mock
    private DataSnapshot recipeSnapshot1;
    Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);

    @Mock
    private DataSnapshot recipeSnapshot2;
    Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);

    @Mock
    private DatabaseError databaseError;

    private RecipeStorage recipeStorage = Mockito.mock(RecipeStorage.class,CALLS_REAL_METHODS );

    private String oldDate = "2020/02/21 13:00:00";
    private String recentDate = "2020/02/23 13:00:00";
    
    private void mockResponse(boolean mockSuccess){
        doAnswer((call) -> {
            ValueEventListener listener = call.getArgument(0);
            
            if(mockSuccess){
                listener.onDataChange(dataSnapshot);
            } else {
                listener.onCancelled(databaseError);
            }
            return null;
        }).when(query).addListenerForSingleValueEvent(any());
    }

    private void initGetNRecipe(){
        when(databaseRecipeReference.orderByChild("date")).thenReturn(query);
        when(query.startAt(any(String.class))).thenReturn(query);
        when(query.endAt(any(String.class))).thenReturn(query);
        when(query.limitToFirst(any(Integer.class))).thenReturn(query);
        when(query.limitToLast(any(Integer.class))).thenReturn(query);
    }

    @Before
    public void initMockFirebaseDatabase() {
        MockitoAnnotations.initMocks(this);

        when(recipeStorage.getFirebaseDatabase()).thenReturn(firebaseDatabase);
        when(firebaseDatabase.getReference(RecipeStorage.DB_NAME)).thenReturn(databaseRecipeReference);

        when(recipeSnapshot1.getValue(Recipe.class)).thenReturn(recipe1);
        when(recipeSnapshot2.getValue(Recipe.class)).thenReturn(recipe2);
    }


    @Test
    public synchronized void getDateWorks() throws InterruptedException{
        String first = recipeStorage.getCurrentDate();
        wait(2000);
        String second = recipeStorage.getCurrentDate();
        assertTrue(first.compareTo(second) < 0);
    }

    @Test
    public synchronized void canReadARecipeFromUUid() throws InterruptedException {

        String recipeUuid = recipe1.getRecipeUuid();

        when(databaseRecipeReference.orderByChild("recipeUuid")).thenReturn(query);
        when(query.equalTo(any(String.class))).thenAnswer((call) -> {
            assertEquals(call.getArgument(0), recipeUuid);
            return query;
        });

        mockResponse(true);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 1);

        List<DataSnapshot> children = new ArrayList<>(1);
        children.add(recipeSnapshot1);
        when(dataSnapshot.getChildren()).thenReturn(children);

        when(recipeSnapshot1.getValue(Recipe.class)).thenReturn(recipe1);

        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(recipe1, true);

        recipeStorage.readRecipeFromUuid(recipeUuid, fakeCallHandler);
        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void readRecipeFromUuidFailWhenThereIsNoResult() throws InterruptedException {
        String recipeUuid = recipe1.getRecipeUuid();

        when(databaseRecipeReference.orderByChild("recipeUuid")).thenReturn(query);
        when(query.equalTo(any(String.class))).thenAnswer((call) -> {
            assertEquals(call.getArgument(0), recipeUuid);
            return query;
        });

        mockResponse(true);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 0);

        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(null, false);

        recipeStorage.readRecipeFromUuid(recipeUuid, fakeCallHandler);
        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void readRecipeFromUuidFailWhenThereIsMultipleResults() throws InterruptedException {
        String recipeUuid = recipe1.getRecipeUuid();

        when(databaseRecipeReference.orderByChild("recipeUuid")).thenReturn(query);
        when(query.equalTo(any(String.class))).thenAnswer((call) -> {
            assertEquals(call.getArgument(0), recipeUuid);
            return query;
        });

        mockResponse(true);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 2);

        List<DataSnapshot> children = new ArrayList<>(2);
        children.add(recipeSnapshot1);
        children.add(recipeSnapshot2);
        when(dataSnapshot.getChildren()).thenReturn(children);

        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(null, false);

        recipeStorage.readRecipeFromUuid(recipeUuid, fakeCallHandler);
        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void readRecipeFromUuidFailOnCancel() throws InterruptedException {
            String recipeUuid = recipe1.getRecipeUuid();

        when(databaseRecipeReference.orderByChild("recipeUuid")).thenReturn(query);
        when(query.equalTo(any(String.class))).thenAnswer((call) -> {
            assertEquals(call.getArgument(0), recipeUuid);
            return query;
        });

        mockResponse(false);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 1);

        List<DataSnapshot> children = new ArrayList<>(1);
        children.add(recipeSnapshot1);
        when(dataSnapshot.getChildren()).thenReturn(children);

        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(null, false);

        recipeStorage.readRecipeFromUuid(recipeUuid, fakeCallHandler);
        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public void nullThrowsExceptions() {
        String mockString = "mockString";
        CallHandler mockCallHandler = mock(CallHandler.class);
        RecipeStorage recipeStorage = RecipeStorage.getInstance();
        assertThrows(NullPointerException.class, () -> recipeStorage.addRecipe(null));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipeFromUuid("", mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(0, mockString, mockString, true, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(-1, mockString, mockString, true, mockCallHandler));
        assertThrows(NullPointerException.class,
                () -> recipeStorage.getNRecipes(5, null, mockString, true, mockCallHandler));
        assertThrows(NullPointerException.class,
                () -> recipeStorage.getNRecipes(5, mockString, null, true, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, mockString, mockString, true, null));


        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, recentDate, oldDate, true, mockCallHandler));
        assertThrows(NullPointerException.class, () -> recipeStorage.updateRecipe(null));
    }

    @Test
    public void canUpdateARecipe() {
        DatabaseReference databaseReference = mock(DatabaseReference.class);
        when(databaseRecipeReference.child(recipe1.getKey())).thenReturn(databaseReference);
        recipeStorage.updateRecipe(recipe1);
        verify(databaseReference).setValue(recipe1);
    }

    @Test
    public synchronized void getNRecipesWorks() throws InterruptedException{

        initGetNRecipe();

        mockResponse(true);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 2);

        ArrayList<DataSnapshot> resultSnapshots = new ArrayList<>(2);
        resultSnapshots.add(recipeSnapshot1);
        resultSnapshots.add(recipeSnapshot2);

        when(dataSnapshot.getChildren()).thenReturn(resultSnapshots);

        ArrayList<Miniatures> result = new ArrayList<>(2);
        result.add(recipe1);
        result.add(recipe2);

        CallHandlerChecker<List<Miniatures>> fakeCallHandler = new CallHandlerChecker<>(result, true);
        recipeStorage.getNRecipes(5, oldDate, recentDate, true, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void getNRecipesFailsWhenNoRecipesAreFound() throws InterruptedException{

        initGetNRecipe();

        mockResponse(true);

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 0);

        CallHandlerChecker<List<Miniatures>> fakeCallHandler = new CallHandlerChecker<>(null, false);
        recipeStorage.getNRecipes(5, oldDate, recentDate, false, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void getNRecipesFailsWhenQueryIsCanceled() throws InterruptedException{

        initGetNRecipe();

        mockResponse(false);

        CallHandlerChecker<List<Miniatures>> fakeCallHandler = new CallHandlerChecker<>(null, false);
        recipeStorage.getNRecipes(5, oldDate, recentDate, false, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }
}

package ch.epfl.polychef.recipe;

import androidx.core.util.Consumer;

import com.google.firebase.database.ChildEventListener;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
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


    private void callOnDataChange(){
        doAnswer((call) -> {
            ValueEventListener listener = call.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(query).addListenerForSingleValueEvent(any());
    }

    private void callOnCancel(){
        doAnswer((call) -> {
            ValueEventListener listener = call.getArgument(0);
            listener.onCancelled(databaseError);
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

        callOnDataChange();

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

        callOnDataChange();

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

        callOnDataChange();

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

        callOnCancel();

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
        Boolean mockBoolean = true;
        RecipeStorage recipeStorage = RecipeStorage.getInstance();
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.addRecipe(null));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipeFromUuid(null, mockCallHandler));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipeFromUuid(mockString, null));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(0, mockString, mockString, mockBoolean, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(-1, mockString, mockString, mockBoolean, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, null, mockString, mockBoolean, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, mockString, null, mockBoolean, mockCallHandler));
        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, mockString, mockString, mockBoolean, null));


        assertThrows(IllegalArgumentException.class,
                () -> recipeStorage.getNRecipes(5, recentDate, oldDate, mockBoolean, mockCallHandler));
    }

    @Test
    public synchronized void getNRecipesWorks() throws InterruptedException{

        initGetNRecipe();

        callOnDataChange();

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 2);

        ArrayList<DataSnapshot> resultSnapshots = new ArrayList<>(2);
        resultSnapshots.add(recipeSnapshot1);
        resultSnapshots.add(recipeSnapshot2);

        when(dataSnapshot.getChildren()).thenReturn(resultSnapshots);

        ArrayList<Recipe> result = new ArrayList<>(2);
        result.add(recipe1);
        result.add(recipe2);

        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(result, true);
        recipeStorage.getNRecipes(5, oldDate, recentDate, true, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void getNRecipesFailsWhenNoRecipesAreFound() throws InterruptedException{

        initGetNRecipe();

        callOnDataChange();

        when(dataSnapshot.getChildrenCount()).thenReturn((long) 0);

        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(null, false);
        recipeStorage.getNRecipes(5, oldDate, recentDate, false, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }

    @Test
    public synchronized void getNRecipesFailsWhenQueryIsCanceled() throws InterruptedException{

        initGetNRecipe();

        callOnCancel();

        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(null, false);
        recipeStorage.getNRecipes(5, oldDate, recentDate, false, fakeCallHandler);

        wait(1000);
        fakeCallHandler.assertWasCalled();
    }
//
//    @Test
//    public void canAddRecipe() {
//        prepareAsyncCallAdd((listener) -> listener.onDataChange(dataSnapshot));
//        when(dataSnapshot.getValue(Integer.class)).thenReturn(2);
//        when(databaseRecipeReference.child(Integer.toString(3))).thenReturn(databaseIdRecipeReference);
//        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        recipeStorage.addRecipe(recipe);
//        verify(databaseIdReference).setValue(3);
//        verify(databaseIdRecipeReference).setValue(recipe);
//    }
//
//    @Test
//    public void cannotAddRecipeWhenCancelled() {
//        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
//        prepareAsyncCallAdd((listener) -> listener.onCancelled(databaseError));
//        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        recipeStorage.addRecipe(recipe);
//        verify(databaseError).toException();
//    }

//    @Test
//    public synchronized void canReadARecipe() throws InterruptedException {
//        when(databaseRecipeReference.child(Integer.toString(2))).thenReturn(databaseIdRecipeReference);
//        prepareAsyncCallRead((listener) -> listener.onDataChange(dataSnapshot));
//        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe);
//        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(recipe, true);
//        recipeStorage.readRecipe(2, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }
//
//    @Test
//    public synchronized void cannotReadRecipeWhenNull() throws InterruptedException {
//        when(databaseRecipeReference.child(Integer.toString(4))).thenReturn(databaseIdRecipeReference);
//        prepareAsyncCallRead((listener) -> listener.onDataChange(dataSnapshot));
//        when(dataSnapshot.getValue(Recipe.class)).thenReturn(null);
//        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(null, false);
//        recipeStorage.readRecipe(4, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }
//
//    @Test
//    public synchronized void cannotReadRecipeOnCancelled() throws InterruptedException {
//        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
//        when(databaseRecipeReference.child(Integer.toString(4))).thenReturn(databaseIdRecipeReference);
//        prepareAsyncCallRead((listener) -> listener.onCancelled(databaseError));
//        CallHandlerChecker<Recipe> fakeCallHandler = new CallHandlerChecker<>(null, false);
//        recipeStorage.readRecipe(4, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }

//    @Test
//    public synchronized void canGetNRecipes() throws InterruptedException {
//        prepareNRecipesFor(2, 6);
//        prepareAsyncNCall((listener) -> listener.onDataChange(dataSnapshot));
//        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
//        List<Recipe> recipes = new ArrayList<>();
//        recipes.add(recipe1);
//        recipes.add(recipe2);
//        DataSnapshot d1 = mock(DataSnapshot.class);
//        DataSnapshot d2 = mock(DataSnapshot.class);
//        List<DataSnapshot> dataSnapshots = new ArrayList<>();
//        dataSnapshots.add(d1);
//        dataSnapshots.add(d2);
//        when(d1.getValue(Recipe.class)).thenReturn(recipe1);
//        when(d2.getValue(Recipe.class)).thenReturn(recipe2);
//        when(dataSnapshot.getChildren()).thenReturn(dataSnapshots);
//        when(dataSnapshot.getValue()).thenReturn(new Object());
//        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(recipes, true);
//        recipeStorage.getNRecipes(5, 2, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }
//
//    @Test
//    public synchronized void cannotGetNRecipesWhenNull() throws InterruptedException {
//        prepareNRecipesFor(1, 3);
//        prepareAsyncNCall((listener) -> listener.onDataChange(dataSnapshot));
//        when(dataSnapshot.getValue(Recipe.class)).thenReturn(null);
//        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(null, false);
//        recipeStorage.getNRecipes(3, 1, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }

//    @Test
//    public synchronized void cannotReadNRecipesOnCancelled() throws InterruptedException {
//        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
//        prepareNRecipesFor(2, 5);
//        prepareAsyncNCall((listener) -> listener.onCancelled(databaseError));
//        CallHandlerChecker<List<Recipe>> fakeCallHandler = new CallHandlerChecker<>(null, false);
//        recipeStorage.getNRecipes(4, 2, fakeCallHandler);
//        wait(1000);
//        fakeCallHandler.assertWasCalled();
//    }
//
//    @Test
//    public synchronized void canGetNRecipesOneByOne() throws InterruptedException {
//        prepareNRecipesFor(4, 5);
//        prepareAsyncNCallChild((listener) -> {
//            listener.onChildAdded(dataSnapshot, null);
//            listener.onChildAdded(dataSnapshot2, null);
//        });
//        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
//        List<Recipe> recipes = new ArrayList<>();
//        recipes.add(recipe1);
//        recipes.add(recipe2);
//        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe1);
//        when(dataSnapshot2.getValue(Recipe.class)).thenReturn(recipe2);
//        CallNotifierChecker<Recipe> fakeCallNotifier = new CallNotifierChecker<>(recipes, true);
//        recipeStorage.getNRecipesOneByOne(2, 4, fakeCallNotifier);
//        wait(1000);
//        fakeCallNotifier.assertWasCalled(2);
//    }

//    @Test
//    public synchronized void canGetNRecipesOneByOneWhenChanged() throws InterruptedException {
//        when(databaseRecipeReference.orderByKey()).thenReturn(databaseRecipeReference);
//        prepareNRecipesFor(2, 3);
//        prepareAsyncNCallChild((listener) -> {
//            listener.onChildChanged(dataSnapshot, null);
//            listener.onChildChanged(dataSnapshot2, null);
//        });
//        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
//        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
//        List<Recipe> recipes = new ArrayList<>();
//        recipes.add(recipe1);
//        recipes.add(recipe2);
//        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe1);
//        when(dataSnapshot2.getValue(Recipe.class)).thenReturn(recipe2);
//        CallNotifierChecker<Recipe> fakeCallNotifier = new CallNotifierChecker<>(recipes, true);
//        recipeStorage.getNRecipesOneByOne(2, 2, fakeCallNotifier);
//        wait(1000);
//        fakeCallNotifier.assertWasCalled(2);
//    }
//
//    @Test
//    public synchronized void cannotReadNRecipesOneByOneOnCancelled() throws InterruptedException {
//        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
//        prepareNRecipesFor(2, 5);
//        prepareAsyncNCallChild((listener) -> listener.onCancelled(databaseError));
//        CallNotifierChecker<Recipe> fakeCallNotifier = new CallNotifierChecker<>(null, false);
//        recipeStorage.getNRecipesOneByOne(4, 2, fakeCallNotifier);
//        wait(1000);
//        fakeCallNotifier.assertWasCalled(1);
//    }

//    private void prepareAsyncCallAdd(Consumer<ValueEventListener> func) {
//        doAnswer((call) -> {
//            func.accept(call.getArgument(0));
//            return null;
//        }).when(databaseIdReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
//    }
//
//    private void prepareAsyncCallRead(Consumer<ValueEventListener> func) {
//        doAnswer((call) -> {
//            func.accept(call.getArgument(0));
//            return null;
//        }).when(databaseIdRecipeReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
//    }

    private void prepareAsyncNCall(Consumer<ValueEventListener> func) {
        doAnswer((call) -> {
            func.accept(call.getArgument(0));
            return null;
        }).when(query).addValueEventListener(any(ValueEventListener.class));
    }

    private void prepareAsyncNCallChild(Consumer<ChildEventListener> func) {
        doAnswer((call) -> {
            func.accept(call.getArgument(0));
            return null;
        }).when(query).addChildEventListener(any(ChildEventListener.class));
    }

    private void prepareNRecipesFor(int start, int end) {
        when(databaseRecipeReference.orderByKey()).thenReturn(databaseRecipeReference);
        when(databaseRecipeReference.startAt(""+start)).thenReturn(databaseRecipeReference);
        when(databaseRecipeReference.endAt(""+end)).thenReturn(query);
    }
}

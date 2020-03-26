package ch.epfl.polychef;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.epfl.polychef.recipe.OfflineRecipes;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;

import static junit.framework.TestCase.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeStorageTest {

    @Mock
    FirebaseDatabase firebaseDatabase;

    @Mock
    DatabaseReference databaseIdReference;

    @Mock
    DatabaseReference databaseRecipeReference;

    @Mock
    DatabaseReference databaseIdRecipeReference;

    @Mock
    Query query;

    @Mock
    DataSnapshot dataSnapshot;

    @Mock
    DataSnapshot dataSnapshot2;

    @Mock
    DatabaseError databaseError;

    @Before
    public void initMockFirebaseDatabase() {
        MockitoAnnotations.initMocks(this);
        when(firebaseDatabase.getReference("id")).thenReturn(databaseIdReference);
        when(firebaseDatabase.getReference("recipe")).thenReturn(databaseRecipeReference);
    }

    @Test
    public void nullThrowsExceptions() {
        RecipeStorage recipeStorage = new RecipeStorage();
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.addRecipe(null));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipe(0, mock(CallHandler.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipe(-1, mock(CallHandler.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.readRecipe(2, null));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipes(0, 2, mock(CallHandler.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipes(3, 0, mock(CallHandler.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipes(3, 3, null));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipesOneByOne(0, 2, mock(CallNotifier.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipesOneByOne(3, 0, mock(CallNotifier.class)));
        assertThrows(IllegalArgumentException.class, () -> recipeStorage.getNRecipesOneByOne(3, 3, null));
    }

    @Test
    public void canAddRecipe() {
        prepareAsyncCallAdd((listener) -> listener.onDataChange(dataSnapshot));
        when(dataSnapshot.getValue(Integer.class)).thenReturn(2);
        when(databaseRecipeReference.child(Integer.toString(3))).thenReturn(databaseIdRecipeReference);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        fakeRecipeStorage.addRecipe(recipe);
        verify(databaseIdReference).setValue(3);
        verify(databaseIdRecipeReference).setValue(recipe);
    }

    @Test
    public void cannotAddRecipeWhenCancelled() {
        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
        prepareAsyncCallAdd((listener) -> listener.onCancelled(databaseError));
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        fakeRecipeStorage.addRecipe(recipe);
        verify(databaseError).toException();
    }

    @Test
    public synchronized void canReadARecipe() throws InterruptedException {
        when(databaseRecipeReference.child(Integer.toString(2))).thenReturn(databaseIdRecipeReference);
        prepareAsyncCallRead((listener) -> listener.onDataChange(dataSnapshot));
        Recipe recipe = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<Recipe> fakeCallHandler = new FakeCallHandler<>(recipe, true);
        fakeRecipeStorage.readRecipe(2, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void cannotReadRecipeWhenNull() throws InterruptedException {
        when(databaseRecipeReference.child(Integer.toString(4))).thenReturn(databaseIdRecipeReference);
        prepareAsyncCallRead((listener) -> listener.onDataChange(dataSnapshot));
        when(dataSnapshot.getValue(Recipe.class)).thenReturn(null);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<Recipe> fakeCallHandler = new FakeCallHandler<>(null, false);
        fakeRecipeStorage.readRecipe(4, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void cannotReadRecipeOnCancelled() throws InterruptedException {
        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
        when(databaseRecipeReference.child(Integer.toString(4))).thenReturn(databaseIdRecipeReference);
        prepareAsyncCallRead((listener) -> listener.onCancelled(databaseError));
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<Recipe> fakeCallHandler = new FakeCallHandler<>(null, false);
        fakeRecipeStorage.readRecipe(4, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void canGetNRecipes() throws InterruptedException {
        prepareNRecipesFor(2, 6);
        prepareAsyncNCall((listener) -> listener.onDataChange(dataSnapshot));
        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe1);
        recipes.add(recipe2);
        DataSnapshot d1 = mock(DataSnapshot.class);
        DataSnapshot d2 = mock(DataSnapshot.class);
        List<DataSnapshot> dataSnapshots = new ArrayList<>();
        dataSnapshots.add(d1);
        dataSnapshots.add(d2);
        when(d1.getValue(Recipe.class)).thenReturn(recipe1);
        when(d2.getValue(Recipe.class)).thenReturn(recipe2);
        when(dataSnapshot.getChildren()).thenReturn(dataSnapshots);
        when(dataSnapshot.getValue()).thenReturn(new Object());
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<List<Recipe>> fakeCallHandler = new FakeCallHandler<>(recipes, true);
        fakeRecipeStorage.getNRecipes(5, 2, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void cannotGetNRecipesWhenNull() throws InterruptedException {
        prepareNRecipesFor(1, 3);
        prepareAsyncNCall((listener) -> listener.onDataChange(dataSnapshot));
        when(dataSnapshot.getValue(Recipe.class)).thenReturn(null);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<List<Recipe>> fakeCallHandler = new FakeCallHandler<>(null, false);
        fakeRecipeStorage.getNRecipes(3, 1, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void cannotReadNRecipesOnCancelled() throws InterruptedException {
        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
        prepareNRecipesFor(2, 5);
        prepareAsyncNCall((listener) -> listener.onCancelled(databaseError));
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallHandler<List<Recipe>> fakeCallHandler = new FakeCallHandler<>(null, false);
        fakeRecipeStorage.getNRecipes(4, 2, fakeCallHandler);
        wait(1000);
        assertTrue(fakeCallHandler.wasCalled);
    }

    @Test
    public synchronized void canGetNRecipesOneByOne() throws InterruptedException {
        prepareNRecipesFor(4, 5);
        prepareAsyncNCallChild((listener) -> {
            listener.onChildAdded(dataSnapshot, null);
            listener.onChildAdded(dataSnapshot2, null);
        });
        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe1);
        recipes.add(recipe2);
        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe1);
        when(dataSnapshot2.getValue(Recipe.class)).thenReturn(recipe2);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallNotifier fakeCallNotifier = new FakeCallNotifier(recipes, true);
        fakeRecipeStorage.getNRecipesOneByOne(2, 4, fakeCallNotifier);
        wait(1000);
        assertThat(fakeCallNotifier.numberOfCall, equalTo(2));
    }

    @Test
    public synchronized void canGetNRecipesOneByOneWhenChanged() throws InterruptedException {
        when(databaseRecipeReference.orderByKey()).thenReturn(databaseRecipeReference);
        prepareNRecipesFor(2, 3);
        prepareAsyncNCallChild((listener) -> {
            listener.onChildChanged(dataSnapshot, null);
            listener.onChildChanged(dataSnapshot2, null);
        });
        Recipe recipe1 = OfflineRecipes.getInstance().getOfflineRecipes().get(0);
        Recipe recipe2 = OfflineRecipes.getInstance().getOfflineRecipes().get(1);
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe1);
        recipes.add(recipe2);
        when(dataSnapshot.getValue(Recipe.class)).thenReturn(recipe1);
        when(dataSnapshot2.getValue(Recipe.class)).thenReturn(recipe2);
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallNotifier fakeCallNotifier = new FakeCallNotifier(recipes, true);
        fakeRecipeStorage.getNRecipesOneByOne(2, 2, fakeCallNotifier);
        wait(1000);
        assertThat(fakeCallNotifier.numberOfCall, equalTo(2));
    }

    @Test
    public synchronized void cannotReadNRecipesOneByOneOnCancelled() throws InterruptedException {
        when(databaseError.toException()).thenReturn(mock(DatabaseException.class));
        prepareNRecipesFor(2, 5);
        prepareAsyncNCallChild((listener) -> listener.onCancelled(databaseError));
        RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
        FakeCallNotifier fakeCallNotifier = new FakeCallNotifier(null, false);
        fakeRecipeStorage.getNRecipesOneByOne(4, 2, fakeCallNotifier);
        wait(1000);
        assertThat(fakeCallNotifier.numberOfCall, equalTo(1));
    }

    private void prepareAsyncCallAdd(Consumer<ValueEventListener> func) {
        doAnswer((call) -> {
            func.accept(call.getArgument(0));
            return null;
        }).when(databaseIdReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    private void prepareAsyncCallRead(Consumer<ValueEventListener> func) {
        doAnswer((call) -> {
            func.accept(call.getArgument(0));
            return null;
        }).when(databaseIdRecipeReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

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

    private class FakeRecipeStorage extends RecipeStorage {
        @Override
        public FirebaseDatabase getFirebaseDatabase() {
            return firebaseDatabase;
        }
    }

    private class FakeCallHandler<T> implements CallHandler<T> {

        private final T expected;
        private final boolean shouldBeSuccessful;

        private boolean wasCalled = false;

        public FakeCallHandler(T expected, boolean shouldBeSuccessful) {
            this.expected = expected;
            this.shouldBeSuccessful = shouldBeSuccessful;
        }

        @Override
        public void onSuccess(T data) {
            assertThat(data, equalTo(expected));
            Assert.assertTrue(shouldBeSuccessful);
            wasCalled = true;
        }

        @Override
        public void onFailure() {
            assertFalse(shouldBeSuccessful);
            wasCalled = true;
        }
    }

    private class FakeCallNotifier implements CallNotifier<Recipe> {

        private final List<Recipe> expected;
        private final boolean shouldBeSuccessful;

        private int numberOfCall = 0;

        public FakeCallNotifier(List<Recipe> expected, boolean shouldBeSuccessful) {
            this.expected = expected;
            this.shouldBeSuccessful = shouldBeSuccessful;
        }

        @Override
        public void notify(Recipe data) {
            assertThat(data, isIn(expected));
            expected.remove(data);
            Assert.assertTrue(shouldBeSuccessful);
            ++numberOfCall;
        }

        @Override
        public void onFailure() {
            assertFalse(shouldBeSuccessful);
            ++numberOfCall;
        }
    }
}

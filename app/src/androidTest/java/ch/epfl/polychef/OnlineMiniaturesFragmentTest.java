package ch.epfl.polychef;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.fragments.OnlineMiniaturesFragment;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;

import static androidx.test.espresso.Espresso.onView;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class OnlineMiniaturesFragmentTest {

    private RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
    private Recipe testRecipe1 = new RecipeBuilder().setName("test1").setRecipeDifficulty(Recipe.Difficulty.EASY).addInstruction("test1instruction").setPersonNumber(4).setEstimatedCookingTime(30).setEstimatedPreparationTime(30).addIngredient("test1", 1.0, Ingredient.Unit.CUP).build();

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Mock
    FirebaseDatabase firebaseInstance;

    @Before
    public void initMockAndStorage() {
        MockitoAnnotations.initMocks(this);
        fakeRecipeStorage = new FakeRecipeStorage();
    }

    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    @Test
    public synchronized void databaseEmptyAddNothingToView() throws InterruptedException {
        initActivity();
        wait(1000);
        assertEquals(0, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void oneElementIsDisplayedOnActivityLoadIfDatabaseContainsOne() throws InterruptedException {
        fakeRecipeStorage.addRecipe(testRecipe1);

        initActivity();
        wait(1000);
        assertEquals(1, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void maxElementAreLoadedOnActivityStart() throws InterruptedException {
        for(int i = 0; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            fakeRecipeStorage.addRecipe(testRecipe1);
        }
        initActivity();
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void maxElementAreLoadedOnActivityStartAndNoMore() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            fakeRecipeStorage.addRecipe(testRecipe1);
        }
        fakeRecipeStorage.addRecipe(testRecipe1);

        initActivity();
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingWithDatabaseSmallerThanMaxLoadedAtATimeShouldAddNothingToTheMiniaturesList() throws InterruptedException {
        fakeRecipeStorage.addRecipe(testRecipe1);
        initActivity();
        wait(1000);
        onView(withId(R.id.miniaturesList))
                .perform(RecyclerViewActions.scrollToPosition(getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);
        assertEquals(1, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingDownLoadANewRecipe() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            fakeRecipeStorage.addRecipe(testRecipe1);
        }
        fakeRecipeStorage.addRecipe(testRecipe1);
        initActivity();
        wait(1000);
        onView(withId(R.id.miniaturesList))
                .perform(RecyclerViewActions.scrollToPosition(getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime + 1, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

//    @Test
//    public synchronized void scrollingDownLoadANewRecipePeriodically() throws InterruptedException {
//        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
//            fakeRecipeStorage.addRecipe(testRecipe1);
//            fakeRecipeStorage.addRecipe(testRecipe2);
//        }
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//        fakeRecipeStorage.addRecipe(testRecipe2);
//
//
//
//
//        initActivity();
//        wait(1000);
//        onView(withId(R.id.miniaturesList))
//                .perform(RecyclerViewActions.scrollToPosition(getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount() - 1));
//        wait(4000);
//        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
//        wait(4000);
//        onView(withId(R.id.miniaturesList))
//                .perform(RecyclerViewActions.scrollToPosition(getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount() - 1));
//        wait(4000);
//        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
//        wait(1000);
//        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
//        wait(1000);
//        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
//        wait(1000);
//        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime * 2 + 1, ((FakeRecipeStorage) fakeRecipeStorage).getRecipeList().size());
//    }

    @Test
    public synchronized void scrollingDownLoadANewRecipeOnceButNotMore() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            fakeRecipeStorage.addRecipe(testRecipe1);
            fakeRecipeStorage.addRecipe(testRecipe1);
        }
        fakeRecipeStorage.addRecipe(testRecipe1);
        initActivity();
        wait(1000);
        onView(withId(R.id.miniaturesList))
                .perform(RecyclerViewActions.scrollToPosition(getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesList)).perform(ViewActions.swipeUp());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime * 2, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    public OnlineMiniaturesFragment getMiniaturesFragment(){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        return (OnlineMiniaturesFragment) hostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            return fakeRecipeStorage;
        }

        @Override
        protected void retrieveUserInfo(String email) {

        }

        @Override
        protected void newUser(String email) {

        }

        @Override
        protected void oldUser(DataSnapshot snap) {

        }

        @Override
        protected void updateUserInfo() {

        }
    }

    public class FakeRecipeStorage extends RecipeStorage {

        private List<Recipe> recipesInDatabase = new ArrayList<>();

        private int getIndexInArrayList(int indexInDatabase) {
            return indexInDatabase - 1;
        }

        @Override
        public FirebaseDatabase getFirebaseDatabase() {
            return firebaseInstance;
        }

        public List<Recipe> getRecipeList(){
           return recipesInDatabase;
        }

        @Override
        public void addRecipe(Recipe recipe) {
            id += 1;
            recipesInDatabase.add(recipe);
        }

        @Override
        public void readRecipe(int id, CallHandler<Recipe> ch){
            int actualIndex = getIndexInArrayList(id);
            if(!(actualIndex >= 0 && actualIndex < recipesInDatabase.size())){
               ch.onFailure();
            }
            ch.onSuccess(recipesInDatabase.get(actualIndex));
        }

        @Override
        public void getNRecipes(int numberOfRecipes, int fromId, CallHandler<List<Recipe>> caller){
            int actualFromIndex = getIndexInArrayList(fromId);
            if(actualFromIndex >= recipesInDatabase.size()){
                return;
            }
            caller.onSuccess(recipesInDatabase.subList(actualFromIndex, Math.min(recipesInDatabase.size(), actualFromIndex + numberOfRecipes + 1)));
        }

        @Override
        public void getNRecipesOneByOne(int numberOfRecipes, int fromId, CallNotifier<Recipe> caller){
            int actualFromIndex = getIndexInArrayList(fromId);
            if(actualFromIndex >= recipesInDatabase.size()){
                return;
            }
            int maxIndexWithData = Math.min(recipesInDatabase.size() - 1, actualFromIndex + numberOfRecipes - 1);
            for(int i = actualFromIndex; i <= maxIndexWithData; i ++){
                caller.notify(recipesInDatabase.get(i));
            }
        }
    }

}

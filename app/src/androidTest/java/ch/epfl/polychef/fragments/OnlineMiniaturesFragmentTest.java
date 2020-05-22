package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.SearchRecipe;
import ch.epfl.polychef.users.SearchUser;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class OnlineMiniaturesFragmentTest {

    private RecipeStorage fakeRecipeStorage = Mockito.mock(RecipeStorage.class,CALLS_REAL_METHODS);
    private SearchRecipe mockRecipeSearch = Mockito.mock(SearchRecipe.class);
    private UserStorage fakeUserStorage = Mockito.mock(UserStorage.class);
    private SearchUser mockUserSearch = Mockito.mock(SearchUser.class);
    private List<Recipe> recipesInDatabase;
    private User testUser = new User("test", "username");
    private int currentReadIndex = 0;
    private String currentOlderDate = "01/05/20 11:59:59";
    private String currentYoungerDate = "01/05/20 12:00:01";

    private FragmentTestUtils fragUtils = new FragmentTestUtils();

    private RecipeBuilder testRecipeBuilder = new RecipeBuilder().setName("test1")
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .addInstruction("test1instruction").setPersonNumber(4)
            .setEstimatedCookingTime(30).setEstimatedPreparationTime(30)
            .addIngredient("test1", 1.0, Ingredient.Unit.CUP).setAuthor("test");


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

        fakeRecipeStorage = Mockito.mock(RecipeStorage.class);

        recipesInDatabase = new ArrayList<>();
        currentReadIndex = 0;
        currentOlderDate = "2020/05/01 11:59:59";
        currentYoungerDate = "2020/05/01 12:00:01";

        initializeMockRecipeStorage();
        initializerMockUserStorage();
    }

    public void addNewOlderRecipe(){
        currentOlderDate = changeDateByNSeconds(currentOlderDate, -1);
        fakeRecipeStorage.addRecipe(testRecipeBuilder.setDate(currentOlderDate).build());
    }

    public void addNewYoungerRecipe(){
        currentYoungerDate = changeDateByNSeconds(currentYoungerDate, 1);
        fakeRecipeStorage.addRecipe(testRecipeBuilder.setDate(currentYoungerDate).build());
    }

    public String changeDateByNSeconds(String dateStr, int nbSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat(RecipeStorage.RECIPE_DATE_FORMAT);
        Date date = new Date();
        try {
            date = formatter.parse(dateStr);
            date.setSeconds(date.getSeconds() + nbSeconds);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return formatter.format(date);
    }

    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    @Test
    public synchronized void searchCanReturnRecipes() throws InterruptedException {
        initActivity();

        search("test");

        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.fullRecipeFragment)).check(matches(isDisplayed()));
    }

    @Test
    public synchronized void searchCanReturnUsers() throws InterruptedException {
        initActivity();

        search("test");

        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.userProfileFragment)).check(matches(isDisplayed()));
    }

    @Test
    public synchronized void canClickOnFiltersWhenSearch() throws InterruptedException {
        initActivity();

        search("test");
        wait(100);
        onView(withId(R.id.filter_ingre)).perform(click());
        wait(100);
        onView(withId(R.id.filter_users)).perform(click());
        wait(100);
        onView(withId(R.id.filter_recipe)).perform(click());
        wait(100);
        onView(withId(R.id.filter_rate)).perform(click());
    }

    @Test
    public synchronized void removesDuplicate() throws InterruptedException {
        initActivity();
        fakeRecipeStorage.addRecipe(testRecipeBuilder.setDate(currentYoungerDate).build());
        fakeRecipeStorage.addRecipe(testRecipeBuilder.setDate(currentYoungerDate).build());
        search("test");
    }

    @Test
    public synchronized void databaseEmptyAddNothingToView() throws InterruptedException {
        initActivity();
        wait(1000);
        assertEquals(0, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void oneElementIsDisplayedOnActivityLoadIfDatabaseContainsOne() throws InterruptedException {
        addNewOlderRecipe();

        initActivity();
        wait(1000);
        assertEquals(1, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void maxElementAreLoadedOnActivityStart() throws InterruptedException {
        for(int i = 0; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
        }
        initActivity();
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void maxElementAreLoadedOnActivityStartAndNoMore() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
        }
        addNewOlderRecipe();

        initActivity();
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingDownWithDatabaseSmallerThanMaxLoadedAtATimeShouldAddNothingToTheMiniaturesList() throws InterruptedException {
        addNewOlderRecipe();
        initActivity();
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesOnlineList))
                .perform(RecyclerViewActions.scrollToPosition(((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);
        assertEquals(1, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingUpWithDatabaseSmallerThanMaxLoadedAtATimeShouldAddNothingToTheMiniaturesList() throws InterruptedException {
        addNewOlderRecipe();
        initActivity();
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesOnlineList)).perform(ViewActions.swipeDown());
        wait(1000);
        assertEquals(1, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }


    @Test
    public synchronized void scrollingDownDownLoadANewRecipe() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
        }
        addNewOlderRecipe();
        initActivity();
        wait(1000);
        onView(withId(R.id.miniaturesOnlineList))
                .perform(RecyclerViewActions.scrollToPosition(((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);

        onView(ViewMatchers.withId(R.id.miniaturesOnlineList)).perform(ViewActions.swipeUp());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime + 1, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingUpDownLoadANewRecipe() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
        }
        addNewYoungerRecipe();
        initActivity();
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesOnlineList)).perform(ViewActions.swipeDown());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime + 1, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingDownLoadANewRecipeOnceButNotMore() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
            addNewOlderRecipe();
        }
        addNewOlderRecipe();
        initActivity();
        wait(1000);
        onView(withId(R.id.miniaturesOnlineList))
                .perform(RecyclerViewActions.scrollToPosition(((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount() - 1));
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesOnlineList)).perform(ViewActions.swipeUp());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime * 2, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void scrollingUpLoadANewRecipeOnceButNotMore() throws InterruptedException {
        for(int i = 0 ; i < OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime; i++){
            addNewOlderRecipe();
            addNewYoungerRecipe();
        }
        addNewYoungerRecipe();
        initActivity();
        wait(1000);
        onView(ViewMatchers.withId(R.id.miniaturesOnlineList)).perform(ViewActions.swipeDown());
        wait(1000);
        assertEquals(OnlineMiniaturesFragment.nbOfRecipesLoadedAtATime * 2, ((OnlineMiniaturesFragment) fragUtils.getTestedFragment(intentsTestRule)).getRecyclerView().getAdapter().getItemCount());
    }

    private synchronized void search(String query) throws InterruptedException {
        wait(1000);
        onView(withId(R.id.searchBar)).perform(typeSearchViewText(query));
        onView(withId(R.id.searchBar)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        wait(1000);
    }

    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }

        @Override
        public UserStorage getUserStorage(){
            return fakeUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            return fakeRecipeStorage;
        }

    }

    private void initializeMockRecipeStorage(){

        when(fakeRecipeStorage.getCurrentDate()).thenReturn("01/05/20 12:00:00");

        when(fakeRecipeStorage.getFirebaseDatabase()).thenReturn(firebaseInstance);

        when(fakeRecipeStorage.getSearch()).thenReturn(mockRecipeSearch);

        doAnswer(invocation -> {
            CallHandler<List<Miniatures>> caller = invocation.getArgument(1);
            List<Miniatures> result = new ArrayList<>(1);
            result.add(testRecipeBuilder.build());
            caller.onSuccess(result);
            return null;
        }).when(mockRecipeSearch).searchForRecipe(any(), any());

        doAnswer(invocation -> {
            recipesInDatabase.add(invocation.getArgument(0));
            return null;
        }).when(fakeRecipeStorage).addRecipe(any(Recipe.class));

        doAnswer(invocation -> {
            int numberOfRecipes=invocation.getArgument(0);
            CallHandler<List<Recipe>> caller = invocation.getArgument(4);

            if(currentReadIndex < recipesInDatabase.size()){
                List<Recipe> results = recipesInDatabase.subList(currentReadIndex, Math.min(recipesInDatabase.size(), currentReadIndex + numberOfRecipes));
                caller.onSuccess(results);
                currentReadIndex += Math.min(recipesInDatabase.size() - currentReadIndex, currentReadIndex + numberOfRecipes);
            }
            return null;
        }).when(fakeRecipeStorage).getNRecipes(any(Integer.class), any(String.class), any(String.class), any(Boolean.class), any(CallHandler.class));
    }

    private void initializerMockUserStorage(){
        when(fakeUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));

        when(fakeUserStorage.getPolyChefUser()).thenReturn(new User("polychef", "user"));

        when(fakeUserStorage.getSearch()).thenReturn(mockUserSearch);

        doAnswer(invocation -> {
            CallHandler<List<Miniatures>> caller = invocation.getArgument(1);
            List<Miniatures> result = new ArrayList<>(1);
            result.add(testUser);
            caller.onSuccess(result);
            return null;
        }).when(mockUserSearch).searchForUser(any(), any());

        doAnswer(invocation -> {
            CallHandler<Miniatures> caller = invocation.getArgument(1);
            caller.onSuccess(testUser);
            return null;
        }).when(fakeUserStorage).getUserByEmail(any(String.class), any(CallHandler.class));
    }

    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setIconified(false);
                ((SearchView) view).setIconified(true);
                ((SearchView) view).setQuery(text,false);
                ((SearchView) view).setIconified(false);
            }
        };
    }
}

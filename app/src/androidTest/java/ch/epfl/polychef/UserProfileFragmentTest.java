package ch.epfl.polychef;

import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

import ch.epfl.polychef.fragments.UserProfileFragment;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class UserProfileFragmentTest {

    private String mockEmail = "mock@email.com";
    private String mockUsername = "mockUsername";
    private User mockUser;

    private RecipeBuilder builder = new RecipeBuilder()
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .addInstruction("test1instruction")
            .setPersonNumber(4)
            .setEstimatedCookingTime(30)
            .setEstimatedPreparationTime(30)
            .addIngredient("test1", 1.0, Ingredient.Unit.CUP);

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new UserProfileFragmentTest.FakeHomePage();
            return activity;
        }
    };

    public UserProfileFragment getTestedFragment(){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);

        return (UserProfileFragment) hostFragment.getChildFragmentManager().getFragments().get(0);
    }

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Before
    public synchronized void initTest() throws InterruptedException {
        mockUser = new User(mockEmail, mockUsername);

        Intents.init();
        intentsTestRule.launchActivity(new Intent());
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        wait(1000);
    }

    public void startTest() {
        onView(withId(R.id.drawerProfileImage)).perform(click());
    }

    @Test
    public void userInfoIsDisplayed() {
        startTest();
        onView(withId(R.id.UsernameDisplay)).check(matches(withText(mockUsername)));
        onView(withId(R.id.UserEmailDisplay)).check(matches(withText(mockEmail)));
    }

    @Test
    public void nothingIsDisplayedWhenUserHasNoRecipe() {
        startTest();
        assertEquals(0, getTestedFragment().getUserRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void recipesAreDisplayedWhenUserHasFewOfThem() throws InterruptedException {
        testUserRecipes(0, UserProfileFragment.nbOfRecipesLoadedAtATime + 1);
    }

    @Test
    public synchronized void recipesAreDisplayedWhenUserHasManyOfThem() throws InterruptedException {
        testUserRecipes(UserProfileFragment.nbOfRecipesLoadedAtATime + 1, 20);
    }

    public synchronized void testUserRecipes(int min, int max) throws InterruptedException {
        Random rnd = new Random();

        int n = rnd.nextInt(max - min) + min;   //min inclusive, max exclusive
        for(int i = 0; i < n; ++i){
            mockUser.addRecipe("Recipe " + i);
        }

        startTest();

        int recipeLoaded = UserProfileFragment.nbOfRecipesLoadedAtATime;
        assertEquals(Math.min(recipeLoaded, n), getTestedFragment().getUserRecyclerView().getAdapter().getItemCount());

        for(int i = 0; i < n/recipeLoaded; ++i){
            wait(1000);

            int scrollTo = getTestedFragment().getUserRecyclerView().getAdapter().getItemCount() - 1;

            onView(withId(R.id.UserRecipesList))
                    .perform(RecyclerViewActions.scrollToPosition(scrollTo));
            wait(1000);

            onView(withId(R.id.UserRecipesList)).perform(swipeUp());
            wait(1000);
        }
        assertEquals(n, getTestedFragment().getUserRecyclerView().getAdapter().getItemCount());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    class FakeHomePage extends HomePage {

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);

            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser);

            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = Mockito.mock(RecipeStorage.class);
            doAnswer(invocation -> {

                String UUID = invocation.getArgument(0);
                CallHandler<Recipe> caller = invocation.getArgument(1);

                caller.onSuccess(builder.setName(UUID).build());

                return null;
            }).when(mockRecipeStorage).readRecipeFromUUID(any(String.class), any(CallHandler.class));
            return mockRecipeStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            return mockUser;
        }
    }
}

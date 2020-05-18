package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.OpinionsMiniatureAdapter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CommentTestOnFullRecipe {

    public static RecipeBuilder fakeRecipeBuilder = new RecipeBuilder()
            .setName("Fake recipe")
            .addInstruction("Instruction 1")
            .addIngredient("ingredient", 2, Ingredient.Unit.NONE)
            .setPersonNumber(1)
            .setEstimatedCookingTime(1)
            .setEstimatedPreparationTime(1)
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setDate("20/06/01 13:10:00")
            .setAuthor("author name");

    private HashMap<String, User> userResults;

    private User mockUser;
    private List<Recipe> recipeArr = new ArrayList<>();

    private User mockUser(String userEmail, String userName){
        return new User(userEmail, userName);
    }

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

    @Before
    public void init() {
        userResults = new HashMap<>();
        mockUser = mockUser("mock@email.com", "mockUsername");
        intentsTestRule.launchActivity(new Intent());
    }

    private class FakeHomePage extends RateRecipeFragmentsHomeTest.FakeFakeHomePage {

        public RecipeStorage mockRecipeStorage = mock(RecipeStorage.class);

        public FakeHomePage() {
            Recipe testRecipe = fakeRecipeBuilder.build();
            recipeArr.add(testRecipe);
            testRecipe = fakeRecipeBuilder.build();
            testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
            recipeArr.add(testRecipe);
            testRecipe = fakeRecipeBuilder.build();
            testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
            testRecipe.getRating().addOpinion("id2", 3, "Ceci est un commentaire de test");
            testRecipe.getRating().addOpinion("id3", 3, "Ceci est un commentaire de test");
            testRecipe.getRating().addOpinion("id4", 3, "Ceci est un commentaire de test");
            testRecipe.getRating().addOpinion("id5", 3, "Ceci est un commentaire de test");
            testRecipe.getRating().addOpinion("id6", 3, "Ceci est un commentaire de test");
            recipeArr.add(testRecipe);
        }

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            doAnswer((call) -> {
                CallHandler<User> ch = call.getArgument(1);
                ch.onSuccess(mockUser);
                return null;
            }).when(mockUserStorage).getUserByEmail(anyString(), any(CallHandler.class));
            doAnswer(invocation -> {

                String userID = invocation.getArgument(0);
                CallHandler<User> ch = invocation.getArgument(1);
                if(userResults.containsKey(userID)) {
                    ch.onSuccess(userResults.get(userID));
                }else{
                    ch.onFailure();
                }

                return null;
            }).when(mockUserStorage).getUserByID(anyString(), any(CallHandler.class));
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            User connectedUser = new User("TestUser@PolyChef.com", "TestUser");
            connectedUser.setKey("test key");
            when(mockUserStorage.getPolyChefUser()).thenReturn(connectedUser);
            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            when(mockRecipeStorage.getCurrentDate()).thenCallRealMethod();
            doAnswer((call) -> {
                CallHandler<List<Recipe>> ch =  call.getArgument(4);
                ch.onSuccess(recipeArr);
                return null;
            }).when(mockRecipeStorage).getNRecipes(any(Integer.class),any(String.class),any(String.class),any(Boolean.class),any(CallHandler.class));
            return mockRecipeStorage;
        }
    }

    @Test
    public void noCommentIsDisplayedOnFragmentLoad() {
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        assertEquals(0, ((FullRecipeFragment)new FragmentTestUtils().getTestedFragment(intentsTestRule)).getOpinionsRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public void oneCommentIsDisplayed() {
        userResults.put("id1", mockUser("testEmail", "test"));
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        assertEquals(1, ((FullRecipeFragment)new FragmentTestUtils().getTestedFragment(intentsTestRule)).getOpinionsRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public void oneCommentIsDisplayedWithCorrectUser() {
        userResults.put("id1", mockUser("testEmail", "test"));
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        OpinionsMiniatureAdapter adapter = (OpinionsMiniatureAdapter) ((FullRecipeFragment)new FragmentTestUtils().getTestedFragment(intentsTestRule)).getOpinionsRecyclerView().getAdapter();
        assertEquals("testEmail", adapter.getMap().get(adapter.getDisplayedOpinions().get(0)).getEmail());
        assertEquals("test", adapter.getMap().get(adapter.getDisplayedOpinions().get(0)).getUsername());
    }

    @Test
    public void clickOnCommentLaunchUserProfile() {
        User mockUser = mockUser("testEmail", "test");
        mockUser.setKey("id1");
        userResults.put("id1", mockUser);
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.opinionsList)).perform(NestedScrollViewHelper.nestedScrollTo(), RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.UsernameDisplay)).check(matches(isDisplayed()));
        onView(withId(R.id.UsernameDisplay)).check(matches(withText("test")));
    }

    @Test
    public void scrollDownTheCommentsLoadNewComments() {
        userResults.put("id1", mockUser("testEmail", "test"));
        userResults.put("id2", mockUser("testEmail", "test"));
        userResults.put("id3", mockUser("testEmail", "test"));
        userResults.put("id4", mockUser("testEmail", "test"));
        userResults.put("id5", mockUser("testEmail", "test"));
        userResults.put("id6", mockUser("testEmail", "test"));
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.opinionsList)).perform(NestedScrollViewHelper.nestedScrollTo());
        onView(withId(R.id.fullRecipeFragment)).perform(swipeUp());
        onView(withId(R.id.fullRecipeFragment)).perform(swipeUp());
        onView(withId(R.id.fullRecipeFragment)).perform(swipeUp());
        assertEquals(6, ((FullRecipeFragment)new FragmentTestUtils().getTestedFragment(intentsTestRule)).getOpinionsRecyclerView().getAdapter().getItemCount());
    }

}

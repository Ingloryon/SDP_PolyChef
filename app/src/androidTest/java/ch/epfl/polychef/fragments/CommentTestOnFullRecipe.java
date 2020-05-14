package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;


import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InterruptedIOException;
import java.util.HashMap;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.notifications.NotificationSenderTest;
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
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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

    private UserStorage fakeUserStorage = mock(UserStorage.class,CALLS_REAL_METHODS );

    private HashMap<String, User> userResults;

    private FakeFullRecipeFragment fakeFragment = new FakeFullRecipeFragment(fakeUserStorage);

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
            true);

    @Before
    public void init(){
        doAnswer(invocation -> {

            String userID = invocation.getArgument(0);
            CallHandler<User> ch = invocation.getArgument(1);
            if(userResults.containsKey(userID)) {
                ch.onSuccess(userResults.get(userID));
            }else{
                ch.onFailure();
            }

            return null;
        }).when(fakeUserStorage).getUserByID(anyString(), any(CallHandler.class));

        userResults = new HashMap<>();
    }


    public void initActivity() {
        Intents.init();
        intentsTestRule.launchActivity(new Intent());
    }
    @After
    public void afterTest(){
        Intents.release();
    }

    public void setUp(Recipe recipe){
        Bundle bundle = new Bundle();
        bundle.putSerializable("Recipe", recipe);
        Fragment fragment = fakeFragment;
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment).addToBackStack(null);
        transaction.commit();
    }

    private class FakeHomePage extends HomePage {

        public RecipeStorage mockUserStorage = mock(RecipeStorage.class);

        @Override
        public UserStorage getUserStorage(){
            return fakeUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            return mockUserStorage;
        }
    }

    @Test
    public synchronized void noCommentIsDisplayedOnFragmentLoad() throws InterruptedException {
        initActivity();
        Recipe testRecipe = fakeRecipeBuilder.build();
        setUp(testRecipe);
        wait(1000);
        assertEquals(0, fakeFragment.getOpinionsRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void oneCommentIsDisplayed() throws InterruptedException {
        initActivity();
        Recipe testRecipe = fakeRecipeBuilder.build();
        testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
        userResults.put("id1", mockUser("testEmail", "test"));
        setUp(testRecipe);
        wait(1000);
        assertEquals(1, fakeFragment.getOpinionsRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void oneCommentIsDisplayedWithCorrectUser() throws InterruptedException {
        initActivity();
        Recipe testRecipe = fakeRecipeBuilder.build();
        testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
        userResults.put("id1", mockUser("testEmail", "test"));
        setUp(testRecipe);
        wait(1000);
        OpinionsMiniatureAdapter adapter = (OpinionsMiniatureAdapter) fakeFragment.getOpinionsRecyclerView().getAdapter();
        assertEquals("testEmail", adapter.getMap().get(adapter.getDisplayedOpinions().get(0)).getEmail());
        assertEquals("test", adapter.getMap().get(adapter.getDisplayedOpinions().get(0)).getUsername());
    }

    @Test
    public synchronized void clickOnCommentLaunchUserProfile() throws InterruptedException {
        initActivity();
        Recipe testRecipe = fakeRecipeBuilder.build();
        testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
        User mockUser = mockUser("testEmail", "test");
        mockUser.setKey("id1");
        userResults.put("id1", mockUser);
        setUp(testRecipe);
        wait(1000);
        onView(withId(R.id.opinionsList)).perform(NestedScrollViewHelper.nestedScrollTo());
        onView(withId(R.id.opinionsList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        wait(1000);
        onView(withId(R.id.UsernameDisplay)).check(matches(isDisplayed()));
        //onView(withId(R.id.UsernameDisplay)).check(matches(withText("test")));
    }



    @Test
    public synchronized  void scrollDownTheCommentsLoadNewComments() throws InterruptedException {
        initActivity();
        Recipe testRecipe = fakeRecipeBuilder.build();
        testRecipe.getRating().addOpinion("id1", 3, "Ceci est un commentaire de test");
        testRecipe.getRating().addOpinion("id2", 3, "Ceci est un commentaire de test");
        testRecipe.getRating().addOpinion("id3", 3, "Ceci est un commentaire de test");
        testRecipe.getRating().addOpinion("id4", 3, "Ceci est un commentaire de test");
        testRecipe.getRating().addOpinion("id5", 3, "Ceci est un commentaire de test");
        testRecipe.getRating().addOpinion("id6", 3, "Ceci est un commentaire de test");

        userResults.put("id1", mockUser("testEmail", "test"));
        setUp(testRecipe);
        wait(1000);
    }

    public static class FakeFullRecipeFragment extends FullRecipeFragment {

        private UserStorage fakeUserStorage;

        public FakeFullRecipeFragment(UserStorage userStorage){
            this.fakeUserStorage = userStorage;
        }

        @Override
        public UserStorage getUserStorage(){
            return fakeUserStorage;
        }

    }





}

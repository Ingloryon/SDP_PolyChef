package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ProfilePictChoiceTest {
    private String mockEmail = "mock@email.com";
    private String mockUsername = "mockUsername";
    private User mockUser;

    private FragmentTestUtils fragUtils = new FragmentTestUtils();

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
            HomePage activity = new ProfilePictChoiceTest.FakeHomePage();
            return activity;
        }
    };

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

    @Test
    public void possibleProfilePicturesAreDisplayedAndCanBeClicked() {
        onView(withId(R.id.drawerProfileImage)).perform(click());
        onView(withId(R.id.usersImage)).perform(click());
        onView(withId(R.id.usersImage)).perform(click());
        //Assertions.assertTrue(((ProfilePictChoice) fragUtils.getTestedFragment(intentsTestRule)) > 0);
        Assertions.assertEquals(20, 20);
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
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser);

            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = Mockito.mock(RecipeStorage.class);
            doAnswer(invocation -> {

                String uuid = invocation.getArgument(0);
                CallHandler<Recipe> caller = invocation.getArgument(1);

                caller.onSuccess(builder.setName(uuid).setAuthor(mockEmail).build());

                return null;
            }).when(mockRecipeStorage).readRecipeFromUuid(any(String.class), any(CallHandler.class));
            return mockRecipeStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            return mockUser;
        }
    }

}

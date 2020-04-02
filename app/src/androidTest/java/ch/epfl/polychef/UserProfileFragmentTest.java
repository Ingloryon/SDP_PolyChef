package ch.epfl.polychef;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserProfileFragmentTest {

    private String mockEmail = "mock@email.com";
    private String mockUsername = "mockUsername";

    private int numberOfRecipes = 5;

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new UserProfileFragmentTest.FakeHomePage();
            return activity;
        }
    };


    @Test
    public void firstTest() {

    }

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Before
    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    class FakeHomePage extends HomePage {

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            User mockUser = Mockito.mock(User.class);
            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser);
            when(mockUser.getEmail()).thenReturn(mockEmail);
            when(mockUser.getUsername()).thenReturn(mockUsername);
            ArrayList<String> recipes = new ArrayList<>(numberOfRecipes);
            when(mockUser.getRecipes()).thenReturn(recipes);    //TODO choose mock recipes
            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = Mockito.mock(RecipeStorage.class);
            doNothing().when(mockRecipeStorage).readRecipeFromUUID(any(String.class), any(CallHandler.class));
            return mockRecipeStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            return mockUser;
        }


    }
}

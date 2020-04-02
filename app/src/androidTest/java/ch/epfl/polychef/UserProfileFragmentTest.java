package ch.epfl.polychef;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mockito;

import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.RecipeStorage;

import static org.mockito.Mockito.when;

public class UserProfileFragmentTest {

        FirebaseDatabase firebaseDatabase;

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
        public void initActivity() {
            intentsTestRule.launchActivity(new Intent());
        }

        @After
        public void finishActivity(){
            intentsTestRule.finishActivity();
        }

        private class FakeRecipeStorage extends RecipeStorage {
            @Override
            public FirebaseDatabase getFirebaseDatabase() {
                return firebaseDatabase;
            }
        }

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);

            when(mockUser.getEmail()).thenReturn("test@epfl.ch");
            when(mockUser.getDisplayName()).thenReturn("TestUsername");
            return mockUser;
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

}

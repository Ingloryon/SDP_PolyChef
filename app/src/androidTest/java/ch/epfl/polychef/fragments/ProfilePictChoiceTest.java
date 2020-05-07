package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.CallHandlerChecker;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void profilePicturesCanBeClickedAndUpdatesProfile() {
        onView(withId(R.id.drawerProfileImage)).perform(click());
        onView(withId(R.id.usersImage)).perform(click());
        onView(withIndex(withId(R.id.profile_picture_drawable), 2)).perform(click());
        //onData(withId(R.id.profile_picture_drawable)).inAdapterView(withId(R.id.listView)).atPosition(0).perform(click());

        assertEquals(20, 20);
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }


    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    class FakeHomePage extends HomePage {
        ArrayList<Recipe> arr=new ArrayList<>();

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

       /* @Override
        public FirebaseDatabase getFireDatabase(){


            FirebaseDatabase mockFirebase=Mockito.mock(FirebaseDatabase.class);
            DatabaseReference mockDatabaseReference=Mockito.mock(DatabaseReference.class);

            when(mockFirebase.getReference(anyString())).thenReturn(mockDatabaseReference);
            when(mockDatabaseReference.child(anyString())).thenReturn(mockDatabaseReference);

            CallHandlerChecker<Recipe> callHandler=new CallHandlerChecker<Recipe>(arr.get(0),true);

            doAnswer((call) -> {
                Recipe recipe =  call.getArgument(0);
                callHandler.onSuccess(recipe);

                return null;
            }).when(mockDatabaseReference).setValue(any(Recipe.class));

            return mockFirebase;
        }*/
    }

}

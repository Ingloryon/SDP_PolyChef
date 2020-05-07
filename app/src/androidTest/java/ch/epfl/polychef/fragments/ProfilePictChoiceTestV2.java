package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.view.View;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.pages.HomePageTest;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ProfilePictChoiceTestV2 {

    private static final int IDX_IMAGE_TO_TEST=2;
//    private FragmentTestUtils fragUtils = new FragmentTestUtils();

//    private RecipeBuilder builder = new RecipeBuilder()
//            .setRecipeDifficulty(Recipe.Difficulty.EASY)
//            .addInstruction("test1instruction")
//            .setPersonNumber(4)
//            .setEstimatedCookingTime(30)
//            .setEstimatedPreparationTime(30)
//            .addIngredient("test1", 1.0, Ingredient.Unit.CUP);


    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeFakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Test
    public void profilePicturesCanBeClickedAndUpdatesProfile() {
        onView(withId(R.id.drawer)).perform(DrawerActions.open());

        onView(withId(R.id.drawerProfileImage)).perform(click());
        onView(withId(R.id.usersImage)).perform(click());

        ((FakeFakeHomePage)intentsTestRule.getActivity()).setMockUserProfilePictureId(1);

        onView(withIndex(withId(R.id.profile_picture_drawable), IDX_IMAGE_TO_TEST)).perform(click());
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

    public static class FakeFakeHomePage extends HomePageTest.FakeHomePage {

        private User mockUser=new User("TestUser@PolyChef.com", "TestUser");

        @Override
        public UserStorage getUserStorage() {
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(this.mockUser);

            User mockUserCopy=new User("TestUser@PolyChef.com", "TestUser");
            mockUserCopy.setProfilePictureId(IDX_IMAGE_TO_TEST);

            CallHandlerChecker<User> callHandler=new CallHandlerChecker<>(mockUserCopy,true);

            doAnswer((call) -> {
                User userReceived =  call.getArgument(0);
                callHandler.onSuccess(userReceived);

                return null;
            }).when(mockUserStorage).updateUserInfo(any(User.class));

            return mockUserStorage;
        }

        public void setMockUserProfilePictureId(int id) {
            mockUser.setProfilePictureId(id);
        }

        public User getMockUser() {
            return mockUser;
        }
    }
}

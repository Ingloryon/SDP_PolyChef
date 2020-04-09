package ch.epfl.polychef;

import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.polychef.fragments.OnlineMiniaturesFragment;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class RecipeMiniatureImageTest {

    private String mockEmail = "mock@email.com";
    private String mockUsername = "mockUsername";
    private User mockUser;

    private RecipeBuilder recipeBuilder = new RecipeBuilder()
                .addInstruction("test instruction")
            .setPersonNumber(4)
            .setEstimatedCookingTime(35)
            .setEstimatedPreparationTime(40)
            .addIngredient("test", 1.0, Ingredient.Unit.CUP)
            .setRecipeDifficulty(Recipe.Difficulty.EASY);

    private Recipe recipe1 = recipeBuilder.setName("test1").setMiniatureFromPath("test_path").build();

    private Recipe recipe2 = recipeBuilder.setName("test2").setMiniatureFromPath("test_path2").build();

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new RecipeMiniatureImageTest.FakeHomePage();
            return activity;
        }
    };

    public OnlineMiniaturesFragment getMiniatureFragment(){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        return (OnlineMiniaturesFragment) hostFragment.getChildFragmentManager().getFragments().get(0);
    }

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public synchronized void initTest() {
        mockUser = new User(mockEmail, mockUsername);

        Intents.init();
        intentsTestRule.launchActivity(new Intent());
    }

    @Test
    public synchronized void canShowOnlineMiniature() throws InterruptedException {
        wait(1000);
        assertEquals(2, getMiniatureFragment().getRecyclerView().getAdapter().getItemCount());
        onView(allOf(withId(R.id.miniatureRecipeImage), hasSibling(withText("test1")))).check(matches(isDisplayed()));
        onView(withId(R.id.miniaturesOnlineList)).perform(actionOnItemAtPosition(1, scrollTo()));
        onView(allOf(withId(R.id.miniatureRecipeImage), hasSibling(withText("test2")))).check(matches(not(isDisplayed())));
    }


    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    private static class FakeImageStorage extends ImageStorage {
        @Override
        public void getImage(String imageName, CallHandler<byte[]> caller) {
            byte[] data = new byte[] {1, 2, 3, 4, 3, 2, 1};
            if(imageName.equals("test_path")) {
                caller.onSuccess(data);
            } else {
                caller.onFailure();
            }
        }
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

            doAnswer((call) -> {
                CallNotifier<Recipe> ch = call.getArgument(2);
                ch.notify(recipe1);
                ch.notify(recipe2);
                return null;
            }).when(mockRecipeStorage).getNRecipesOneByOne(any(Integer.class), any(Integer.class), any(CallNotifier.class));

            return mockRecipeStorage;
        }

        @Override
        public ImageStorage getImageStorage() {
            return new FakeImageStorage();
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            return mockUser;
        }
    }
}

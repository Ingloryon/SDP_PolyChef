package ch.epfl.polychef;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.fragments.OnlineMiniaturesFragment;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserProfileFragmentTest {

    private String mockEmail = "mock@email.com";
    private String mockUsername = "mockUsername";

    private RecipeStorage fakeRecipeStorage = Mockito.mock(RecipeStorage.class,CALLS_REAL_METHODS);
    private List<Recipe> recipesInDatabase = new ArrayList<>();
    private List<String> recipesSupposedUUID = new ArrayList<>();

    private Recipe testRecipe1 = new RecipeBuilder()
            .setName("test1")
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .addInstruction("test1instruction")
            .setPersonNumber(4)
            .setEstimatedCookingTime(30)
            .setEstimatedPreparationTime(30)
            .addIngredient("test1", 1.0, Ingredient.Unit.CUP)
            .build();

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new UserProfileFragmentTest.FakeHomePage();
            return activity;
        }
    };

    @Mock
    FirebaseDatabase firebaseInstance;

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    public void initActivity(){
        intentsTestRule.launchActivity(new Intent());
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.drawerProfileImage)).perform(click());
        onView(withId(R.id.drawer)).perform(DrawerActions.close());
    }

    @Before
    public void initMockAndStorage() {
        MockitoAnnotations.initMocks(this);
        fakeRecipeStorage = Mockito.mock(RecipeStorage.class);

        recipesInDatabase = new ArrayList<Recipe>();

        initializeMockRecipeStorage();
    }

    private void initializeMockRecipeStorage(){
        when(fakeRecipeStorage.getFirebaseDatabase()).thenReturn(firebaseInstance);

        doAnswer(invocation -> {
            String uuid = invocation.getArgument(0);
            CallHandler<Recipe> caller = invocation.getArgument(1);
            if(recipesSupposedUUID.contains(uuid)){
                caller.onSuccess(recipesInDatabase.get(recipesSupposedUUID.indexOf(uuid)));
            }
            return null;
        }).when(fakeRecipeStorage).readRecipeFromUUID(any(String.class), any(CallHandler.class));
    }


    @Test
    public synchronized void noRecipeInUserProfileLoadNothing() throws InterruptedException {
        initActivity();
        wait(1000);

        assertEquals(0, getUsersFragment().getUserRecyclerView().getAdapter().getItemCount());
    }
    @Test
    public synchronized  void oneRecipeInUserProfileIsLoaded() throws InterruptedException{
        recipesSupposedUUID.add(Integer.toString(recipesSupposedUUID.size()));
        recipesInDatabase.add(testRecipe1);
        initActivity();
        wait(1000);
        assertEquals(1, getUsersFragment().getUserRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void maxRecipeAtOnceAddedInUserProfile() throws InterruptedException{
        for(int i = 0; i < UserProfileFragment.nbOfRecipesLoadedAtATime; i++){
            recipesSupposedUUID.add(Integer.toString(recipesSupposedUUID.size()));
            recipesInDatabase.add(testRecipe1);
        }
        initActivity();
        wait(1000);
        assertEquals(UserProfileFragment.nbOfRecipesLoadedAtATime, getUsersFragment().getUserRecyclerView().getAdapter().getItemCount());
    }
    @Test
    public synchronized void scrollLoadOtherRecipes() throws InterruptedException{
        for(int i = 0; i < UserProfileFragment.nbOfRecipesLoadedAtATime; i++){
            recipesSupposedUUID.add(Integer.toString(recipesSupposedUUID.size()));
            recipesInDatabase.add(testRecipe1);
        }
        recipesSupposedUUID.add(Integer.toString(recipesSupposedUUID.size()));
        recipesInDatabase.add(testRecipe1);
        initActivity();
        wait(4000);
        onView(withId(R.id.UserRecipesList))
                .perform(RecyclerViewActions.scrollToPosition(getUsersFragment().getUserRecyclerView().getAdapter().getItemCount() - 1));
        wait(4000);
        onView(ViewMatchers.withId(R.id.UserRecipesList)).perform(ViewActions.swipeUp());
        wait(4000);
        assertEquals(UserProfileFragment.nbOfRecipesLoadedAtATime + 1, getUsersFragment().getUserRecyclerView().getAdapter().getItemCount());
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
            when(mockUser.getRecipes()).thenReturn(recipesSupposedUUID);
            return mockUserStorage;
        }

        @Override
        public RecipeStorage getRecipeStorage() {
            return fakeRecipeStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            return mockUser;
        }

    }

    public UserProfileFragment getUsersFragment(){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        if(hostFragment.getChildFragmentManager().getFragments().get(0) instanceof UserProfileFragment){
            return (UserProfileFragment) hostFragment.getChildFragmentManager().getFragments().get(0);
        }
        return null;
    }
}

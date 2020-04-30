package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.RecipeTest;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class RateRecipeFragmentsTest {

    Recipe aRecipe= RecipeTest.setStandardRecipe().build();

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRuleEntry = new ActivityTestRule<>(fakeEntryPage, false,
            false);
    @Rule
    public ActivityTestRule<HomePage> intentsTestRuleHome = new ActivityTestRule<>(fakeHomePage, false,
            false);


    private void launchFakeEntryPage() {
        intentsTestRuleEntry.launchActivity(new Intent());
    }

    private void launchFakeHomePage() {
        intentsTestRuleHome.launchActivity(new Intent());
    }

    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateButtonIsDisplayedAndDisplayCorrectText(){
        launchFakeEntryPage();

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonRate)).check(matches(withText(R.string.RateButton)));
    }

    @Test
    public void toastIsDisplayedIfTryToRateWhileNotLoggedIn(){
        launchFakeEntryPage();

        onView(withId(R.id.miniaturesOfflineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(click());
        onView(withText(R.string.errorOnlineFeature))
                .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleEntry.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void rateSpinnerCanBeClickedOn() {
        launchFakeHomePage();

        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(click());
        onView(withId(R.id.RateChoices)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("0 star"))).perform(click());
        onView(withId(R.id.RateChoices)).check(matches(withSpinnerText(containsString("0 star"))));
    }


    @Test
    public void test(){
        RateRecipeFragment rrf=new RateRecipeFragment();
        RateRecipeFragment mockRrf=Mockito.mock(RateRecipeFragment.class);

        View mockView=Mockito.mock(View.class);
        Bundle mockBundle=Mockito.mock(Bundle.class);

        //when()
        rrf.onViewCreated(mockView,mockBundle);

    }

    /*public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        recipe = (Recipe) bundle.getSerializable("RecipeToRate");

        String text = getActivity().getString(R.string.RateText) + " \"" + recipe.getName() + "\" ?";
        TextView rateText =  getView().findViewById(R.id.RateText);
        rateText.setText(text);


        postButton = getView().findViewById(R.id.buttonSendRate);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSendRate();
            }
        });
    }*/

    public static class FakeHomePage extends HomePage {

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = Mockito.mock(RecipeStorage.class);
            when(mockRecipeStorage.getCurrentDate()).thenCallRealMethod();

            ArrayList<Recipe> arr=new ArrayList<>();
            arr.add(RecipeTest.setStandardRecipe().build());

            doAnswer((call) -> {
                CallHandler<List<Recipe>> ch =  call.getArgument(4);
                ch.onSuccess(arr);

                return null;
            }).when(mockRecipeStorage).getNRecipes(anyInt(),anyString(),anyString(),anyBoolean(),any(CallHandler.class));


            return mockRecipeStorage;
        }

        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(new User("TestUser@PolyChef.com", "TestUser"));
            return mockUserStorage;
        }

        @Override
        public FirebaseUser getUser() {
            FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
            when(mockUser.getEmail()).thenReturn("test@epfl.ch");
            when(mockUser.getDisplayName()).thenReturn("TestUsername");
            return mockUser;
        }
    }

}

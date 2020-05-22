package ch.epfl.polychef.fragments;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.pages.HomePageTest;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.RecipeTest;
import ch.epfl.polychef.utils.CallHandlerChecker;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class RateRecipeFragmentsHomeTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeFakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRuleHome = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public void startTest() {
        Intents.init();
        intentsTestRuleHome.launchActivity(new Intent());
    }

    @After
    public void releaseIntent() {
        Intents.release();
    }

    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateSpinnerCanBeClickedOn() throws InterruptedException {

        //Click on the first recipe
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        rateCurrentRecipeNStars(0);
        String s0="Your rating is 0 stars.";
        sendRateAndCheckToast(s0);

        rateCurrentRecipeNStars(1);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s1="Your new rating is 1 stars. Your previous rating was 0";
        sendRateAndCheckToast(s1);

    }

    private void rateCurrentRecipeNStars(int nbStars){

        onView(withId(R.id.buttonRate)).perform(NestedScrollViewHelper.nestedScrollTo(),click());
        onView(withId(R.id.RateChoices)).perform(click());
        String star= nbStars<2?" star":" stars";
        onData(allOf(is(instanceOf(String.class)), is(nbStars+star))).perform(click());
        onView(withId(R.id.RateChoices)).check(matches(withSpinnerText(containsString(nbStars+star))));
    }

    private synchronized void sendRateAndCheckToast(String expectedText) throws InterruptedException {
        onView(withId(R.id.buttonSendRate)).perform(scrollTo(), click());

        onView(withText(expectedText))
                .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleHome.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        wait(2000);

    }

    public static class FakeFakeHomePage extends HomePageTest.FakeHomePage {

        ArrayList<Recipe> arr=new ArrayList<>();

        FakeFakeHomePage(){
            super();
            arr.add(RecipeTest.setStandardRecipe().setDate("2020/04/01 12:00:01").build());
        }

        @Override
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
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            RecipeStorage mockRecipeStorage = Mockito.mock(RecipeStorage.class);
            when(mockRecipeStorage.getCurrentDate()).thenCallRealMethod();

            doAnswer((call) -> {
                CallHandler<List<Recipe>> ch =  call.getArgument(4);
                ch.onSuccess(arr);

                return null;
            }).when(mockRecipeStorage).getNRecipes(any(Integer.class),any(String.class),any(String.class),any(Boolean.class),any(CallHandler.class));


            return mockRecipeStorage;
        }
    }

}

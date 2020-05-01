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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.pages.HomePageTest;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.RecipeTest;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.CallHandlerChecker;

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
            true);


    @Test
    public void createInstanceOfRateRecipeFragmentDoesNotThrowError(){
        RateRecipeFragment rrf=new RateRecipeFragment();
    }

    @Test
    public void rateSpinnerCanBeClickedOn() {

        String s0="Your rating is 0 stars.";
        String s1="Your new rating is 1 stars. Your previous rating was 0";

        for(int i =0;i<2;i++) {
            onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.buttonRate)).perform(click());
            onView(withId(R.id.RateChoices)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(i+" star"))).perform(click());
            onView(withId(R.id.RateChoices)).check(matches(withSpinnerText(containsString(i+" star"))));
            onView(withId(R.id.buttonSendRate)).perform(click());
            if(i==0){
                onView(withText(s0))
                        .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleHome.getActivity()
                                .getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));
            }else{
                onView(withText(s1))
                        .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleHome.getActivity()
                                .getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));
            }
        }
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

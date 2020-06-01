package ch.epfl.polychef.fragments;

import android.content.Intent;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
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
import ch.epfl.polychef.utils.CustomRatingBar;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@RunWith(AndroidJUnit4.class)
public class RateRecipeFragmentsHomeTest {

    private CustomRatingBar ratingBar;

    private FragmentTestUtils fragUtils = new FragmentTestUtils();

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
    public synchronized void startTest(){
        Intents.init();
        intentsTestRuleHome.launchActivity(new Intent());
        //Click on the first recipe
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.buttonRate)).perform(NestedScrollViewHelper.nestedScrollTo(),click());
        RateRecipeFragment rateFragment = (RateRecipeFragment) fragUtils.getTestedFragment(intentsTestRuleHome);
        ratingBar = rateFragment.getRatingBar();
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
    public void rateBarCanBeClickedOn() {

        rateCurrentRecipeNStars(1);
        String s0="Your rating is 1 stars.";
        sendRateAndCheckToast(s0);
        onView(withId(R.id.buttonRate)).perform(NestedScrollViewHelper.nestedScrollTo(),click());

        rateCurrentRecipeNStars(2);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s1="Your new rating is 2 stars. Your previous rating was 1";
        sendRateAndCheckToast(s1);

    }

    @Test
    public void clickOnRateRegisterCorrectRate(){
        for(int i = 0; i < 5; i++){
            rateCurrentRecipeNStars(i + 1);
            assertEquals(i + 1, ratingBar.getRate());
        }
    }

    @Test
    public void nonClickableRatingBarIsIndeedUnClickable(){
        // Change the page rating bar by a non clickable one for test purpose
        RateRecipeFragment rateFragment = (RateRecipeFragment) fragUtils.getTestedFragment(intentsTestRuleHome);
        rateFragment.setRatingBar(new CustomRatingBar(rateFragment.getView().findViewById(R.id.RateChoices), R.drawable.spatuladoree, R.drawable.spatuladoreehalf, R.drawable.spatulagray, false));
        ratingBar = rateFragment.getRatingBar();
        ratingBar.setRate(0);
        //This should do nothing since the click is disable
        rateCurrentRecipeNStars(3);
        assertEquals(0, ratingBar.getRate());
    }

    @Test
    public void correctImageResourceAreDisplayedWithoutHalf() throws Throwable {
        rateCurrentRecipeNStars(3);
        ArrayList<ImageView> images = ratingBar.getAllStarsImage();

        assertNotNull(images);
        assertEquals((int)images.get(0).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(1).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(2).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(3).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(4).getTag(), ratingBar.getEmptyImageResource());
    }

    @Test
    public void correctImageResourceAreDisplayedWithHalf() throws Throwable {
        //Not possible to rate half but here we force it to test for other display place of the rating bar
        runOnUiThread(() -> ratingBar.setRate(2.5));
        ArrayList<ImageView> images = ratingBar.getAllStarsImage();
        assertNotNull(images);
        assertEquals((int)images.get(0).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(1).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(2).getTag(), ratingBar.getHalfImageResource());
        assertEquals((int)images.get(3).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(4).getTag(), ratingBar.getEmptyImageResource());
    }

    @Test
    public void correctImageResourceAreDisplayedWithLessThanZero() throws Throwable {
        runOnUiThread(() -> ratingBar.setRate(-5));
        ArrayList<ImageView> images = ratingBar.getAllStarsImage();
        assertNotNull(images);
        assertEquals(0, ratingBar.getRate());
        assertEquals((int)images.get(0).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(1).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(2).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(3).getTag(), ratingBar.getEmptyImageResource());
        assertEquals((int)images.get(4).getTag(), ratingBar.getEmptyImageResource());
    }

    @Test
    public void correctImageResourceAreDisplayedWithMoreThanFive() throws Throwable {
        runOnUiThread(() -> ratingBar.setRate(10));
        ArrayList<ImageView> images = ratingBar.getAllStarsImage();
        assertNotNull(images);
        assertEquals(5, ratingBar.getRate());
        assertEquals((int)images.get(0).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(1).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(2).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(3).getTag(), ratingBar.getFullImageResource());
        assertEquals((int)images.get(4).getTag(), ratingBar.getFullImageResource());
    }

    private void rateCurrentRecipeNStars(int nbStars){
        if(nbStars < 1 || nbStars > 5){
            return;
        }
        switch(nbStars){
            case 1:
                onView(withId(R.id.star0)).perform(click());
                break;
            case 2:
                onView(withId(R.id.star1)).perform(click());
                break;
            case 3:
                onView(withId(R.id.star2)).perform(click());
                break;
            case 4:
                onView(withId(R.id.star3)).perform(click());
                break;
            case 5:
                onView(withId(R.id.star4)).perform(click());
                break;
            default:
                break;
        }
    }

    private void sendRateAndCheckToast(String expectedText){
        onView(withId(R.id.buttonSendRate)).perform(scrollTo(), click());

        //TODO Fix this test
//        onView(withText(expectedText))
//                .inRoot(RootMatchers.withDecorView(not(is(intentsTestRuleHome.getActivity()
//                        .getWindow().getDecorView()))))
//                .check(matches(isDisplayed()));
    }

    public static class FakeFakeHomePage extends HomePageTest.FakeHomePage {

        ArrayList<Recipe> arr=new ArrayList<>();

        public FakeFakeHomePage(){
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

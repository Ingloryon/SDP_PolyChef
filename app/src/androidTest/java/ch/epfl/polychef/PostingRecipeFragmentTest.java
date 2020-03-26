package ch.epfl.polychef;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.polychef.pages.HomePage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PostingRecipeFragmentTest {

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new PostingRecipeFragmentTest.FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    @Before
    public void initActivity(){
        Intents.init();
        intentsTestRule.launchActivity(new Intent());
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_recipe));
        onView(withId(R.id.drawer)).perform(DrawerActions.close());
    }

    @After
    public void releaseIntent() {
        Intents.release();
    }

    @Test
    public void checkThatFragmentIsDisplayed() {
        onView(withId(R.id.postRecipeFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickPostRecipeWithEmptyDisplaysErrorLogs() {
        checkErrorLog("There are errors in the given inputs :"
                + "\nCooking Time: should be a positive number."
                + "\nIngredients: There should be 3 arguments entered as {a,b,c}"
                + "\nInstructions: the entered instructions should match format {a},{b},... (no spaces)"
                + "\nPerson number: should be a number between 0 and 100."
                + "\nPreparation Time: should be a positive number."
                + "\nTitle: too long or too short. Need to be between 3 and 80 characters.");
    }

    @Test
    public void onClickPostRecipeWithEverythingButNameDisplaysErrorLogs() {
        writeRecipe("","{a,1,gram},{b,2,cup}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :"
                + "\nTitle: too long or too short. Need to be between 3 and 80 characters.");
    }

    @Test
    public void nullUnitInIngredientDisplaysErrorLogs() {
        writeRecipe("Cake","{a,1,null},{a,1}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :"
                + "\nIngredients: The entered unit is not part of the possible units "
                + "[TEASPOON, TABLESPOON, POUND, KILOGRAM, GRAM, CUP, OUNCE, NO_UNIT, NONE].");
        }

    @Test
    public void noSeparatorInIngredientDisplaysErrorLogs() {
        writeRecipe("Cake","{a}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :"
                + "\nIngredients: There should be 3 arguments entered as {a,b,c}");
    }

    @Test
    public void negativeQuantityDisplaysErrorLogs() {
        writeRecipe("Cake","{a,-1,gram}","{a},{b}","10","10", "10");
        checkErrorLog("There are errors in the given inputs :"
                + "\nIngredients: There should be 3 arguments entered as {a,b,c}");
    }

    @Test
    public void zeroPersonNumberDisplaysErrorLogs() {
        writeRecipe("Cake","{a,10,gram}","{a},{b}","0","10", "10");
        checkErrorLog("There are errors in the given inputs :\nPerson number:  The number of persons must be strictly positive");
    }

    @Test
    public void zeroPrepTimeDisplaysErrorLogs() {
        writeRecipe("Cake","{a,10,gram}","{a},{b}","10","0", "0");
        checkErrorLog("There are errors in the given inputs :\nPreparation time:  The estimated time required must be strictly positive");
    }

    @Test
    public void testOnACompleteRecipe() {
        writeRecipe("aaaa","{a,1,gram},{b,2,cup}","{a},{b}","10","10", "10");
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(scrollTo() ,click());
    }

    @Test
    public void testClickOnMiniatureButtonOpenDialog() {
        onView(withId(R.id.miniature)).perform(scrollTo(), click());
        onView(withText("Add a picture")).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
    }

    @Test
    public void testHandleActivityGallery() {
        setActivityResultGallery(R.id.miniature);
    }

    @Test
    public void testHandleActivityGalleryMealPictures() {
        setActivityResultGallery(R.id.pictures);
    }

    @Test
    public void testHandleActivityTakePhoto() {
        Intent resultData = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
        onView(withId(R.id.miniature)).perform(scrollTo(), click());
        onView(withText("Add a picture")).check(matches(isDisplayed()));
        onView(withText("Take Photo")).perform(click());
    }

    private void setActivityResultGallery(int id) {
        Intent resultData = new Intent(Intent.ACTION_GET_CONTENT);
        Bitmap icon = BitmapFactory.decodeResource(
                ApplicationProvider.getApplicationContext().getResources(),
                R.drawable.frenchtoast);
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(result);
        onView(withId(id)).perform(scrollTo(), click());
        onView(withText("Add a picture")).check(matches(isDisplayed()));
        onView(withText("Choose from Gallery")).perform(click());
    }

    private void writeRecipe(String name, String ingre, String instru, String personNb, String prep, String cook){
        onView(withId(R.id.nameInput)).perform(scrollTo(), typeText(name));
        onView(withId(R.id.ingredientsList)).perform(scrollTo(), typeText(ingre));
        onView(withId(R.id.instructionsList)).perform(scrollTo(), typeText(instru));
        onView(withId(R.id.personNbInput)).perform(scrollTo(), typeText(personNb));
        onView(withId(R.id.prepTimeInput)).perform(scrollTo(), typeText(prep));
        onView(withId(R.id.cookTimeInput)).perform(typeText(cook));
    }

    private void checkErrorLog(String expected){
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(scrollTo(), click());
        onView(withId(R.id.errorLogs)).perform(scrollTo());
        onView(withId(R.id.errorLogs)).check(matches(isDisplayed()));
        (onView(withId(R.id.errorLogs))).check(matches(withText(expected)));
    }

    private class FakeHomePage extends HomePage {
        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }
    }
}

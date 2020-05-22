package ch.epfl.polychef.fragments;

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
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
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

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.notifications.NotificationSender;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

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
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

    private RecipeStorage mockRecipeStorage;

    private String email1="first@gmail.com";
    private String email2="second@gmail.com";
    private User mockUser1;
    private User mockUser2;
    private List<Recipe> recipeArr = new ArrayList<>();

    private Recipe fakeRecipe1 = CommentTestOnFullRecipe.fakeRecipeBuilder.setAuthor(email1).build();
    //the second recipe is created after
    private Recipe fakeRecipe2 = CommentTestOnFullRecipe.fakeRecipeBuilder.setDate("20/06/02 13:10:00").setAuthor(email2).build();

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            false);

    @Before
    public void initActivity(){
        mockUser1=new User(email1,"user1");
        mockUser1.setKey("test key user1");
        mockUser2=new User(email2,"user2");
        mockUser2.setKey("test key user2");
        mockRecipeStorage=Mockito.mock(RecipeStorage.class);
        recipeArr.add(fakeRecipe1);
        recipeArr.add(fakeRecipe2);

        Intents.init();
        intentsTestRule.launchActivity(new Intent());
    }

    private void openCreateRecipe(){
        onView(ViewMatchers.withId(R.id.drawer)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView)).perform(NavigationViewActions.navigateTo(R.id.nav_recipe));
        onView(withId(R.id.drawer)).perform(DrawerActions.close());
    }

    @After
    public void releaseIntent() {
        Intents.release();
    }

    @Test
    public void checkThatFragmentIsDisplayed() {
        openCreateRecipe();
        onView(withId(R.id.postRecipeFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void onClickPostRecipeWithEmptyDisplaysErrorLogs() {
        openCreateRecipe();
        checkErrorLog("There are errors in the given inputs :"
                + "\nCooking Time: should be a positive number."
                + "\nIngredient: the number of ingredients can't be 0"
                + "\nInstruction: the number of instructions can't be 0"
                + "\nNumber of Person: should be a number between 0 and 100."
                + "\nPreparation Time: should be a positive number."
                + "\nTitle: should be a string between 3 and 80 characters.");
    }

    @Test
    public void onClickPostRecipeWithEverythingButNameDisplaysErrorLogs() {
        openCreateRecipe();
        writeRecipe("","a", "1","a","10","10", "10");
        checkErrorLog("There are errors in the given inputs :"
                + "\nTitle: should be a string between 3 and 80 characters.");
    }

    @Test
    public void zeroPersonNumberDisplaysErrorLogs() {
        openCreateRecipe();
        writeRecipe("Cake","a", "1","a","0","10", "10");
        checkErrorLog("There are errors in the given inputs :\nPerson number:  The number of persons must be strictly positive");
    }

    @Test
    public void zeroPrepTimeDisplaysErrorLogs() {
        openCreateRecipe();
        writeRecipe("Cake","a", "1","a","10","", "0");
        checkErrorLog("There are errors in the given inputs :\nPreparation Time: should be a positive number.");
    }

    @Test
    public void invalidIngredientsAreRejectedAndPrintErrorLog() {
        openCreateRecipe();
        writeRecipe("Cake","","1","a","10","10", "10");
        checkErrorLog("There are errors in the given inputs :\nIngredient: the ingredient shouldn't be empty");
    }

    @Test
    public void invalidQuantityIsRejectedAndPrintErrorLog() {
        openCreateRecipe();
        writeRecipe("Cake","a","","a","10","10", "10");
        checkErrorLog("There are errors in the given inputs :\nIngredient: the quantity needs to be a positive number");
    }

    @Test
    public void testOnACompleteRecipe() {
        openCreateRecipe();
        writeRecipe("Cake","a", "1","a","10","10", "10");
        addIngredient();
        addInstruction();
        Espresso.closeSoftKeyboard();
        mockInit();
        onView(withId(R.id.postRecipe)).perform(scrollTo(), click());
    }

    @Test
    public void testClickOnMiniatureButtonOpenDialog() {
        openCreateRecipe();
        onView(withId(R.id.miniature)).perform(scrollTo(), click());
        onView(withText("Add a picture")).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
    }

    @Test
    public void testHandleActivityGallery() {
        openCreateRecipe();
        setActivityResultGallery(R.id.miniature);
    }

    @Test
    public void testHandleActivityGalleryMealPictures() {
        openCreateRecipe();
        setActivityResultGallery(R.id.pictures);
    }

    @Test
    public void testHandleActivityTakePhoto() {
        openCreateRecipe();
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

    private void writeRecipe(String name, String ingre, String quantity, String instru, String personNb, String prep, String cook){
        onView(withId(R.id.nameInput)).perform(scrollTo(), typeText(name));
        onView(withId(R.id.ingredient0)).perform(scrollTo(), typeText(ingre));
        onView(withId(R.id.quantity0)).perform(scrollTo(), typeText(quantity));
        onView(withId(R.id.instruction0)).perform(scrollTo(), typeText(instru));
        onView(withId(R.id.personNbInput)).perform(scrollTo(), typeText(personNb));
        onView(withId(R.id.prepTimeInput)).perform(scrollTo(), typeText(prep));
        onView(withId(R.id.cookTimeInput)).perform(scrollTo(), typeText(cook));
    }

    private void addInstruction(){
        onView(withId(R.id.buttonAddInstr)).perform(scrollTo(), click());
    }

    private void addIngredient(){
        onView(withId(R.id.buttonAddIngre)).perform(scrollTo(), click());
    }

    private void checkErrorLog(String expected){
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.postRecipe)).perform(scrollTo(), click());
        onView(withId(R.id.errorLogs)).perform(scrollTo());
        onView(withId(R.id.errorLogs)).check(matches(isDisplayed()));
        (onView(withId(R.id.errorLogs))).check(matches(withText(expected)));
    }

    private void mockInit() {

        doNothing().when(mockRecipeStorage).addRecipe(any(Recipe.class));
        doNothing().when(mockRecipeStorage).getNRecipes(any(Integer.class), any(String.class), any(String.class), any(Boolean.class), any(CallHandler.class));
    }


    @Test
    public void testDoNotDisplayImageRelatedButtonWhenModifying() {
        //click second recipe
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.modifyButton)).perform(click());

        onView(withId(R.id.miniature)).check(matches(not(isDisplayed())));
        onView(withId(R.id.pictures)).check(matches(not(isDisplayed())));
        onView(withId(R.id.miniaturePreview)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testModifyButtonDoesNotAppearWhenItIsNotYourRecipe(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.modifyButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testModifyButtonDoesAppearWhenItIsNotYourRecipe(){
        onView(withId(R.id.miniaturesOnlineList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.modifyButton)).check(matches(isDisplayed()));
    }



    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }

        @Override
        public RecipeStorage getRecipeStorage(){
            when(mockRecipeStorage.getCurrentDate()).thenCallRealMethod();
            doAnswer((call) -> {
                CallHandler<List<Recipe>> ch =  call.getArgument(4);
                ch.onSuccess(recipeArr);
                return null;
            }).when(mockRecipeStorage).getNRecipes(any(Integer.class),any(String.class),any(String.class),any(Boolean.class),any(CallHandler.class));
            return mockRecipeStorage;
        }

        /*@Override
        public UserStorage getUserStorage() {
            mockUser = Mockito.mock(User.class);
            mockUserStorage = Mockito.mock(UserStorage.class);
            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser);
            when(mockUser.getEmail()).thenReturn("fake@email.com");
            doNothing().when(mockUser).addRecipe(any(String.class));
            when(mockUser.getKey()).thenReturn("fake_key");
            return mockUserStorage;
        }*/


        @Override
        public UserStorage getUserStorage(){
            UserStorage mockUserStorage = Mockito.mock(UserStorage.class);
            doAnswer((call) -> {
                CallHandler<User> ch = call.getArgument(1);
                String emailReceived=call.getArgument(0);
                if(email1.equals(emailReceived)){
                    ch.onSuccess(mockUser1);
                }else if(email2.equals(emailReceived)){
                    ch.onSuccess(mockUser2);
                }else{
                    ch.onFailure();
                }
                return null;
            }).when(mockUserStorage).getUserByEmail(anyString(), any(CallHandler.class));

            when(mockUserStorage.getAuthenticatedUser()).thenReturn(Mockito.mock(FirebaseUser.class));
            when(mockUserStorage.getPolyChefUser()).thenReturn(mockUser1);
            return mockUserStorage;
        }

        @Override
        public NotificationSender getNotificationSender() {
            return Mockito.mock(NotificationSender.class);
        }
    }
}

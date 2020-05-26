package ch.epfl.polychef.images;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.synnapps.carouselview.CarouselView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.NestedScrollViewHelper;
import ch.epfl.polychef.R;
import ch.epfl.polychef.fragments.FullRecipeFragment;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class FullRecipeImageTest {

    private ImageStorage mockImageStorage;

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            true);

    private FragmentTest fragmentTest = new FragmentTest();

    private void setUp(String path) {

        Recipe recipe = new RecipeBuilder()
                .setName("test")
                .setAuthor("testAuthor")
                .addInstruction("test instruction")
                .setPersonNumber(4)
                .setEstimatedCookingTime(35)
                .setEstimatedPreparationTime(40)
                .addIngredient("test", 1.0, Ingredient.Unit.CUP)
                .setMiniatureFromPath("test_path")
                .addPicturePath("test_1")
                .addPicturePath(path)
                .setRecipeDifficulty(Recipe.Difficulty.EASY)
                .build();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Recipe", recipe);
        Fragment fragment = fragmentTest;
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_entry_fragment, fragment).addToBackStack(null);
        transaction.commit();
    }

    @Test
    public void canShowPicturesOnFullRecipe() {
        setUp("test_2");
        onView(withId(R.id.recipeImages)).perform(NestedScrollViewHelper.nestedScrollTo()).check(matches(isDisplayed()));
        CarouselView carouselView = intentsTestRule.getActivity().findViewById(R.id.recipeImages);
        assertThat(carouselView.getCurrentItem(), is(0));
        onView(withId(R.id.recipeImages)).perform(swipeLeft());
        assertThat(carouselView.getCurrentItem(), is(1));
        onView(withId(R.id.recipeImages)).perform(swipeLeft());
        assertThat(carouselView.getCurrentItem(), is(2));
        onView(withId(R.id.recipeImages)).perform(swipeLeft());
        assertThat(carouselView.getCurrentItem(), is(2));
    }

    @Test
    public synchronized void callOnFailureIfImageDoesNotExistsShowToast() throws InterruptedException {
        setUp("other_not_found_string");
        onView(withId(R.id.recipeImages)).perform(NestedScrollViewHelper.nestedScrollTo()).check(matches(isDisplayed()));
        onView(withText(R.string.errorImageRetrieve))
                .inRoot(withDecorView(not(is(intentsTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        wait(3000);
    }

    @Test
    public void canOpenRecipeAndHasAuthor() {
        doAnswer((call) -> {
            CallHandler<User> ch = call.getArgument(1);
            ch.onSuccess(new User("TEST", "TEST1234"));
            return null;
        }).when(fragmentTest.mockUserStorage).getUserByEmail(anyString(), any(CallHandler.class));
        setUp("test_2");
        onView(withId(R.id.authorUsername)).check(matches(withText("TEST1234")));
    }

    public static class FragmentTest extends FullRecipeFragment {

        public UserStorage mockUserStorage = mock(UserStorage.class);

        @Override
        public UserStorage getUserStorage() {
            return mockUserStorage;
        }

        @Override
        public ImageStorage getImageStorage() {

            ImageStorage mockImageStorage = Mockito.mock(ImageStorage.class);
            doAnswer(invocation -> {

                CallHandler<byte[]> caller = invocation.getArgument(1);

                String imageName = invocation.getArgument(0);

                byte[] data = new byte[] {1, 2, 3, 4, 3, 2, 1};
                if(imageName.equals("test_path")) {
                    caller.onSuccess(data);
                } else if(imageName.equals("test_1")) {
                    caller.onSuccess(data);
                } else if(imageName.equals("test_2")) {
                    caller.onSuccess(data);
                } else {
                    caller.onFailure();
                }
                return null;
            }).when(mockImageStorage).getImage(any(), any());

            return mockImageStorage;
        }
    }
}

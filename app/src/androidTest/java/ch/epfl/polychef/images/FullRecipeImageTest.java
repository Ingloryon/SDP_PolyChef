package ch.epfl.polychef.images;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.synnapps.carouselview.CarouselView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.fragments.FullRecipeFragment;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class FullRecipeImageTest {

    @Rule
    public IntentsTestRule<EntryPage> intentsTestRule = new IntentsTestRule<>(EntryPage.class);

    private void setUp(String path) {
        Recipe recipe = new RecipeBuilder()
                .setName("test")
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
        bundle.putInt("fragmentID", R.id.nav_entry_fragment);
        Fragment fragment = new FragmentTest();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_entry_fragment, fragment).addToBackStack(null);
        transaction.commit();
    }

    @Test
    public void canShowPicturesOnFullRecipe() {
        setUp("test_2");
        onView(withId(R.id.recipeImages)).perform(scrollTo()).check(matches(isDisplayed()));
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
    public void callOnFailureIfImageDoesNotExistsShowToast() {
        setUp("other_not_found_string");
        onView(withId(R.id.recipeImages)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withText(R.string.errorImageRetrieve))
                .inRoot(withDecorView(not(is(intentsTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    public static class FragmentTest extends FullRecipeFragment {

        @Override
        public ImageStorage getImageStorage() {
            return new FakeImageStorage();
        }
    }

    private static class FakeImageStorage extends ImageStorage {
        @Override
        public void getImage(String imageName, CallHandler<byte[]> caller) {
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
        }
    }
}
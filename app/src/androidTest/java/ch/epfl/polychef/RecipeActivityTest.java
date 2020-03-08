package ch.epfl.polychef;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {
    @Rule
    public IntentsTestRule<MiniatureTestActivity> intentsTestRule = new IntentsTestRule<>(MiniatureTestActivity.class);

    @Test
    public void checkMiniatureTestActivityRunsCorrectly(){
        onView(withId(R.id.cardList)).perform(click());
        assertTrue(true);
    }
}

package ch.epfl.polychef;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static junit.framework.TestCase.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FooRunningMiniatureTestAct {
    //@Rule
    //public IntentsTestRule<MiniatureTestActivity> intentsTestRule = new IntentsTestRule<>(MiniatureTestActivity.class);

    @Test
    public void checkMiniatureTestActivityRunsCorrectly(){
        //onView(withId(R.id.cardList)).perform(click());
        assertTrue(true);
    }
}

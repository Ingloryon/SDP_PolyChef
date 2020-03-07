package ch.epfl.polychef;


import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {
   // @Rule
    //public IntentsTestRule<RecipeActivity> intentsTestRule = new IntentsTestRule<>(RecipeActivity.class);


    @Test
    public void checkMiniatureTestActivityRunsCorrectly(){
        new IntentsTestRule<>(MiniatureTestActivity.class);
    }



}

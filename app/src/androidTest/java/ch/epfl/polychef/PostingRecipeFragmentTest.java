package ch.epfl.polychef;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;

import ch.epfl.polychef.fragments.PostRecipeFragment;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.HomePage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PostingRecipeFragmentTest {

    @Rule
    public IntentsTestRule<HomePage> intentsTestRule = new IntentsTestRule<>(HomePage.class);

    //TODO: Navigate to PortRecipeFragment

    @BeforeAll
    private void setValidInputs(){

        // TODO: Set using R.id.layout_id all EditText to valid values

    }


    @Test
    public void validInputsAreSentToFirebase() {
        //onView(withId(R.id.miniaturesOfflineList)).check(matches(isDisplayed()));
    }

    @Test
    public void rejectsTooLongTitles() {
        //TODO: change the EditText of title to a long value and check displays string "blabla name too long" in onView(withId(R.id.errorLogs))
    }


}

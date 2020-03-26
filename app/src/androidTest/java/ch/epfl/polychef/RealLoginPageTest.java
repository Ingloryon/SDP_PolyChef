package ch.epfl.polychef;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polychef.pages.LoginPage;

@RunWith(AndroidJUnit4.class)
public class RealLoginPageTest {

    @Rule
    public ActivityTestRule<LoginPage> activityTestRule = new ActivityTestRule<>(LoginPage.class);

    @Test
    public void canCallCreateSignInIntent() {
        activityTestRule.getActivity().createSignInIntent(null);
    }

    @Test
    public void getUserReturnNullWhenNotLoggedIn() {
        assertNull(activityTestRule.getActivity().getUser());
    }
}

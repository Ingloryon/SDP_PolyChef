package ch.epfl.polychef;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.test.rule.ActivityTestRule;

import org.junit.Test;

import ch.epfl.polychef.fragments.OnlineMiniaturesFragment;
import ch.epfl.polychef.fragments.PostRecipeFragment;
import ch.epfl.polychef.fragments.UserProfileFragment;
import ch.epfl.polychef.pages.EntryPage;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class OnAttachTest {

    public ActivityTestRule<EntryPage> entryPageRule = new ActivityTestRule<>(EntryPage.class, false,
            false);

    public void onAttachThrowsExceptionWhenWrongContext(Fragment fragment){
        assertThrows(IllegalArgumentException.class, () -> fragment.onAttach((Context) entryPageRule.getActivity()));
    }

    @Test
    public void onAttachTest(){

        onAttachThrowsExceptionWhenWrongContext(new UserProfileFragment());
        onAttachThrowsExceptionWhenWrongContext(new PostRecipeFragment());
        onAttachThrowsExceptionWhenWrongContext(new OnlineMiniaturesFragment());
    }
}

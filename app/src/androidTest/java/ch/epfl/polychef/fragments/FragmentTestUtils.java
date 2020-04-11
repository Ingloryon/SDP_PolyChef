package ch.epfl.polychef.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.rule.ActivityTestRule;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;

public class FragmentTestUtils {

    public Fragment getTestedFragment(ActivityTestRule<HomePage> intentsTestRule){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);

        return hostFragment.getChildFragmentManager().getFragments().get(0);
    }
}

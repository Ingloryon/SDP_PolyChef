package ch.epfl.polychef.fragments;

import androidx.fragment.app.Fragment;

import ch.epfl.polychef.R;
import ch.epfl.polychef.users.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionsFragment extends UserListFragment {

    /**
     * Create a new {@code SubscriptionsFragment}.
     */
    public SubscriptionsFragment() {
        super(User::getSubscriptions, R.layout.fragment_subscriptions);
    }

}

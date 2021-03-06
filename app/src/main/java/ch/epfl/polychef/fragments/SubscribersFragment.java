package ch.epfl.polychef.fragments;

import androidx.fragment.app.Fragment;

import ch.epfl.polychef.R;
import ch.epfl.polychef.users.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribersFragment extends UserListFragment {

    /**
     * Create a new {@code SubscribersFragment}.
     */
    public SubscribersFragment() {
        super(User::getSubscribers, R.layout.fragment_subscribers, R.id.no_subscribers_text);
    }

}

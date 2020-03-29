package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.users.ConnectedActivity;


public class FullUsersFragment extends Fragment {

    public FullUsersFragment() {
        // Required empty public constructor
    }

    private ConnectedActivity hostActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO use .getUsername() and .getUserEmail() once the refactor has been done
        ((TextView) getView().findViewById(R.id.UserEmailDisplay)).setText(hostActivity.getUser().getEmail());
        ((TextView) getView().findViewById(R.id.UsernameDisplay)).setText(hostActivity.getUser().getDisplayName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof ConnectedActivity){
            hostActivity = (ConnectedActivity) context;
        } else {
            throw new IllegalArgumentException("The user profile fragment wasn't attached properly!");
        }
    }
}

package ch.epfl.polychef.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ProfilePicture;
import ch.epfl.polychef.pages.HomePage;

public class ProfilePictChoice extends Fragment {

    private HomePage hostActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof HomePage){
            hostActivity = (HomePage) context;
        } else {
            throw new IllegalArgumentException("The user profile fragment wasn't attached properly!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_pict_choice, container, false);

        ListView listView = view.findViewById(R.id.listView);

        ProfilePicture tom = new ProfilePicture("Tom","admin");
        ProfilePicture jerry = new ProfilePicture("Jerry","user");
        ProfilePicture donald = new ProfilePicture("Donald","guest", false);

        ArrayList<ProfilePicture> users = new ArrayList<>();
        users.add(tom);
        users.add(jerry);
        users.add(donald);

        // android.R.layout.simple_list_item_1 is a constant predefined layout of Android.
        // used to create a ListView with simple ListItem (Only one TextView).
        ArrayAdapter<ProfilePicture> arrayAdapter
                = new ArrayAdapter<ProfilePicture>(this, android.R.layout.simple_list_item_1 , users);

        listView.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

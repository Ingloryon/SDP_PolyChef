package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ProfilePicture;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.ProfilePictureAdapter;

/**
 * The fragment displaying the different profile picture choices to the user.
 */
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

        List<ProfilePicture> imageDetails = getListData();

        listView.setAdapter(new ProfilePictureAdapter(getContext(), imageDetails));

        // When the user clicks on the ListItem
        listView.setOnItemClickListener((adaptor, view1, position, id) -> {
            User updatedUser=hostActivity.getUserStorage().getPolyChefUser();
            updatedUser.setProfilePictureId(position);
            hostActivity.setupProfilePicture();
            hostActivity.getUserStorage().updateUserInfo(updatedUser);
            ProfilePictChoice.this.requireActivity().onBackPressed();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private  List<ProfilePicture> getListData() {
        List<ProfilePicture> list = new ArrayList<>();

        String[] photoNames=getResources().getStringArray(R.array.profilePicturesNames);
        String[] photoLabels=getResources().getStringArray(R.array.profilePicturesLabels);
        for(int i=0;i<photoNames.length;i++){
            list.add(new ProfilePicture(photoLabels[i], photoNames[i]));
        }

        return list;
    }


}

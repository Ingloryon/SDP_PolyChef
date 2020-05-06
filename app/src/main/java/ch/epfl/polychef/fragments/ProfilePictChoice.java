package ch.epfl.polychef.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ProfilePicture;
import ch.epfl.polychef.pages.HomePage;
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

        List<ProfilePicture> image_details = getListData();

        listView.setAdapter(new ProfilePictureAdapter(getContext(), image_details));

        // When the user clicks on the ListItem
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                ProfilePicture picture = (ProfilePicture) o;
                Toast.makeText(getActivity(), "Selected :" + " " + picture, Toast.LENGTH_LONG).show();


                //TODO: Send info to the database so the User profile picture gets uploaded
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private  List<ProfilePicture> getListData() {
        List<ProfilePicture> list = new ArrayList<ProfilePicture>();
        list.add(new ProfilePicture("A real life boiii", "boy"));
        list.add(new ProfilePicture("An outstanding girl", "girl"));
        list.add(new ProfilePicture("An awesome man", "man1"));
        list.add(new ProfilePicture("An old fella", "man2"));
        list.add(new ProfilePicture("A cutie grand-mother", "man3"));
        list.add(new ProfilePicture("The boss of the kitchen", "man4"));
        list.add(new ProfilePicture("An healthy man", "boy1"));

        return list;
    }

}

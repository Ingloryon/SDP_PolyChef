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
                ProfilePicture country = (ProfilePicture) o;
                Toast.makeText(getActivity(), "Selected :" + " " + country, Toast.LENGTH_LONG).show();
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
        ProfilePicture vietnam = new ProfilePicture("Vietnam", "vn", 98000000);
        ProfilePicture usa = new ProfilePicture("United States", "us", 320000000);
        ProfilePicture russia = new ProfilePicture("Russia", "ru", 142000000);


        list.add(vietnam);
        list.add(usa);
        list.add(russia);

        return list;
    }

}

package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.UserMiniatureAdapter;

/**
 * Represent a fragment with a list of {@code User} to show the miniature.
 */
@SuppressWarnings("WeakerAccess")
public class UserListFragment extends Fragment {

    private RecyclerView usersRecyclerView;
    private List<User> dynamicUserList = new ArrayList<>();
    private UserStorage userStorage;
    private HomePage hostActivity;

    private final Function<User, List<String>> userListFunction;
    private final int fragmentId;


    /**
     * Constructs a UserListFragment.
     * @param userListFunction the function that maps a user to its corresponding strings
     * @param fragmentId the id of the fragment
     */
    public UserListFragment(Function<User, List<String>> userListFunction, int fragmentId) {
        this.userListFunction = userListFunction;
        this.fragmentId = fragmentId;
    }

    /**
     * Gets the recycler view of the users.
     * @return the recycler view of the users
     */
    public RecyclerView getRecyclerView() {
        return usersRecyclerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(fragmentId, container, false);

        usersRecyclerView = view.findViewById(R.id.miniatureUserList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        UserMiniatureAdapter adapter = new UserMiniatureAdapter(this.getActivity(),
                dynamicUserList, usersRecyclerView);

        usersRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Preconditions.checkArgument(context instanceof HomePage, "The favourite miniature fragment wasn't attached properly!");
        hostActivity = (HomePage) context;
        ImageStorage imageStorage = hostActivity.getImageStorage();
        userStorage = hostActivity.getUserStorage();
        Preconditions.checkArgument(imageStorage != null && userStorage != null, "One of the storage is null !");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!hostActivity.isOnline()){
            Toast.makeText(hostActivity, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
        }

        dynamicUserList.clear();
        MultipleCallHandler<User> multipleCallHandler = new MultipleCallHandler<>(userListFunction.apply(userStorage.getPolyChefUser()).size(), (dataList) -> {
            dynamicUserList.addAll(dataList);
            Objects.requireNonNull(usersRecyclerView.getAdapter()).notifyDataSetChanged();
        });
        for(String userUuid: userListFunction.apply(userStorage.getPolyChefUser())) {
            userStorage.getUserByEmail(userUuid, multipleCallHandler);
        }
    }

}

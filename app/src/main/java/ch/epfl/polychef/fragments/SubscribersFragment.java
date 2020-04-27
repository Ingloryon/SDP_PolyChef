package ch.epfl.polychef.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.UserMiniatureAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscribersFragment extends Fragment {

    private RecyclerView usersRecyclerView;
    private List<User> dynamicUserList = new ArrayList<>();

    private ImageStorage imageStorage;
    private UserStorage userStorage;

    /**
     * Required empty public constructor.
     */
    public SubscribersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribers, container, false);

        usersRecyclerView = view.findViewById(R.id.miniatureUserList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        UserMiniatureAdapter adapter = new UserMiniatureAdapter(this.getActivity(),
                dynamicUserList, usersRecyclerView, container.getId(), imageStorage, userStorage);

        usersRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Preconditions.checkArgument(context instanceof HomePage, "The favourite miniature fragment wasn't attached properly!");
        HomePage homePage = (HomePage) context;
        imageStorage = homePage.getImageStorage();
        userStorage = homePage.getUserStorage();
        Preconditions.checkArgument(imageStorage != null && userStorage != null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dynamicUserList.clear();
        MultipleCallHandler<User> multipleCallHandler = new MultipleCallHandler<>(userStorage.getPolyChefUser().getSubscribers().size(), (dataList) -> {
            dynamicUserList.addAll(dataList);
            usersRecyclerView.getAdapter().notifyDataSetChanged();
        });
        for(String userUuid: userStorage.getPolyChefUser().getSubscribers()) {
            userStorage.getUserByEmail(userUuid, multipleCallHandler);
        }
    }
}

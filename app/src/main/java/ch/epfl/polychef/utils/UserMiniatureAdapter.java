package ch.epfl.polychef.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.users.User;

/**
 * This class is an adapter that take a list of users and update the fields of each user inside the miniature list in the recyclerView that is in the activity where the miniatures are shown.
 */
public class UserMiniatureAdapter extends RecyclerView.Adapter<UserMiniatureAdapter.MiniatureViewHolder>  {
    private final Context mainContext;
    private final List<User> userList;
    private final RecyclerView recyclerview;

    /**
     * Constructs a UserMiniatureAdapter of a list of users.
     * @param mainContext the context of the corresponding activity
     * @param userList the list of user miniatures to display
     * @param recyclerView the recyclerView that is in the activity where the user miniatures are shown
     */
    public UserMiniatureAdapter(Context mainContext, List<User> userList, RecyclerView recyclerView) {
        this.mainContext = mainContext;
        this.userList = userList;
        this.recyclerview = recyclerView;
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.user_miniature_layout, null);
        view.setOnClickListener(new UserMiniatureAdapter.MiniatureOnClickListener(recyclerview));
        return new UserMiniatureAdapter.MiniatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.imageView.setImageResource(User.getResourceImageFromUser(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView email;
        ImageView imageView;

        MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.profilePicture);
            email = itemView.findViewById(R.id.userEmail);
        }
    }

    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {

            // Get the clicked user from the recyclerView
            int userPosition = recyclerView.getChildLayoutPosition(view);
            User clickedUser = userList.get(userPosition);
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", clickedUser);

            ((HomePage) mainContext)
                    .getNavController()
                    .navigate(R.id.userProfileFragment, bundle);
        }
    }
}

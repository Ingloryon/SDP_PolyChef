package ch.epfl.polychef.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class UserMiniatureAdapter extends RecyclerView.Adapter<UserMiniatureAdapter.MiniatureViewHolder>  {

    private final Context mainContext;
    private final int fragmentContainerID;
    private final List<User> userList;
    private final RecyclerView recyclerview;
    private final ImageStorage imageStorage;
    private final UserStorage userStorage;
    private final HashMap<String, Bitmap> images;

    public UserMiniatureAdapter(Context mainContext, List<User> userList, RecyclerView recyclerView, int fragmentContainerID, ImageStorage storage, UserStorage userStorage) {
        this.mainContext = mainContext;
        this.userList = userList;
        this.recyclerview = recyclerView;
        this.fragmentContainerID = fragmentContainerID;
        this.imageStorage = storage;
        this.userStorage = userStorage;
        this.images = new HashMap<>();
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View view = inflater.inflate(R.layout.user_miniature_layout, null);
        view.setOnClickListener(new UserMiniatureAdapter.MiniatureOnClickListener(recyclerview));
        return new UserMiniatureAdapter.MiniatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MiniatureViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView imageView;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.profilePicture);
        }
    }

    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        public MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {

//            //Here we know that the context is an activity
//            AppCompatActivity activity = (AppCompatActivity) mainContext;
//            FragmentManager fragMana = activity.getSupportFragmentManager();
//
//            // Get the clicked recipe from the recyclerView
//            int recipePosition = recyclerView.getChildLayoutPosition(view);
//            Recipe clickedRecipe = recipeList.get(recipePosition);
//
//            //Create new Bundle to store recipe object inside the recipe fragment that will be open after
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("Recipe", clickedRecipe);
//
//            FullRecipeFragment recipeFragment = new FullRecipeFragment();
//            recipeFragment.setArguments(bundle);
//
//            fragMana.beginTransaction().replace(fragmentContainerID, recipeFragment).addToBackStack(null).commit();
        }
    }
}

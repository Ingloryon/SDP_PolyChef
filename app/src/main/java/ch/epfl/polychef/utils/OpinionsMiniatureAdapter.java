package ch.epfl.polychef.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Opinion;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class OpinionsMiniatureAdapter extends RecyclerView.Adapter<OpinionsMiniatureAdapter.MiniatureViewHolder>
{

    private Context mainContext;
    private RecyclerView recyclerView;
    private HashMap<Opinion, User> userOp;
    List<Opinion> displayedOpinions;
    List<Opinion> allOpinions;
    private int currentIndex = 0;
    public static final int nbOfOpinionsLoadedAtATime = 5;
    private Recipe recipe;
    UserStorage userStorage;
    private boolean isLoading = false;

    public OpinionsMiniatureAdapter(Context mainContext, RecyclerView recyclerView, Recipe recipe, UserStorage userStorage){
        this.mainContext = mainContext;
        this.recyclerView = recyclerView;
        this.displayedOpinions = new ArrayList<>();
        this.userOp = new HashMap<>();
        allOpinions = new ArrayList<>();
        for(Opinion opinion : recipe.getRating().getAllOpinion().values()){
            allOpinions.add(opinion);
        }
        this.recipe = recipe;
        this.userStorage = userStorage;
    }

    public boolean isLoading(){
        return isLoading;
    }

    public int getNbOfOpinionsLoadedAtATime(){
        return nbOfOpinionsLoadedAtATime;
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View view = inflater.inflate(R.layout.comment_layout, null);
        view.setOnClickListener(new OpinionsMiniatureAdapter.MiniatureOnClickListener(recyclerView));
        return new OpinionsMiniatureAdapter.MiniatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        Opinion opinion = displayedOpinions.get(position);
        User user = userOp.get(opinion);
        holder.rate.setRating(opinion.getRate());
        holder.commentText.setText(opinion.getComment());
        holder.commentUsername.setText(user.getUsername());
        int imageID = User.getResourceImageFromUser(user);
        holder.profilePict.setImageResource(imageID);
    }

    @Override
    public int getItemCount() {
        return displayedOpinions.size();
    }

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        RatingBar rate;
        ImageView profilePict;
        TextView commentText;
        TextView commentUsername;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            rate = itemView.findViewById(R.id.ratingCommentBar);
            profilePict = itemView.findViewById(R.id.profilePicture);
            commentText = itemView.findViewById(R.id.commentText);
            commentUsername = itemView.findViewById(R.id.commentUsername);
        }
    }

    /**
     * This class is the listener that when we click on a miniature it swaps to the new fragment with the full recipe displayed.
     */
    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        public MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {
            // Get the clicked user from the recyclerView
            int opinionPosition = recyclerView.getChildLayoutPosition(view);
            Opinion clickedOpinion = allOpinions.get(opinionPosition);
            User clickedUser = userOp.get(clickedOpinion);
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", clickedUser);

            ((HomePage) mainContext)
                    .getNavController()
                    .navigate(R.id.userProfileFragment, bundle);
        }
    }

    public void loadNewComments(){
        isLoading = true;
        MultipleCallHandler<User> multipleCallHandler = new MultipleCallHandler<>(Math.min(nbOfOpinionsLoadedAtATime, allOpinions.size() - currentIndex), (newUser) -> {
            isLoading = false;
            for(int i = 0; i < newUser.size(); i++){
                userOp.put(allOpinions.get(currentIndex), newUser.get(i));
                displayedOpinions.add(allOpinions.get(currentIndex));
                currentIndex += 1;
            }
            notifyDataSetChanged();
        });
        for(int i = currentIndex; i < Math.min(currentIndex + nbOfOpinionsLoadedAtATime, allOpinions.size()); i++){
            userStorage.getUserByID(recipe.getRating().getUserIdFromOpinion(allOpinions.get(i)), multipleCallHandler);
        }
    }

    public List<Opinion> getDisplayedOpinions(){
        return displayedOpinions;
    }

    public HashMap<Opinion, User>  getMap(){
        return userOp;
    }
}

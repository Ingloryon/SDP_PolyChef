package ch.epfl.polychef.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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

import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Opinion;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * A miniature adapter to display the opinions given by users on Recipes.
 */
public class OpinionsMiniatureAdapter extends RecyclerView.Adapter<OpinionsMiniatureAdapter.MiniatureViewHolder> {

    private static final int NB_OF_OPINIONS_LOADED_AT_A_TIME = 5;
    private Context mainContext;
    private RecyclerView recyclerView;
    private HashMap<Opinion, User> userOp;
    private List<Opinion> displayedOpinions;
    private List<Opinion> allOpinions;
    private int currentIndex = 0;
    private Recipe recipe;
    private UserStorage userStorage;
    private boolean isLoading = false;

    /**
     * Constructs an opinion adapter.
     * @param mainContext the context
     * @param recyclerView the recycler view
     * @param recipe the concerned recipe
     * @param userStorage the corresponding user storage
     */
    public OpinionsMiniatureAdapter(Context mainContext, RecyclerView recyclerView, Recipe recipe, UserStorage userStorage){
        this.mainContext = mainContext;
        this.recyclerView = recyclerView;
        this.displayedOpinions = new ArrayList<>();
        this.userOp = new HashMap<>();
        allOpinions = new ArrayList<>();
        allOpinions.addAll(recipe.getRating().getAllOpinion().values());
        this.recipe = recipe;
        this.userStorage = userStorage;
    }

    /**
     * Tells whether the adapter is loading.
     * @return whether the adapter is loading
     */
    public boolean isLoading(){
        return isLoading;
    }

    /**
     * Loads the newly posted comments.
     */
    public void loadNewComments(){
        isLoading = true;
        MultipleCallHandler<User> multipleCallHandler = new MultipleCallHandler<>(Math.min(NB_OF_OPINIONS_LOADED_AT_A_TIME, allOpinions.size() - currentIndex), (newUser) -> {
            isLoading = false;
            for(int i = 0; i < newUser.size(); i++){
                userOp.put(allOpinions.get(currentIndex), newUser.get(i));
                displayedOpinions.add(allOpinions.get(currentIndex));
                currentIndex += 1;
            }
            notifyDataSetChanged();
        });
        for(int i = currentIndex; i < Math.min(currentIndex + NB_OF_OPINIONS_LOADED_AT_A_TIME, allOpinions.size()); i++){
            userStorage.getUserByID(recipe.getRating().getUserIdFromOpinion(allOpinions.get(i)), multipleCallHandler);
        }
    }

    /**
     * Gets the list of displayed opinions.
     * @return the list of displayed opinions
     */
    public List<Opinion> getDisplayedOpinions(){
        return displayedOpinions;
    }

    /**
     * Gets the mapping between Opinions and corresponding User.
     * @return the map between opinion and user
     */
    public HashMap<Opinion, User> getOpinionToUserMap(){
        return userOp;
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.comment_layout, null);
        view.setOnClickListener(new OpinionsMiniatureAdapter.MiniatureOnClickListener(recyclerView));
        return new OpinionsMiniatureAdapter.MiniatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        Opinion opinion = displayedOpinions.get(position);
        User user = userOp.get(opinion);
        holder.ratingBar.setRate(opinion.getRate());
        holder.commentText.setText(opinion.getComment());
        if(user != null) {
            holder.commentUsername.setText(user.getUsername());
            int imageID = User.getResourceImageFromUser(user);
            holder.profilePict.setImageResource(imageID);
        }
    }

    @Override
    public int getItemCount() {
        return displayedOpinions.size();
    }

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePict;
        TextView commentText;
        TextView commentUsername;
        CustomRatingBar ratingBar;

        MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePict = itemView.findViewById(R.id.profilePicture);
            commentText = itemView.findViewById(R.id.commentText);
            commentUsername = itemView.findViewById(R.id.commentUsername);
            ratingBar = new CustomRatingBar(itemView.findViewById(R.id.ratingCommentBar), R.drawable.spatuladoree, R.drawable.spatuladoreehalf, R.drawable.spatulagray, false);
        }
    }

    /**
     * This class is the listener that when we click on a miniature it swaps to the new fragment with the full recipe displayed.
     */
    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        MiniatureOnClickListener(RecyclerView recyclerView) {
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
}

package ch.epfl.polychef.utils;

import android.content.Context;
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
import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Opinion;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class OpinionsMiniatureAdapter extends RecyclerView.Adapter<OpinionsMiniatureAdapter.MiniatureViewHolder> implements CallHandler<User>
{

    private Context mainContext;
    private RecyclerView recyclerView;
    private HashMap<Opinion, User> userOp;
    List<Opinion> displayedOpinions;
    List<Opinion> allOpinions;
    private int currentIndex = 0;
    public static final int nbOfOpinionsLoadedAtATime = 5;
    private boolean isLoading = false;
    private Recipe recipe;

    public OpinionsMiniatureAdapter(Context mainContext, RecyclerView recyclerView, Recipe recipe, UserStorage userStorage){
        this.mainContext = mainContext;
        this.recyclerView = recyclerView;
        this.displayedOpinions = new ArrayList<>();
        this.userOp = new HashMap<>();
        for(Opinion opinion : recipe.getRating().getAllOpinion().values()){
            allOpinions.add(opinion);
        }
        this.recipe = recipe;
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
        holder.rate.setRating(opinion.getRate());
        holder.commentText.setText(opinion.getComment());
        holder.commentUsername.setText(userOp.get(opinion).getUsername());
        holder.profilePict.setImageResource(userOp.get(opinion).getProfilePictureId());
    }

    @Override
    public int getItemCount() {
        return displayedOpinions.size();
    }

    @Override
    public void onSuccess(User user) {

    }

    @Override
    public void onFailure() {

    }

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        RatingBar rate;
        ImageView profilePict;
        TextView commentText;
        TextView commentUsername;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            rate = itemView.findViewById(R.id.ratingCommentBar);
            profilePict = itemView.findViewById(R.id.commentProfilePict);
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
            // Go to user page
        }
    }

    public void loadNewComments(){
        for(int i = currentIndex; i < Math.min(currentIndex + nbOfOpinionsLoadedAtATime, allOpinions.size()); i++){
            
        }
    }
}

package ch.epfl.polychef.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polychef.R;

import java.util.List;
/**
 * This class is a adapter that take a list of recipes and update the fields of each miniature inside the miniature list in the recyclerView  in the activity where the miniatures are shown
 */
public class RecipeMiniatureAdapter extends RecyclerView.Adapter<RecipeMiniatureAdapter.MiniatureViewHolder>{

    private Context mainContext;
    private List<Recipe> recipeList;

    public RecipeMiniatureAdapter(Context mainContext, List<Recipe> recipeList) {
        this.mainContext = mainContext;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View view = inflater.inflate(R.layout.miniature_layout, null);
        return new MiniatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getName());
        holder.ratingBar.setRating((float) recipe.getRating().ratingAverage());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    class MiniatureViewHolder extends RecyclerView.ViewHolder{

        TextView recipeTitle;
        RatingBar ratingBar;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeNameMiniature);
            ratingBar = itemView.findViewById(R.id.miniatureRatingBar);
        }
    }


}

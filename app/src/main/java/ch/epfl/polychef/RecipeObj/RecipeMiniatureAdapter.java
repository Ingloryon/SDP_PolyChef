package ch.epfl.polychef.RecipeObj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polychef.R;

public class RecipeMiniatureAdapter extends RecyclerView.Adapter<RecipeMiniatureAdapter.MiniatureViewHolder>{

    private Context mCtx;
    private List<Recipe> recipeList;

    public RecipeMiniatureAdapter(Context mCtx, List<Recipe> recipeList) {
        this.mCtx = mCtx;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
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

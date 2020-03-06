package ch.epfl.polychef.recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polychef.R;
import ch.epfl.polychef.RecipeActivity;

/**
 * This class is an adapter that take a list of recipes and update the fields of each miniature inside the miniature list in the recyclerView that is in the activity where the miniatures are shown.
 */
public class RecipeMiniatureAdapter extends RecyclerView.Adapter<RecipeMiniatureAdapter.MiniatureViewHolder> {

    private Context mainContext;
    private List<Recipe> recipeList;
    private RecyclerView recyclerview;

    /**
     * Creates a new adapter of recipes to miniatures.
     *
     * @param mainContext  the context where the adapter will operate i.e the activity where the recyclerView is
     * @param recipeList   the list of all the recipes that will be displayed inside the recyclerView
     * @param recyclerView this is the recyclerview where the recipes will be displayed
     */
    public RecipeMiniatureAdapter(Context mainContext, List<Recipe> recipeList, RecyclerView recyclerView) {
        this.mainContext = mainContext;
        this.recipeList = recipeList;
        this.recyclerview = recyclerView;
    }

    /**
     * This method create a new MiniatureViewHolder which contains the view which contains the information of the layout of one miniature and make that view listen to user clicks on him.
     *
     * @param parent   not used here but needed since it's an overridden method
     * @param viewType not used here but needed since it's an overridden method
     * @return the new MiniatureViewHolder containing the view
     */
    @NonNull
    @Override
    public MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View view = inflater.inflate(R.layout.miniature_layout, null);
        view.setOnClickListener(new MiniatureOnClickListener(recyclerview));
        return new MiniatureViewHolder(view);
    }

    /**
     * Fill the view with the field of one recipe in the recipe list.
     *
     * @param holder   the MiniatureViewHolder that we need to bind the recipe with
     * @param position the position in the miniature list, this is the position where the miniature will be displayed relatively to the other ones inside the recyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull MiniatureViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getName());
        holder.ratingBar.setRating((float) recipe.getRating().ratingAverage());
        // TODO change to the selected image by the cooker who posted the recipe
        holder.image.setImageResource(recipe.getPicturesNumbers().get(0));
    }

    /**
     * Return the size of the list of the recipes.
     *
     * @return the size of the list of the recipes
     */
    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    /**
     * This is the MiniatureViewHolder that contains the fields of one miniature to be filled when binned to one recipe in the list.
     */
    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        TextView recipeTitle;
        ImageView image;
        RatingBar ratingBar;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeNameMiniature);
            ratingBar = itemView.findViewById(R.id.miniatureRatingBar);
            image = itemView.findViewById(R.id.miniatureRecipeImage);
        }
    }

    /**
     * This class is the listener that when we click on a miniature it send us to a new activity with the full recipe displayed.
     */
    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        public MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {
            int recipePosition = recyclerView.getChildLayoutPosition(view);
            Recipe clickedRecipe = recipeList.get(recipePosition);
            Intent intent = new Intent(mainContext, RecipeActivity.class);
            intent.putExtra("Recipe", clickedRecipe);
            mainContext.startActivity(intent);
        }
    }

}

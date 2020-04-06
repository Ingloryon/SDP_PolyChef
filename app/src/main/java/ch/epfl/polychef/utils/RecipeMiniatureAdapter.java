package ch.epfl.polychef.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.fragments.FullRecipeFragment;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.recipe.Recipe;

import java.util.List;

/**
 * This class is an adapter that take a list of recipes and update the fields of each miniature inside the miniature list in the recyclerView that is in the activity where the miniatures are shown.
 */
public class RecipeMiniatureAdapter extends RecyclerView.Adapter<RecipeMiniatureAdapter.MiniatureViewHolder> {

    private Context mainContext;
    private List<Recipe> recipeList;
    private RecyclerView recyclerview;
    private int fragmentContainerID;

    /**
     * Creates a new adapter of recipes to miniatures.
     *
     * @param mainContext  the context where the adapter will operate i.e the activity where the recyclerView is
     * @param recipeList   the list of all the recipes that will be displayed inside the recyclerView
     * @param recyclerView this is the recyclerView where the recipes will be displayed
     * @param fragmentContainerID the id of the fragment container where the miniature are displayed
     */
    public RecipeMiniatureAdapter(Context mainContext, List<Recipe> recipeList, RecyclerView recyclerView, int fragmentContainerID) {
        this.mainContext = mainContext;
        this.recipeList = recipeList;
        this.recyclerview = recyclerView;
        this.fragmentContainerID = fragmentContainerID;
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
        Either<String, Integer> miniatureMeta = recipe.getMiniaturePath();
        if(miniatureMeta.isNone()) {
            holder.image.setImageResource(Recipe.DEFAULT_MINIATURE_PATH);
        } else if(miniatureMeta.isRight()) {
            holder.image.setImageResource(miniatureMeta.getRight());
        } else {
            new ImageStorage().getImage(miniatureMeta.getLeft(), new CallHandler<byte[]>() {
                @Override
                public void onSuccess(byte[] data) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    holder.image.setImageBitmap(bmp);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(mainContext, mainContext.getString(R.string.errorImageRetrieve), Toast.LENGTH_LONG).show();
                }
            });
        }
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
     * This class is the listener that when we click on a miniature it swaps to the new fragment with the full recipe displayed.
     */
    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        public MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {

            // TODO: Bug on nav to fix (in progress)
/*
            // To fix problem of offlineRecipe can dirtily add a if else with old case
            //Here we know that the context is an activity
            AppCompatActivity activity = (AppCompatActivity) mainContext;
            FragmentManager fragmentManager = activity.getSupportFragmentManager();

            NavHostFragment hostFragment = (NavHostFragment)
                    fragmentManager.findFragmentById(R.id.nav_host_fragment);

            NavController navController = NavHostFragment.findNavController(hostFragment);

            // Get the clicked recipe from the recyclerView
            int recipePosition = recyclerView.getChildLayoutPosition(view);
            Recipe clickedRecipe = recipeList.get(recipePosition);

            // Create new Bundle containing the id of the container for the adapter
            Bundle bundle = new Bundle();
            bundle.putSerializable("Recipe", clickedRecipe);
            FullRecipeFragment recipeFragment = new FullRecipeFragment();
            recipeFragment.setArguments(bundle);

            // Set this bundle to be an arguments of the startDestination using this trick
            navController.setGraph(R.navigation.nav_graph, bundle);

            navController.navigate(R.id.fullRecipeFragment, bundle);

            //fragmentManager.beginTransaction().addToBackStack(null).commit();*/


            //Here we know that the context is an activity
            AppCompatActivity activity = (AppCompatActivity) mainContext;
            FragmentManager fragMana = activity.getSupportFragmentManager();

            // Get the clicked recipe from the recyclerView
            int recipePosition = recyclerView.getChildLayoutPosition(view);
            Recipe clickedRecipe = recipeList.get(recipePosition);

            //Create new Bundle to store recipe object inside the recipe fragment that will be open after
            Bundle bundle = new Bundle();
            bundle.putSerializable("Recipe", clickedRecipe);

            FullRecipeFragment recipeFragment = new FullRecipeFragment();
            recipeFragment.setArguments(bundle);

            fragMana.beginTransaction().replace(fragmentContainerID, recipeFragment).addToBackStack(null).commit();
        }
    }

}

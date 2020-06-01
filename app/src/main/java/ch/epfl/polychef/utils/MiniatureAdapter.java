package ch.epfl.polychef.utils;

import android.annotation.SuppressLint;
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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * The miniature adapter to display the different miniatures.
 */
public class MiniatureAdapter extends RecyclerView.Adapter<MiniatureAdapter.MiniatureViewHolder>{

    private static final int RECIPE_TYPE=0;
    private static final int USER_TYPE=1;

    private Context mainContext;
    private List<Miniatures> miniaturesList;
    private RecyclerView recyclerview;

    private ImageStorage imageStorage;
    private UserStorage userStorage;

    /**
     * Constructs a miniature adapter with the given arguments.
     * @param mainContext the main context
     * @param miniaturesList the list of the Miniatures
     * @param recyclerView the recycler view where we display
     * @param storage the image storage
     * @param userStorage the user storage
     */
    public MiniatureAdapter(Context mainContext, List<Miniatures> miniaturesList, RecyclerView recyclerView, ImageStorage storage, UserStorage userStorage) {
        this.mainContext = mainContext;
        this.miniaturesList = miniaturesList;
        this.recyclerview = recyclerView;
        this.imageStorage = storage;
        this.userStorage = userStorage;
    }

    /**
     * Sets a new list of Miniatures to the MiniatureAdapter.
     * @param miniatures the new list of Miniatures
     */
    public void changeList(List<Miniatures> miniatures){
        this.miniaturesList = miniatures;
        notifyDataSetChanged();
    }

    /**
     * Gets the image storage of the adapter.
     * @return the image storage
     */
    public ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureAdapter.MiniatureViewHolder holder, int position) {
        if(miniaturesList.get(position).isRecipe()) {
            Recipe recipe = (Recipe) miniaturesList.get(position);
            holder.recipeTitle.setText(recipe.getName());
            holder.ratingBar.setRate((float) recipe.getRating().ratingAverage());
            FavouritesUtils.getInstance().setFavouriteButton(userStorage, holder.favouriteButton, recipe);
            getImageFor(holder, recipe);
        }else if(miniaturesList.get(position).isUser()){
            User user = (User) miniaturesList.get(position);
            holder.username.setText(user.getUsername());
            holder.imageView.setImageResource(User.getResourceImageFromUser(user));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(miniaturesList.get(position).isUser()) {
            return USER_TYPE;
        }
        else {
            return RECIPE_TYPE;
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MiniatureAdapter.MiniatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainContext);
        View view;
        if(viewType==RECIPE_TYPE) {
            view = inflater.inflate(R.layout.miniature_layout, null);
        }
        else {
            view = inflater.inflate(R.layout.user_miniature_layout, null);
        }
        view.setOnClickListener(new MiniatureAdapter.MiniatureOnClickListener(recyclerview));
        return new MiniatureAdapter.MiniatureViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return miniaturesList.size();
    }

    private void getImageFor(MiniatureAdapter.MiniatureViewHolder holder, Recipe recipe) {
        Either<String, Integer> miniatureMeta = recipe.getMiniaturePath();
        if(miniatureMeta.isNone()) {
            holder.image.setImageResource(Recipe.DEFAULT_MINIATURE_PATH);
        } else if(miniatureMeta.isRight()) {
            holder.image.setImageResource(miniatureMeta.getRight());
        } else {
            getImageStorage().getImage(miniatureMeta.getLeft(), new CallHandler<byte[]>() {
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

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        TextView recipeTitle;
        ImageView image;
        CustomRatingBar ratingBar;
        ToggleButton favouriteButton;

        TextView username;
        ImageView imageView;

        MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeNameMiniature);
            ratingBar = new CustomRatingBar(itemView.findViewById(R.id.miniatureRatingBar), R.drawable.spatuladoree, R.drawable.spatuladoreehalf, R.drawable.spatulagray, false);
            image = itemView.findViewById(R.id.miniatureRecipeImage);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
            username = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.profilePicture);
        }
    }

    class MiniatureOnClickListener implements View.OnClickListener {

        RecyclerView recyclerView;

        MiniatureOnClickListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onClick(View view) {

            int targetFragment;

            Bundle bundle = new Bundle();

            int position = recyclerView.getChildLayoutPosition(view);

            if (miniaturesList.get(position).isRecipe()) {
                targetFragment = R.id.fullRecipeFragment;

                Recipe clickedRecipe = (Recipe) miniaturesList.get(position);
                bundle.putSerializable("Recipe", clickedRecipe);

            } else {
                targetFragment = R.id.userProfileFragment;

                User clickedUser = (User) miniaturesList.get(position);
                bundle.putSerializable("User", clickedUser);
            }

            ((HomePage) mainContext)
                    .getNavController()
                    .navigate(targetFragment, bundle);
        }
    }
}

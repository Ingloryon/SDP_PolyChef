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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.fragments.FullRecipeFragment;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class MiniatureAdapter extends RecyclerView.Adapter<MiniatureAdapter.MiniatureViewHolder>{

    private static final int RECIPE_TYPE=0;
    private static final int USER_TYPE=1;

    private Context mainContext;
    private List<Miniatures> miniaturesList;
    private RecyclerView recyclerview;
    private int fragmentContainerID;

    private ImageStorage imageStorage;// = new ImageStorage();
    private UserStorage userStorage;

    private Map<String, Bitmap> images;

    public MiniatureAdapter(Context mainContext, List<Miniatures> miniaturesList, RecyclerView recyclerView, int fragmentContainerID, ImageStorage storage) {
        this(mainContext, miniaturesList, recyclerView, fragmentContainerID, storage, null);
    }

    public MiniatureAdapter(Context mainContext, List<Miniatures> miniaturesList, RecyclerView recyclerView, int fragmentContainerID, ImageStorage storage, UserStorage userStorage) {
        this.mainContext = mainContext;
        this.miniaturesList = miniaturesList;
        this.recyclerview = recyclerView;
        this.fragmentContainerID = fragmentContainerID;
        this.imageStorage = storage;
        this.userStorage = userStorage;
        this.images = new HashMap<>();
    }

    public void changeList(List<Miniatures> miniatures){
        this.miniaturesList = miniatures;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MiniatureAdapter.MiniatureViewHolder holder, int position) {
        if(miniaturesList.get(position).getClass().equals(Recipe.class)) {
            Recipe recipe = (Recipe) miniaturesList.get(position);
            holder.recipeTitle.setText(recipe.getName());
            holder.ratingBar.setRating((float) recipe.getRating().ratingAverage());
            FavouritesUtils.getInstance().setFavouriteButton(userStorage, holder.favouriteButton, recipe);
            if (images.containsKey(recipe.getRecipeUuid())) {
                holder.image.setImageBitmap(images.get(recipe.getRecipeUuid()));
            } else {
                getImageFor(holder, recipe);
            }
        }else if(miniaturesList.get(position).getClass().equals(User.class)){
            User user = (User) miniaturesList.get(position);
            holder.username.setText(user.getUsername());
        }
    }

    private void getImageFor(MiniatureAdapter.MiniatureViewHolder holder, Recipe recipe) {
        Either<String, Integer> miniatureMeta = recipe.getMiniaturePath();
        if(miniatureMeta.isNone()) {
            holder.image.setImageResource(Recipe.DEFAULT_MINIATURE_PATH);
            images.put(recipe.getRecipeUuid(), BitmapFactory.decodeResource(mainContext.getResources(), Recipe.DEFAULT_MINIATURE_PATH));
        } else if(miniatureMeta.isRight()) {
            holder.image.setImageResource(miniatureMeta.getRight());
            images.put(recipe.getRecipeUuid(), BitmapFactory.decodeResource(mainContext.getResources(), miniatureMeta.getRight()));
        } else {
            getImageStorage().getImage(miniatureMeta.getLeft(), new CallHandler<byte[]>() {
                @Override
                public void onSuccess(byte[] data) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    holder.image.setImageBitmap(bmp);
                    images.put(recipe.getRecipeUuid(), bmp);
                }

                @Override
                public void onFailure() {
                    Toast.makeText(mainContext, mainContext.getString(R.string.errorImageRetrieve), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public ImageStorage getImageStorage() {
        return imageStorage;
    }

    @Override
    public int getItemViewType(int position) {
        if(miniaturesList.get(position).getClass().equals(Recipe.class)) {
            return RECIPE_TYPE;
        }
        else {
            return USER_TYPE;
        }
    }

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

    class MiniatureViewHolder extends RecyclerView.ViewHolder {

        TextView recipeTitle;
        ImageView image;
        RatingBar ratingBar;
        ToggleButton favouriteButton;

        TextView username;
        ImageView imageView;

        public MiniatureViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeNameMiniature);
            ratingBar = itemView.findViewById(R.id.miniatureRatingBar);
            image = itemView.findViewById(R.id.miniatureRecipeImage);
            favouriteButton = itemView.findViewById(R.id.favouriteButton);
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

            int position = recyclerView.getChildLayoutPosition(view);
            if (miniaturesList.get(position).getClass().equals(Recipe.class)) {
                Recipe clickedRecipe = (Recipe) miniaturesList.get(position);
                //Create new Bundle to store recipe object inside the recipe fragment that will be open after
                Bundle bundle = new Bundle();
                bundle.putSerializable("Recipe", clickedRecipe);
                ((HomePage) mainContext)
                            .getNavController()
                            .navigate(R.id.fullRecipeFragment, bundle);

            } else if(miniaturesList.get(position).getClass().equals(User.class)){
                User clickedUser = (User) miniaturesList.get(position);
                //Create new Bundle to store user object inside the user fragment that will be open after
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", clickedUser);
                ((HomePage) mainContext)
                        .getNavController()
                        .navigate(R.id.userProfileFragment, bundle);
            }
        }
    }
}
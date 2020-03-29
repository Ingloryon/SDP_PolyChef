package ch.epfl.polychef.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.R;
import ch.epfl.polychef.adaptersrecyclerview.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.recipe.Recipe;

public class OnlineMiniaturesFragment extends Fragment implements CallNotifier<Recipe> {

    private static final String TAG = "OnlineMiniaturesFrag";
    private RecyclerView onlineRecyclerView;

    private Button searchButton;

    private List<Recipe> dynamicRecipeList = new ArrayList<>();

    private int currentReadInt = 1;

    public static final int nbOfRecipesLoadedAtATime = 5;

    private boolean isLoading = false;

    private RecipeStorage recipeStorage;

    public OnlineMiniaturesFragment(){
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

        Bundle bundle = getArguments();
        recipeStorage = (RecipeStorage) bundle.getSerializable("RecipeStorage");

        int fragmentID = bundle.getInt("fragmentID");
        onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
        onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        onlineRecyclerView.setAdapter(new RecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, onlineRecyclerView, fragmentID));
        // Add a scroll listener when we reach the end of the list we load new recipes from database
        onlineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1)){
                    if(isLoading){
                        return;
                    }
                    recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, currentReadInt, OnlineMiniaturesFragment.this);
                    currentReadInt += nbOfRecipesLoadedAtATime;
                    isLoading = true;
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchButton = getView().findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSearchButton(view);
            }
        });
        // For now when we enter the page we load the offline recipes first
        // add a certain number of recipes at this end of the actual list
        recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, 1, this);
        currentReadInt += nbOfRecipesLoadedAtATime;
    }

    @Override
    public void notify(Recipe data) {
        isLoading = false;
        dynamicRecipeList.add(data);
        onlineRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFailure() {
        isLoading = false;
    }

    public RecyclerView getRecyclerView(){
        return onlineRecyclerView;
    }

    private void setSearchButton(View view){
        FirebaseDatabase database=recipeStorage.getFirebaseDatabase();
        DatabaseReference idRef = database.getReference("id");
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value
                int value = dataSnapshot.getValue(Integer.class);
                searchForRecipe(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void searchForRecipe(int id){
        FirebaseDatabase database=recipeStorage.getFirebaseDatabase();
        for(int i=1;i<=id;i++){
            DatabaseReference nameRef = database.getReference("recipe").child(Integer.toString(i)).child("name");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value
                    String value = dataSnapshot.getValue(String.class);
                    compareName(value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    private void compareName(String value){
        String searchInput=((EditText)getView().findViewById(R.id.searchBar)).getText().toString();
        searchInput=searchInput.toLowerCase();
        value=value.toLowerCase();
        if(searchInput.contains(value) || value.contains(searchInput)){
            Log.w(TAG,value+" is a correct search");
        }

    }

}

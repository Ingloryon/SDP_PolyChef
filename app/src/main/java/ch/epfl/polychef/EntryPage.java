package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polychef.adaptersRecyclerView.RecipeMiniatureAdapter;
import ch.epfl.polychef.recipe.OfflineRecipes;

public class EntryPage extends AppCompatActivity {

    private Button logButton;

    public static final String LOG_IN = "Log in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);

        FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
        // Send the fragmentID of the fragment container to the recyclerView adaptater
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentID", R.id.nav_entry_fragment);
        OfflineMiniaturesFragment miniFrag = new OfflineMiniaturesFragment();
        miniFrag.setArguments(bundle);
        // Set the starting fragment inside the container with the miniatures
        fm.add(R.id.nav_entry_fragment, miniFrag);
        fm.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        logButton.setText(LOG_IN);
    }

    /** Called when the user taps the log button. */
    public void login(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}

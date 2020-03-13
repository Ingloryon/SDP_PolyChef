package ch.epfl.polychef.pages;

import android.os.Bundle;

import ch.epfl.polychef.R;
import ch.epfl.polychef.users.ConnectedActivity;

public class PostRecipePage extends ConnectedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_recipe);
    }
}

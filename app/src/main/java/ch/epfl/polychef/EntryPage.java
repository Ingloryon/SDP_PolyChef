package ch.epfl.polychef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;

public class EntryPage extends AppCompatActivity {

    private Toolbar toolbar;
    private Button logButton;

    public static final String LOG_IN = "Log in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.toolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        logButton = findViewById(R.id.logButton);
        logButton.setText(LOG_IN);
    }
}

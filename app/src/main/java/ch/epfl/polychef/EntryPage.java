package ch.epfl.polychef;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        logButton.setText(LOG_IN);
        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login(v);
            }
        });
    }

    /** Called when the user taps the log button. */
    public void login(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}

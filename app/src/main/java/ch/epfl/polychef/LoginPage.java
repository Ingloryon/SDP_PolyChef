package ch.epfl.polychef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginPage extends AppCompatActivity {

    Button tequilaButton;
    Button googleButton;

    public static final String tequila = "Tequila";
    public static final String google = "Google";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        tequilaButton = findViewById(R.id.tequilaButton);
        googleButton = findViewById(R.id.googleButton);
    }

    /** Called when the user taps the log button */
    public void tequilaLogin(View view) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    /** Called when the user taps the log button */
    public void googleLogin(View view) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}

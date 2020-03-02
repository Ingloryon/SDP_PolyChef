package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String USER_NAME = "ch.epfl.polychef.USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendEnteredText(View view) {
        // TODO ONLY FOR TESTING DO NOT COMMIT THIS CHANGE AND REMOVE IT
        //Intent intent = new Intent(this, GreetingActivity.class);
        //EditText editText = findViewById(R.id.mainName);
        //String userName = editText.getText().toString();
        //intent.putExtra(USER_NAME, userName);
        //startActivity(intent);
        Intent intent = new Intent(this, MiniatureTestActivity.class);
        startActivity(intent);
    }
}

package ch.epfl.polychef.users;

        import android.content.Intent;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.firebase.ui.auth.AuthUI;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import ch.epfl.polychef.EntryPage;

/**
 * Class for connected Activity that test on creation if the user is logged in
 * It can also log out a user
 */
public abstract class FakeConnectedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void signOut() {
        startActivity(new Intent(getApplicationContext(), EntryPage.class));
    }
}

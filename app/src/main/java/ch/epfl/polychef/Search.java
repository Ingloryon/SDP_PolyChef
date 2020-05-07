package ch.epfl.polychef;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public abstract class Search<Searched extends Miniatures> {

    protected abstract String getTAG();
    protected abstract Searched getValue(DataSnapshot dataSnapshot);

    protected void search(String query, BiFunction<String, Searched, Boolean> comparator, CallHandler<List<Miniatures>> caller) {
        DatabaseReference nameRef = getDatabase().getReference(UserStorage.DB_NAME);
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<Miniatures> results = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        Searched value = getValue(d);
                        if (comparator.apply(query, value)) {
                            results.add(value);
                        }
                    }
                    caller.onSuccess(results);
                } else {
                    caller.onFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(getTAG(), "Failed to read value.", error.toException());
                caller.onFailure();
            }
        });
    }

    protected abstract FirebaseDatabase getDatabase();
}

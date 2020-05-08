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

import ch.epfl.polychef.users.UserStorage;

public abstract class Search<S extends Miniatures> {

    protected abstract String getTag();

    protected abstract S getValue(DataSnapshot dataSnapshot);

    protected abstract String getDbName();

    protected void search(String query, BiFunction<String, S, Boolean> comparator, CallHandler<List<Miniatures>> caller) {
        DatabaseReference nameRef = getDatabase().getReference(getDbName());
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    List<Miniatures> results = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        S value = getValue(d);
                        if (comparator.apply(query, value)) {
                            value.setKey(d.getKey());
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
                Log.w(getTag(), "Failed to read value.", error.toException());
                caller.onFailure();
            }
        });
    }

    protected abstract FirebaseDatabase getDatabase();
}

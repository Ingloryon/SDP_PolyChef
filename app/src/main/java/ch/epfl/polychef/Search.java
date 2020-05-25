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

/**
 * An abstract class that represents the search in some Miniatures.
 * @param <S> a {@link Miniatures} subclass
 */
public abstract class Search<S extends Miniatures> {

    /**
     * Gets the tag of the Miniature.
     * @return the tag of the Miniature
     */
    protected abstract String getTag();

    /**
     * Gets the miniature from the given data snapshot.
     * @param dataSnapshot the data snapshot
     * @return a miniature
     */
    protected abstract S getValue(DataSnapshot dataSnapshot);

    /**
     * Gets the database name where we search.
     * @return the database name where we search
     */
    protected abstract String getDbName();

    /**
     * Gets the database where we search.
     * @return the database where we search
     */
    protected abstract FirebaseDatabase getDatabase();

    /**
     * The main search method that sets listener for data changes.
     * @param query the searching query
     * @param comparator a given comparator to search with
     * @param caller the caller of the method giving a list of Miniatures
     */
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
}

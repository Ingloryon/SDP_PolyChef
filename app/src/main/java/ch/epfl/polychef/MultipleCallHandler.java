package ch.epfl.polychef;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultipleCallHandler<T> implements CallHandler<T> {
    private final List<T> data;
    private final Consumer<List<T>> doOnLast;
    private int currentIndex = 0;
    private final int numberData;

    public MultipleCallHandler(int numberData, Consumer<List<T>> doOnLast) {
        this.numberData = numberData;
        this.data = new ArrayList<>(numberData);
        this.doOnLast = doOnLast;
    }

    public void onSuccess(T d) {
        data.add(d);
        ++currentIndex;
        if(numberData == currentIndex) {
            doOnLast.accept(data);
        }
    }

    public void onFailure() {
        Log.e("MultipleCallHandler", "Error");
    }
}

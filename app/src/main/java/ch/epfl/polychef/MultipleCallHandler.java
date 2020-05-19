package ch.epfl.polychef;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultipleCallHandler<T> implements CallHandler<T> {
    private final List<T> dataList;
    private final Consumer<List<T>> doOnLast;
    private int currentIndex = 0;
    private final int numberData;

    public MultipleCallHandler(int numberData, Consumer<List<T>> doOnLast) {
        this.numberData = numberData;
        this.dataList = new ArrayList<>(numberData);
        this.doOnLast = doOnLast;
    }

    public void onSuccess(T data) {
        dataList.add(data);
        ++currentIndex;
        if(numberData == currentIndex) {
            doOnLast.accept(dataList);
        }
    }

    public void onFailure() {
        ++currentIndex;
        Log.e("MultipleCallHandler", "Error");
    }
}

package ch.epfl.polychef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represent a handler for multiple back to back call.
 *
 * @param <T> the type of data to handle
 */
public class MultipleCallHandler<T> implements CallHandler<T> {
    private final List<T> dataList;
    private final Consumer<List<T>> doOnLast;
    private int currentIndex = 0;
    private final int numberData;
    private final Consumer<MultipleCallHandler> doOnSingleFailure;
    private final boolean incrementOnFailure;

    /**
     * Create a new {@code MultipleCallHandler}.
     *
     * @param numberData the number of data to retrieve (i.e. the number of times it will be attached)
     * @param doOnLast   the function to accept when all data are retrieved
     * @see MultipleCallHandler#MultipleCallHandler(int, Consumer, Consumer, boolean)
     */
    public MultipleCallHandler(int numberData, Consumer<List<T>> doOnLast) {
        this(numberData, doOnLast, null, true);
    }

    /**
     * Create a new {@code MultipleCallHandler}.
     *
     * @param numberData         the number of data to retrieve (i.e. the number of times it will be attached)
     * @param doOnLast           the function to accept when all data are retrieved
     * @param doOnSingleFailure  the function to accept every time {@link CallHandler#onFailure()} is called on this handler
     * @param incrementOnFailure whether this multiple call handler should consider call to {@link CallHandler#onFailure()} as one more call or not
     */
    public MultipleCallHandler(int numberData, Consumer<List<T>> doOnLast, Consumer<MultipleCallHandler> doOnSingleFailure, boolean incrementOnFailure) {
        this.numberData = numberData;
        this.dataList = new ArrayList<>(numberData);
        this.doOnLast = doOnLast;
        this.doOnSingleFailure = doOnSingleFailure;
        this.incrementOnFailure = incrementOnFailure;
    }

    @Override
    public void onSuccess(T data) {
        dataList.add(data);
        incrementAndCheck();
    }

    @Override
    public void onFailure() {
        if (incrementOnFailure) {
            incrementAndCheck();
        }
        if (doOnSingleFailure != null) {
            doOnSingleFailure.accept(this);
        }
    }

    private void incrementAndCheck() {
        ++currentIndex;
        if (numberData == currentIndex) {
            doOnLast.accept(dataList);
        }
    }
}

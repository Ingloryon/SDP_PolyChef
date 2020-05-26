package ch.epfl.polychef;

/**
 * Interface to notify the caller or signal failure.
 * @param <T> the result type
 */
public interface CallNotifier<T> {

    /**
     * What to do when notifying.
     * @param data the required data
     */
    void notify(T data);

    /**
     * wWat to do when the callee fails.
     */
    void onFailure();
}

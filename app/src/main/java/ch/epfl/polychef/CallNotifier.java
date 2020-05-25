package ch.epfl.polychef;

/**
 * The interface of a CallNotifier, will notify or fail.
 * @param <T> the result type
 */
public interface CallNotifier<T> {

    /**
     * What happens if manages to notify.
     * @param data the required data
     */
    void notify(T data);

    /**
     * What happens if the CallNotifier fails.
     */
    void onFailure();
}

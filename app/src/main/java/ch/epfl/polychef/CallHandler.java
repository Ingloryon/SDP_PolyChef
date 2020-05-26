package ch.epfl.polychef;

/**
 * Interface handling two types of callbacks: Success and Failure.
 * @param <T> the result type
 */
public interface CallHandler<T> {

    /**
     * What to do when the callee is successful.
     * @param data the required data
     */
    void onSuccess(T data);

    /**
     * What to do when the callee fails.
     */
    void onFailure();

}

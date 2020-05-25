package ch.epfl.polychef;

/**
 * The interface of a CallNotifier, will notify or fail.
 * @param <T> the result type
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface CallNotifier<T> {

    /**
     * What happens if manages to notify.
     * @param data the required data
     */
    public void notify(T data);

    /**
     * What happens if the CallNotifier fails.
     */
    public void onFailure();
}

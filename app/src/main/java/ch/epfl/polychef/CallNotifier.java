package ch.epfl.polychef;

public interface CallNotifier<T> {

    public void notify(T data);

    public void onFailure();
}

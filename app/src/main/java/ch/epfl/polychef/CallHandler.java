package ch.epfl.polychef;

public interface CallHandler<T> {

    public void onSuccess(T data);

    public void onFailure();
}

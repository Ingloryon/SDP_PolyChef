package ch.epfl.polychef;

public interface CallHandler {

    public void onSuccess(byte[] bytes);
    public void onFailure();
}

package ch.epfl.polychef.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import ch.epfl.polychef.GlobalApplication;

/**
 * Singleton queue for sending request for sending notification to other user.
 */
public class SingletonQueue {
    private static SingletonQueue INSTANCE;
    private RequestQueue requestQueue;

    private SingletonQueue() {
        requestQueue = getRequestQueue();
    }

    /**
     * Gets the instance of the singleton queue for sending request for sending notification.
     * @return the singleton queue
     */
    public static synchronized SingletonQueue getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingletonQueue();
        }
        return INSTANCE;
    }

    /**
     * Adds a notification request to the singleton queue.
     * @param req the notification request to add
     * @param <T> the type of the request
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(GlobalApplication.getAppContext());
        }
        return requestQueue;
    }
}

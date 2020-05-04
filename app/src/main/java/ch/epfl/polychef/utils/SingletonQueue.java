package ch.epfl.polychef.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import ch.epfl.polychef.GlobalApplication;

/**
 * Singleton queue for sending request for sending notification to other user.
 */
public class SingletonQueue {
    private static SingletonQueue instance;
    private RequestQueue requestQueue;

    private SingletonQueue() {
        requestQueue = getRequestQueue();
    }

    public static synchronized SingletonQueue getInstance() {
        if (instance == null) {
            instance = new SingletonQueue();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(GlobalApplication.getAppContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

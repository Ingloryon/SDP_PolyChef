package ch.epfl.polychef;

import android.app.Application;
import android.content.Context;

/**
 * The global context of the application where all the activities take place.
 */
public class GlobalApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
    }

    /**
     * Gets the application context.
     * @return the application context
     */
    public static Context getAppContext() {
        return appContext;
    }
}

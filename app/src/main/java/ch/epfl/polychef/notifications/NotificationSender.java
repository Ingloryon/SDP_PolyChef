package ch.epfl.polychef.notifications;

import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.utils.Preconditions;
import ch.epfl.polychef.utils.SingletonQueue;

/**
 * Class to send notification to all the users subscribed to a topic.
 */
public class NotificationSender {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String SERVER_KEY = "key=" + "AAAAhNJZ4jI:APA91bH8UkEAkTdPDXg2xsiWH7ur8o2lM6Jvd3HPZ-HOluYk6NqmptQthq4O0lil0RchrbuaqkFAJoA1PUMU41AMuQ8i3gEJhcGI--4kxQPqaaryPXO2euObw8mGM98j9qAfEx3MqNwK";
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String CONTENT_TYPE = "application/json";
    private static final String TAG = "NotificationSender";

    private static final NotificationSender INSTANCE = new NotificationSender();

    private NotificationSender() {
    }

    /**
     * Returns the unique instance of Notification sender.
     * @return the instance of Notification sender
     */
    public static NotificationSender getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the singleton of the queue.
     * @return the singleton of the queue
     */
    @SuppressWarnings("WeakerAccess")
    public SingletonQueue getSingletonQueue() {
        return SingletonQueue.getInstance();
    }

    /**
     * Send a notification to all the users subscribed to the user that posted a recipe.
     *
     * @param userKey  the key of the user
     * @param userName the name of the user
     * @param recipe   the recipe that was posted
     */
    public void sendNewRecipe(String userKey, String userName, Recipe recipe) {
        Preconditions.checkArgument(userKey != null, "User key can not be null");
        Preconditions.checkArgument(userName != null, "User name can not be null");
        Preconditions.checkArgument(recipe != null, "Recipe can not be null");
        try {
            JSONObject notificationBody = new JSONObject();
            notificationBody.put("title", String.format(GlobalApplication.getAppContext().getString(R.string.recipeNotificationTitle), userName));
            notificationBody.put("message", String.format(GlobalApplication.getAppContext().getString(R.string.recipeNotificationMessage), recipe.getName(), userName));
            notificationBody.put("type", "recipe");
            notificationBody.put("recipe", recipe.getRecipeUuid());
            send("recipe_" + userKey, notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
    }

    private void send(String topic, JSONObject notificationBody) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("to", "/topics/" + topic);
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Log.i(TAG, "onResponse: " + response.toString()),
                error -> Log.i(TAG, "onErrorResponse: Didn't work")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", SERVER_KEY);
                params.put("Content-Type", CONTENT_TYPE);
                return params;
            }
        };
        getSingletonQueue().addToRequestQueue(jsonObjectRequest);
    }

}

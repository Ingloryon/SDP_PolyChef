package ch.epfl.polychef.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

/**
 * A subclass of FirebaseMessagingService that handles the notifications at the receiver part.
 */
public class NotificationReceiverService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    /**
     * Required empty public constructor for Firebase.
     */
    public NotificationReceiverService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if(getUserStorage().getPolyChefUser() != null) {
            MultipleCallHandler<User> multipleCallHandler = new MultipleCallHandler<>(getUserStorage().getPolyChefUser().getSubscriptions().size(),this::doOnSuccess);
            for(String userEmail: getUserStorage().getPolyChefUser().getSubscriptions()) {
                getUserStorage().getUserByEmail(userEmail, multipleCallHandler);
            }
        }
    }

    private void doOnSuccess(List<User> dataList) {
        for(User user: dataList) {
            getFirebaseMessaging().subscribeToTopic("recipe_"+user.getKey());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(Objects.equals(remoteMessage.getData().get("type"), "recipe")) {
            getRecipeStorage().readRecipeFromUuid(Objects.requireNonNull(remoteMessage.getData().get("recipe")), new CallHandler<Recipe>() {
                @Override
                public void onSuccess(Recipe data) {
                    Intent intent = new Intent(NotificationReceiverService.this.getContext(), HomePage.class);
                    intent.putExtra("RecipeToSend", data);
                    getNotificationUtils().setNotificationWithIntent(getContext(), remoteMessage, intent);
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "failure");
                }
            });

        } else {
            getNotificationUtils().setNotificationWithIntent(getContext(), remoteMessage, new Intent(getContext(), HomePage.class));
        }

    }

    /**
     * Returns the instance of the recipe storage.
     * @return the recipe storage instance
     */
    public RecipeStorage getRecipeStorage() {
        return RecipeStorage.getInstance();
    }

    /**
     * Gets the instance of the user storage.
     * @return the instance of the user storage
     */
    public UserStorage getUserStorage() {
        return UserStorage.getInstance();
    }

    /**
     * Gets the instance of firebase messaging associated to this receiver.
     * @return the firebase messaging
     */
    public FirebaseMessaging getFirebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    /**
     * Gets the current context.
     * @return the current context
     */
    public Context getContext() {
        return this;
    }

    /**
     * Gets the instance of Notification utils class.
     * @return the instance of Notification utils
     */
    public NotificationUtils getNotificationUtils() {
        return NotificationUtils.getInstance();
    }
}

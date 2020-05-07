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

public class NotificationReceiverService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

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
            getRecipeStorage().readRecipeFromUuid(remoteMessage.getData().get("recipe"), new CallHandler<Recipe>() {
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

    public RecipeStorage getRecipeStorage() {
        return RecipeStorage.getInstance();
    }

    public UserStorage getUserStorage() {
        return UserStorage.getInstance();
    }

    public FirebaseMessaging getFirebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }

    public Context getContext() {
        return this;
    }

    public NotificationUtils getNotificationUtils() {
        return NotificationUtils.getInstance();
    }
}

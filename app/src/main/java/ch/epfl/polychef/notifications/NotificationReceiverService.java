package ch.epfl.polychef.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.MultipleCallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

public class NotificationReceiverService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private String channelID = "User notification";

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
                    setNotificationWithIntent(remoteMessage, intent);
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "failure");
                }
            });

        } else {
            setNotificationWithIntent(remoteMessage, new Intent(getContext(), HomePage.class));
        }

    }

    private void setNotificationWithIntent(RemoteMessage remoteMessage, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = getRandom();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext() , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setChannels(notificationManager);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), channelID)
                .setSmallIcon(R.drawable.ic_star_black_yellow)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setChannels(NotificationManager notificationManager){
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(channelID, "New notification", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("New user notification");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
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

    public int getRandom() {
        return new Random().nextInt(3000);
    }

    public Context getContext() {
        return this;
    }
}

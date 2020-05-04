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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;

public class NotificationReceiverService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private String channelID = "User notification";

    public NotificationReceiverService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // TODO on new token should check if user should be subscribed
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(Objects.equals(remoteMessage.getData().get("type"), "recipe")) {
            RecipeStorage.getInstance().readRecipeFromUuid(remoteMessage.getData().get("recipe"), new CallHandler<Recipe>() {
                @Override
                public void onSuccess(Recipe data) {
                    Intent intent = new Intent(NotificationReceiverService.this, HomePage.class);
                    intent.putExtra("RecipeToSend", data);
                    setNotificationWithIntent(remoteMessage, intent);
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "failure");
                }
            });

        } else {
            setNotificationWithIntent(remoteMessage, new Intent(this, HomePage.class));
        }

    }

    private void setNotificationWithIntent(RemoteMessage remoteMessage, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setChannels(notificationManager);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID)
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
}

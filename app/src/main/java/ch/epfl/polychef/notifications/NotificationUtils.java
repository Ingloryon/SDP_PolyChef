package ch.epfl.polychef.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import ch.epfl.polychef.R;

/**
 * A util class for Notifications.
 */
public class NotificationUtils {
    private String channelID = "User notification";
    private static final int BOUND = 3000;
    private static final NotificationUtils INSTANCE = new NotificationUtils();


    /**
     * Prevents multiple instances of NotificationUtils to be instantiated.
     */
    private NotificationUtils() {
    }

    /**
     * Gets the instance of NotificationUtils.
     * @return the instance of NotificationUtils
     */
    public static NotificationUtils getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the notification with the given intent and remote message.
     * @param context the context of the caller
     * @param remoteMessage the remote message
     * @param intent the given intent
     */
    public void setNotificationWithIntent(Context context, RemoteMessage remoteMessage, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = getRandom();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setChannels(notificationManager);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_star_black_yellow)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    /**
     * Sets the channel on the given NotificationManager.
     * @param notificationManager the NotificationManager to set the channel on
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setChannels(NotificationManager notificationManager){
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(channelID, "New notification", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("New user notification");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    /**
     * Returns a random integer between 0 and 3000.
     * @return a random integer (between 0 and 3000)
     */
    public int getRandom() {
        return new Random().nextInt(BOUND);
    }
}

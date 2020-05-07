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

public class NotificationUtils {

    private String channelID = "User notification";
    private static final NotificationUtils INSTANCE = new NotificationUtils();

    private NotificationUtils() {
    }

    public static NotificationUtils getInstance() {
        return INSTANCE;
    }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setChannels(NotificationManager notificationManager){
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(channelID, "New notification", NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription("New user notification");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    public int getRandom() {
        return new Random().nextInt(3000);
    }
}

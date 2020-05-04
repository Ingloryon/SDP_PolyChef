package ch.epfl.polychef.users;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.R;

public class ChannelNotificationUser {

    public static void createNotificationChannelForUser(String userEmail) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        Log.e("ChannelNotificationUser", "Add channel 1");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.e("ChannelNotificationUser", "Add channel 2");
//            CharSequence name = GlobalApplication.getAppContext().getString(R.string.channel_name);
//            String description = GlobalApplication.getAppContext().getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(userEmail, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = GlobalApplication.getAppContext().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            Log.e("ChannelNotificationUser", "Add channel " + notificationManager.getNotificationChannels());
//        }
    }

}

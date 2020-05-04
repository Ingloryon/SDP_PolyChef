package ch.epfl.polychef.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.HomePage;

public class NotificationReceiverService extends FirebaseMessagingService {
    private String TAG = "MessagingService";
    private String channelID = "User notification";

    public NotificationReceiverService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, HomePage.class);
//        if(remoteMessage.getData().get("type").equals("recipe")) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("Recipe", OfflineRecipes.getInstance().getOfflineRecipes().get(0));
//            intent = new Intent(this, FullRecipeFragment.class); // TODO find a way to lauch the fullRecipeFragment directly and add recipe by its uuid
//        }
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

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        Log.e(TAG, "send notification error");
    }
}

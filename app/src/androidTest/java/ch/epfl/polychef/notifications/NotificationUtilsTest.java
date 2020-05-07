package ch.epfl.polychef.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ch.epfl.polychef.R;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationUtilsTest {

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            true);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private NotificationUtils notificationUtils = Mockito.mock(NotificationUtils.class, CALLS_REAL_METHODS);

    private Context mockContext = Mockito.mock(Context.class, CALLS_REAL_METHODS);

    private int notifiedID;

    private Notification foundNotification;

    @Mock
    private NotificationManager notificationManager;

    @Before
    public void initMocks(){
        foundNotification = null;
        notifiedID = 0;
        MockitoAnnotations.initMocks(this);
        doAnswer(invocation -> {
            foundNotification = invocation.getArgument(1);
            notifiedID = invocation.getArgument(0);
            Log.e("VOILA", "test");
            return null;
        }).when(notificationManager).notify(anyInt(), any(Notification.class));
        when(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(notificationManager);
        when(mockContext.getPackageName()).thenReturn(intentsTestRule.getActivity().getApplicationContext().getPackageName());
        when(mockContext.getContentResolver()).thenReturn(intentsTestRule.getActivity().getApplicationContext().getContentResolver());
        when(mockContext.getResources()).thenReturn(intentsTestRule.getActivity().getApplicationContext().getResources());
        when(mockContext.getApplicationInfo()).thenReturn(intentsTestRule.getActivity().getApplicationContext().getApplicationInfo());

    }

    @Test
    public void theNotificationManagerIsNotified(){
        disableSetChannels();
        when(notificationUtils.getRandom()).thenReturn(30);
        RemoteMessage remoteMessage = getRemoteMessage();

        notificationUtils.setNotificationWithIntent(mockContext ,remoteMessage, new Intent(intentsTestRule.getActivity().getApplicationContext(), HomePage.class));
        assertEquals(30, notifiedID);
    }

    @Test
    public void theNotificationFoundIsNotNull(){
        disableSetChannels();
        RemoteMessage remoteMessage = getRemoteMessage();
        notificationUtils.setNotificationWithIntent(mockContext ,remoteMessage, new Intent(intentsTestRule.getActivity().getApplicationContext(), HomePage.class));
        assert(foundNotification != null);
    }
    @Test
    public void setChannelsCallsCreateNotificationChannel(){
        notificationUtils.setChannels(notificationManager);
        verify(notificationManager).createNotificationChannel(any(NotificationChannel.class));

    }

    @Test
    public void getRandomReturnValidRandom(){
        int random = notificationUtils.getRandom();
        assert(random >= 0 && random < 3000);
    }


    public void disableSetChannels(){
        doAnswer(invocation -> {
            return null;
        }).when(notificationUtils).setChannels(any(NotificationManager.class));
    }

    public RemoteMessage getRemoteMessage(){
        return new RemoteMessage.Builder("test")
                .addData("title", "title")
                .addData("message", "message")
                .build();

    }
}

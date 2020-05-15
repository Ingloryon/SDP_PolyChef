package ch.epfl.polychef.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

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

import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;

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
            notifiedID = invocation.getArgument(0);
            foundNotification = invocation.getArgument(1);
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
        when(notificationUtils.getRandom()).thenReturn(30);
        setUpSetNotifCommand();
        assertEquals(30, notifiedID);
    }

    @Test
    public void theNotificationFoundIsNotNull(){
        setUpSetNotifCommand();
        assert(foundNotification != null);
    }

    @Test
    public void setChannelsCallsCreateNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationUtils.setChannels(notificationManager);
            verify(notificationManager).createNotificationChannel(any(NotificationChannel.class));
        }
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

    public void setUpSetNotifCommand(){
        disableSetChannels();
        RemoteMessage remoteMessage = getRemoteMessage();
        notificationUtils.setNotificationWithIntent(mockContext ,remoteMessage, new Intent(intentsTestRule.getActivity().getApplicationContext(), HomePage.class));
    }
}

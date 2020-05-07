package ch.epfl.polychef.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.telecom.Call;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ServiceTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.concurrent.TimeoutException;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NotificationReceiverServiceTest {

//    @Rule
//    public final ServiceTestRule serviceRule = new ServiceTestRule();

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

    private NotificationReceiverService notificationReceiverService = mock(NotificationReceiverService.class, CALLS_REAL_METHODS);

    @Mock
    private UserStorage userStorage;

    @Mock
    private RecipeStorage recipeStorage;

    @Mock
    private FirebaseMessaging firebaseMessaging;

//    @Mock
//    private Random random;

    @Before
    public void initMocks() throws TimeoutException {
        MockitoAnnotations.initMocks(this);
        when(notificationReceiverService.getFirebaseMessaging()).thenReturn(firebaseMessaging);
        when(notificationReceiverService.getRecipeStorage()).thenReturn(recipeStorage);
        when(notificationReceiverService.getUserStorage()).thenReturn(userStorage);
        when(notificationReceiverService.getRandom()).thenReturn(23);
        when(notificationReceiverService.getContext()).thenReturn(intentsTestRule.getActivity().getApplicationContext());

//        Intent serviceIntent = new Intent(ApplicationProvider.getApplicationContext(), FakeNotificationReceiver.class);
////        serviceIntent.setClassName("ch.epfl.polychef", "ch.epfl.polychef.notifications.NotificationReceiverService");
//        serviceRule.startService(serviceIntent);
//        IBinder binder = null;
//        // Fix for https://issuetracker.google.com/issues/37054210
//        // We have to wait for the binder to not be null
//        for(int i = 0; i < 10000 && (binder = serviceRule.bindService(serviceIntent)) == null; i++) {}
////        IBinder binder = serviceRule.bindService(serviceIntent);
//        notificationReceiverService = ((FakeNotificationReceiver.LocalBinder) binder).getService();

    }

    @Test
    public void canRetrieveNotificationSubscriptionsFromUser() {
        User user = new User("test@mock.com", "name");
        user.addSubscription("1234");
        user.addSubscription("5678");
        User user2 = new User("1234", "name");
        user2.setKey("1");
        User user3 = new User("5678", "name");
        user3.setKey("2");
        when(userStorage.getPolyChefUser()).thenReturn(user);
        doAnswer(call -> {
            CallHandler<User> ch = call.getArgument(1);
            if(call.getArgument(0).equals("1234")) {
                ch.onSuccess(user2);
            } else {
                ch.onSuccess(user3);
            }
            return null;
        }).when(userStorage).getUserByEmail(anyString(), any(CallHandler.class));
        notificationReceiverService.onNewToken("testToken");
        verify(firebaseMessaging).subscribeToTopic("recipe_1");
        verify(firebaseMessaging).subscribeToTopic("recipe_2");
    }

    @Test
    public void anyNotificationCanTriggerAndGoesToHomePage() {
        RemoteMessage remoteMessage = new RemoteMessage.Builder("test")
                .addData("title", "title")
                .addData("message", "message")
                .build();
        // TODO fix this test
//        notificationReceiverService.onMessageReceived(remoteMessage);
//        NotificationManager notificationManager = (NotificationManager) ApplicationProvider.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
//        for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
//            if (notification.getId() == 23) {
//            }
//        }
    }

//    public class FakeNotificationReceiver extends NotificationReceiverService {
//
//        public FakeNotificationReceiver() {
//
//        }
//
//        public RecipeStorage getRecipeStorage() {
//            return recipeStorage;
//        }
//
//        public UserStorage getUserStorage() {
//            return userStorage;
//        }
//
//        public FirebaseMessaging getFirebaseMessaging() {
//            return firebaseMessaging;
//        }
//
//        public Random getRandom() {
//            return new Random(2);
//        }
//
//        public class LocalBinder {
//            public NotificationReceiverService getService() {
//                return FakeNotificationReceiver.this;
//            }
//        }
//    }
}

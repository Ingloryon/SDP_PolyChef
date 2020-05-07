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

import com.google.common.base.Supplier;
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
import java.util.function.Consumer;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;
import ch.epfl.polychef.pages.HomePage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.users.User;
import ch.epfl.polychef.users.UserStorage;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NotificationReceiverServiceTest {

    private Recipe fakeRecipe = new RecipeBuilder()
            .setName("Fake")
            .addInstruction("ins 1")
            .addIngredient("ing", 4, Ingredient.Unit.NONE)
            .setPersonNumber(2)
            .setEstimatedCookingTime(2)
            .setEstimatedPreparationTime(2)
            .setRecipeDifficulty(Recipe.Difficulty.HARD)
            .setDate("20/06/01 11:11:11")
            .setAuthor("author")
            .build();

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

    @Mock
    private NotificationUtils notificationUtils;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(notificationReceiverService.getFirebaseMessaging()).thenReturn(firebaseMessaging);
        when(notificationReceiverService.getRecipeStorage()).thenReturn(recipeStorage);
        when(notificationReceiverService.getUserStorage()).thenReturn(userStorage);
        when(notificationReceiverService.getContext()).thenReturn(intentsTestRule.getActivity().getApplicationContext());
        when(notificationReceiverService.getNotificationUtils()).thenReturn(notificationUtils);
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
        checkForIntent(intent -> {
            assertThat(intent.getComponent().getClassName(), is("ch.epfl.polychef.pages.HomePage"));
        }, remoteMessage);
        notificationReceiverService.onMessageReceived(remoteMessage);
    }

    @Test
    public void recipeNotificationsCreateWithRecipeAsExtra() {
        RemoteMessage remoteMessage = new RemoteMessage.Builder("test")
                .addData("title", "title")
                .addData("message", "message")
                .addData("recipe", "recipe_uuid")
                .addData("type", "recipe")
                .build();
        doAnswer(call -> {
            CallHandler<Recipe> caller = call.getArgument(1);
            caller.onSuccess(fakeRecipe);
            return null;
        }).when(recipeStorage).readRecipeFromUuid(eq("recipe_uuid"), any(CallHandler.class));
        checkForIntent(intent -> {
            assertThat(intent.getComponent().getClassName(), is("ch.epfl.polychef.pages.HomePage"));
            assertThat(intent.getExtras().getSerializable("RecipeToSend"), is(fakeRecipe));
        }, remoteMessage);
        notificationReceiverService.onMessageReceived(remoteMessage);
    }

    private void checkForIntent(Consumer<Intent> check, RemoteMessage remoteMessage) {
        doAnswer(call -> {
            Intent foundIntent = call.getArgument(2);
            check.accept(foundIntent);
            return null;
        }).when(notificationUtils).setNotificationWithIntent(any(Context.class), eq(remoteMessage), any(Intent.class));
    }
}

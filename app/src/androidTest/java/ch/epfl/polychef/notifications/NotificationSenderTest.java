package ch.epfl.polychef.notifications;

import com.android.volley.toolbox.JsonObjectRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.polychef.fragments.CommentTestOnFullRecipe;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.utils.SingletonQueue;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationSenderTest {

    private NotificationSender notificationSender = Mockito.mock(NotificationSender.class, CALLS_REAL_METHODS);
    private SingletonQueue singletonQueue = mock(SingletonQueue.class);
    public static Recipe fakeRecipe = CommentTestOnFullRecipe.returnFreshFakeRecipeBuilder().build();

    @Before
    public void initMock() {
        when(notificationSender.getSingletonQueue()).thenReturn(singletonQueue);
    }

    @Test
    public void nullArgThrowIllegal() {
        assertThrows(IllegalArgumentException.class, () -> notificationSender.sendNewRecipe(null, "name", fakeRecipe));
        assertThrows(IllegalArgumentException.class, () -> notificationSender.sendNewRecipe("user_key", null, fakeRecipe));
        assertThrows(IllegalArgumentException.class, () -> notificationSender.sendNewRecipe("user_key", "name", null));
    }

    @Test
    public void sendingRecipeForNotificationSendRequest() {
        doAnswer(call -> {
            JsonObjectRequest jsonObjectRequest = call.getArgument(0);
            assertThat(jsonObjectRequest.getHeaders(), hasEntry("Authorization", "key=" + "AAAAhNJZ4jI:APA91bH8UkEAkTdPDXg2xsiWH7ur8o2lM6Jvd3HPZ-HOluYk6NqmptQthq4O0lil0RchrbuaqkFAJoA1PUMU41AMuQ8i3gEJhcGI--4kxQPqaaryPXO2euObw8mGM98j9qAfEx3MqNwK"));
            assertThat(jsonObjectRequest.getHeaders(), hasEntry("Content-Type", "application/json"));
            return null;
        }).when(singletonQueue).addToRequestQueue(any(JsonObjectRequest.class));
        notificationSender.sendNewRecipe("u_key", "u_name", fakeRecipe);
        verify(singletonQueue).addToRequestQueue(any(JsonObjectRequest.class));
    }
}

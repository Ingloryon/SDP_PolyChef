package ch.epfl.polychef.notifications;

import com.android.volley.toolbox.JsonObjectRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.utils.SingletonQueue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationSenderTest {

    private NotificationSender notificationSender = Mockito.mock(NotificationSender.class, CALLS_REAL_METHODS);
    private SingletonQueue singletonQueue = mock(SingletonQueue.class);
    private Recipe fakeRecipe = new RecipeBuilder()
            .setName("Fake recipe")
            .addInstruction("Instruction 1")
            .addIngredient("ingredient", 2, Ingredient.Unit.NONE)
            .setPersonNumber(1)
            .setEstimatedCookingTime(1)
            .setEstimatedPreparationTime(1)
            .setRecipeDifficulty(Recipe.Difficulty.EASY)
            .setDate("20/06/01 13:10:00")
            .setAuthor("author name")
            .build();

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
        notificationSender.sendNewRecipe("u_key", "u_name", fakeRecipe);
        verify(singletonQueue).addToRequestQueue(any(JsonObjectRequest.class));
    }
}

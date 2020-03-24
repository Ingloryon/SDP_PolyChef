package ch.epfl.polychef;

import org.junit.Test;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private String mockUserEmail = "testUser@epfl.ch";
    private String mockUserName = "Alice InWonderland";

    @Test
    public void gettersWorkOnNewUser() {

        String email = mockUserEmail;
        String username = mockUserName;
        User alice = new User(email, username);

        assertNotNull(alice);
        assertEquals(mockUserEmail, alice.getEmail());
        assertEquals(mockUserName, alice.getUsername());

        assertEquals(0, alice.getRecipes().size());
        assertEquals(0, alice.getFavourites().size());
        assertEquals(0, alice.getSubscribers().size());
        assertEquals(0, alice.getSubscriptions().size());
    }

    @Test
    public void gettersWorkOnEmptyUser() {
        User noOne = new User();

        assertNull(noOne.getEmail());
        assertNull(noOne.getUsername());
        assertEquals(0, noOne.getRecipes().size());
        assertEquals(0, noOne.getFavourites().size());
        assertEquals(0, noOne.getSubscribers().size());
        assertEquals(0, noOne.getSubscriptions().size());
    }

    @Test
    public void canAddFavouriteRecipes() {
        User alice = new User(mockUserEmail, mockUserName);

        String recipe1 = "Recipe_1";
        alice.addFavourite(recipe1);
        assertEquals(1, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));

        String recipe2 = "Recipe_2";
        alice.addFavourite(recipe2);
        assertEquals(2, alice.getFavourites().size());;
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));

        String recipe3 = "Recipe_3";
        alice.addFavourite(recipe3);
        assertEquals(3, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));

        String recipe4 = "Recipe_4";
        alice.addFavourite(recipe4);
        assertEquals(4, alice.getFavourites().size());
        assertTrue(alice.getFavourites().contains(recipe1));
        assertTrue(alice.getFavourites().contains(recipe2));
        assertTrue(alice.getFavourites().contains(recipe3));
        assertTrue(alice.getFavourites().contains(recipe4));
    }

    @Test
    public void canAddSubscribers() {

        User definitelyNotAlice = new User(mockUserEmail, mockUserName);

        String subscriber1 = "Subscriber_1";
        definitelyNotAlice.addSubscriber(subscriber1);
        assertEquals(1, definitelyNotAlice.getSubscribers().size());
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber1));

        String subscriber2 = "Subscriber_2";
        definitelyNotAlice.addSubscriber(subscriber2);
        assertEquals(2, definitelyNotAlice.getSubscribers().size());
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber1));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber2));

        String subscriber3 = "Subscriber_3";
        definitelyNotAlice.addSubscriber(subscriber3);
        assertEquals(3, definitelyNotAlice.getSubscribers().size());
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber1));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber2));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber3));

        String subscriber4 = "Subscriber_4";
        definitelyNotAlice.addSubscriber(subscriber4);
        assertEquals(4, definitelyNotAlice.getSubscribers().size());
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber1));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber2));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber3));
        assertTrue(definitelyNotAlice.getSubscribers().contains(subscriber4));
    }

    @Test
    public void canAddSubscriptions() {

        User certainlyNotAlice = new User(mockUserEmail, mockUserName);

        String subscription1 = "Subscription_1";
        certainlyNotAlice.addSubscriptions(subscription1);
        assertEquals(1, certainlyNotAlice.getSubscriptions().size());
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription1));

        String subscription2 = "Subscription_2";
        certainlyNotAlice.addSubscriptions(subscription2);
        assertEquals(2, certainlyNotAlice.getSubscriptions().size());
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription1));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription2));

        String subscription3 = "Subscription_3";
        certainlyNotAlice.addSubscriptions(subscription3);
        assertEquals(3, certainlyNotAlice.getSubscriptions().size());
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription1));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription2));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription3));

        String subscription4 = "Subscription_4";
        certainlyNotAlice.addSubscriptions(subscription4);
        assertEquals(4, certainlyNotAlice.getSubscriptions().size());
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription1));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription2));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription3));
        assertTrue(certainlyNotAlice.getSubscriptions().contains(subscription4));
    }
}

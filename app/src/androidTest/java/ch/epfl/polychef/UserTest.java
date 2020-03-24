package ch.epfl.polychef;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private String mockUserEmail = "testUser@epfl.ch";
    private String mockUserName = "Alice InWonderland";

    private User alice;

    private User mockUser(){
        return new User(mockUserEmail, mockUserName);
    }

    @Before
    public void setupAlice(){
        alice = mockUser();
    }

    @Test
    public void gettersWorkOnNewUser() {

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
    public void canAddRecipes() {

        String recipe1 = "Recipe_1";
        alice.addSubscription(recipe1);
        assertEquals(1, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(recipe1));

        String recipe2 = "Recipe_2";
        alice.addSubscription(recipe2);
        assertEquals(2, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(recipe1));
        assertTrue(alice.getSubscriptions().contains(recipe2));

        String recipe3 = "Recipe_3";
        alice.addSubscription(recipe3);
        assertEquals(3, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(recipe1));
        assertTrue(alice.getSubscriptions().contains(recipe2));
        assertTrue(alice.getSubscriptions().contains(recipe3));

        String recipe4 = "Recipe_4";
        alice.addSubscription(recipe4);
        assertEquals(4, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(recipe1));
        assertTrue(alice.getSubscriptions().contains(recipe2));
        assertTrue(alice.getSubscriptions().contains(recipe3));
        assertTrue(alice.getSubscriptions().contains(recipe4));
    }

    @Test
    public void canAddFavouriteRecipes() {

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

        String subscriber1 = "Subscriber_1";
        alice.addSubscriber(subscriber1);
        assertEquals(1, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));

        String subscriber2 = "Subscriber_2";
        alice.addSubscriber(subscriber2);
        assertEquals(2, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));

        String subscriber3 = "Subscriber_3";
        alice.addSubscriber(subscriber3);
        assertEquals(3, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));

        String subscriber4 = "Subscriber_4";
        alice.addSubscriber(subscriber4);
        assertEquals(4, alice.getSubscribers().size());
        assertTrue(alice.getSubscribers().contains(subscriber1));
        assertTrue(alice.getSubscribers().contains(subscriber2));
        assertTrue(alice.getSubscribers().contains(subscriber3));
        assertTrue(alice.getSubscribers().contains(subscriber4));
    }

    @Test
    public void canAddSubscriptions() {

        String subscription1 = "Subscription_1";
        alice.addSubscription(subscription1);
        assertEquals(1, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));

        String subscription2 = "Subscription_2";
        alice.addSubscription(subscription2);
        assertEquals(2, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));

        String subscription3 = "Subscription_3";
        alice.addSubscription(subscription3);
        assertEquals(3, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));

        String subscription4 = "Subscription_4";
        alice.addSubscription(subscription4);
        assertEquals(4, alice.getSubscriptions().size());
        assertTrue(alice.getSubscriptions().contains(subscription1));
        assertTrue(alice.getSubscriptions().contains(subscription2));
        assertTrue(alice.getSubscriptions().contains(subscription3));
        assertTrue(alice.getSubscriptions().contains(subscription4));
    }

    @Test
    public void equalsWorksOnTrivialInputs(){

        assertNotEquals(alice, null);
        assertNotEquals(null, alice);

        assertNotEquals(alice, "alice");
        assertNotEquals("alice", alice);

        assertNotEquals(alice, new User());
        assertNotEquals(new User(), alice);

        assertNotEquals(alice, new User("bob", "inQuarantine"));
        assertNotEquals(new User("bob", "inQuarantine"), alice);

        assertEquals(alice, alice);
    }

    @Test
    public void equalsChecksEachFields(){

        User doppelganger = mockUser();
        doppelganger.addRecipe("Recipe");
        assertNotEquals(alice, doppelganger);
        assertNotEquals(doppelganger, alice);

        doppelganger = mockUser();
        doppelganger.addFavourite("Favourite");
        assertNotEquals(alice, doppelganger);
        assertNotEquals(doppelganger, alice);

        doppelganger = mockUser();
        doppelganger.addSubscriber("Subscriber");
        assertNotEquals(alice, doppelganger);
        assertNotEquals(doppelganger, alice);

        doppelganger = mockUser();
        doppelganger.addSubscription("Subscription");
        assertNotEquals(alice, doppelganger);
        assertNotEquals(doppelganger, alice);
    }
}

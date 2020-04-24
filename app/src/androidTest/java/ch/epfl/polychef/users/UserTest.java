package ch.epfl.polychef.users;

import androidx.test.espresso.core.internal.deps.guava.base.Function;

import com.google.android.gms.common.util.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    public void addAndAssert(List<String> toAdd,
                             BiConsumer<User, String> add,
                             Function<User, List<String>> content){

        assertNotNull(alice);
        assertNotNull(toAdd);
        assertEquals(4, toAdd.size());

        add.accept(alice, toAdd.get(0));
        assertEquals(1, content.apply(alice).size());
        assertTrue(content.apply(alice).contains(toAdd.get(0)));

        add.accept(alice, toAdd.get(1));
        assertEquals(2, content.apply(alice).size());
        assertTrue(content.apply(alice).contains(toAdd.get(0)));
        assertTrue(content.apply(alice).contains(toAdd.get(1)));

        add.accept(alice, toAdd.get(2));
        assertEquals(3, content.apply(alice).size());
        assertTrue(content.apply(alice).contains(toAdd.get(0)));
        assertTrue(content.apply(alice).contains(toAdd.get(1)));
        assertTrue(content.apply(alice).contains(toAdd.get(2)));

        add.accept(alice, toAdd.get(3));
        assertEquals(4, content.apply(alice).size());
        assertTrue(content.apply(alice).contains(toAdd.get(0)));
        assertTrue(content.apply(alice).contains(toAdd.get(1)));
        assertTrue(content.apply(alice).contains(toAdd.get(2)));
        assertTrue(content.apply(alice).contains(toAdd.get(3)));
    }

    @Test
    public void canAddRecipes() {

        String[] stringUuids = new String[] {UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        User fakeUser = new User();
        fakeUser.addRecipe(stringUuids[0]);
        fakeUser.addRecipe(stringUuids[1]);
        fakeUser.addRecipe(stringUuids[2]);
        fakeUser.addRecipe(stringUuids[3]);

        assertEquals(stringUuids[0], fakeUser.getRecipes().get(0));
        assertEquals(stringUuids[1], fakeUser.getRecipes().get(1));
        assertEquals(stringUuids[2], fakeUser.getRecipes().get(2));
        assertEquals(stringUuids[3], fakeUser.getRecipes().get(3));
    }

    @Test
    public void canAddFavourites() {

        addAndAssert(
                Arrays.asList("Favourite_1", "Favourite_2", "Favourite_3", "Favourite_4"),
                User::addFavourite,
                User::getFavourites
        );
    }

    @Test
    public void canAddSubscribers() {

        addAndAssert(
                Arrays.asList("Subscriber_1", "Subscriber_2", "Subscriber_3", "Subscriber_4"),
                User::addSubscriber,
                User::getSubscribers
        );
    }

    @Test
    public void canAddSubscriptions() {

        addAndAssert(
                Arrays.asList("Subscription_1", "Subscription_2", "Subscription_3", "Subscription_4"),
                User::addSubscription,
                User::getSubscriptions
        );
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
    public void removeNullRecipesCorrectly(){
        User user = mockUser();
        int nbNulls = 12;
        int nbRecipes = 45;
        ArrayList<Integer> nulls = new ArrayList<>(nbNulls);
        Random rnd=new Random();

        for(int i = 0 ; i < nbNulls ; ++i){
            nulls.add(rnd.nextInt(nbRecipes));
        }


        for(int i = 0; i < nbRecipes; ++i){
            if(nulls.contains(i)){
                user.addRecipe(null);
            } else {
                user.addRecipe("Recipe");
            }
        }
        user.removeNullFromLists();
        assertFalse(user.getRecipes().contains(null));
    }

    @Test
    public void equalsChecksEachFields(){

        User doppelganger = mockUser();
        doppelganger.addRecipe(UUID.randomUUID().toString());
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

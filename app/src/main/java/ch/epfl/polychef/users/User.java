package ch.epfl.polychef.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    private String email;
    private String username;
    private List<String> recipes;
    private List<String> favourites;
    private List<String> subscribers;
    private List<String> subscriptions;

    public User(String email, String username){
        this.email = email;
        this.username = username;
        recipes = new ArrayList<>();
        favourites = new ArrayList<>();
        subscribers = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public User() {
        recipes = new ArrayList<>();
        favourites = new ArrayList<>();
        subscribers = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public List<String> getFavourites() {
        return new ArrayList<>(favourites);
    }

    public List<String> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    public List<String> getSubscriptions() {
        return new ArrayList<>(subscriptions);
    }

    public void addRecipe(String recipe) {
        recipes.add(recipe);
    }

    public void addFavourite(String recipe){
        favourites.add(recipe);
    }

    public void addSubscription(String user) {
        subscriptions.add(user);
    }

    public void addSubscriber(String user) {
        subscribers.add(user);
    }

    @NonNull
    @Override
    public String toString() {
        return "User: \n"
                + "Email=" + email + ",\n"
                + "username=" + username + ",\n"
                + "favourites=" + favourites + ",\n"
                + "subscribers=" + subscribers + ",\n"
                + "subscriptions=" + subscriptions;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(this == obj){
            return true;
        }

        if(obj instanceof User){
            User other = (User) obj;

            if(Objects.equals(email, other.email)
            && Objects.equals(username, other.username)){

                return Objects.equals(email, other.email)
                        && Objects.equals(recipes, other.recipes)
                        && Objects.equals(favourites, other.favourites)
                        && Objects.equals(subscribers, other.subscribers)
                        && Objects.equals(subscriptions, other.subscriptions);
            }
        }

        return false;
    }
}

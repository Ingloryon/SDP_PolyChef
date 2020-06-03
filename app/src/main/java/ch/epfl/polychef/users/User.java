package ch.epfl.polychef.users;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.R;
import ch.epfl.polychef.recipe.Rating;
import ch.epfl.polychef.utils.Preconditions;

/**
 * Represents a Polychef User.
 */
public class User implements Serializable, Miniatures {

    public static final String TAG = "User";
    private String email;
    private String username;
    private String key;
    private int profilePictureId;
    private Rating userRating;
    private List<String> recipes;
    private List<String> favourites;
    private List<String> subscribers;
    private List<String> subscriptions;

    /**
     * Construct empty User for Firebase.
     */
    public User() {
        setProfilePictureId(0);
        recipes = new ArrayList<>();
        favourites = new ArrayList<>();
        subscribers = new ArrayList<>();
        subscriptions = new ArrayList<>();
        userRating = new Rating();
    }

    /**
     * Constructs a User from basic information.
     * @param email the email address of the user
     * @param username the username of the user
     */
    public User(String email, String username){
        this.email = email;
        this.username = username;
        setProfilePictureId(0);
        recipes = new ArrayList<>();
        favourites = new ArrayList<>();
        subscribers = new ArrayList<>();
        subscriptions = new ArrayList<>();
        userRating = new Rating();
    }

    /**
     * Constructs a User from basic information.
     * @param email the email address of the user
     * @param username the username of the user
     * @param rating an existing Rating to give the user
     */
    public User(String email, String username, Rating rating){
        this.email = email;
        this.username = username;
        setProfilePictureId(0);
        recipes = new ArrayList<>();
        favourites = new ArrayList<>();
        subscribers = new ArrayList<>();
        subscriptions = new ArrayList<>();
        this.userRating = rating;
    }

    /**
     * Removes the null values in the list of recipes.
     */
    @SuppressWarnings("WeakerAccess")
    public void removeNullFromLists(){
        recipes.removeAll(Collections.singleton(null));
    }

    /**
     * Gets the email of the user.
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the username of the user.
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    @Exclude
    public String getName(){
        return getUsername();
    }

    /**
     * Gets the list of recipes of the user.
     * @return the list of recipes of the user
     */
    public List<String> getRecipes() {
        return new ArrayList<>(recipes);
    }

    /**
     * Gets the list of favorites of the user.
     * @return the list of favorites of the user
     */
    public List<String> getFavourites() {
        return new ArrayList<>(favourites);
    }

    /**
     * Gets the list of subscribers of the user.
     * @return the list of subscribers of the user
     */
    public List<String> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    /**
     * Gets the list of subscriptions of the user.
     * @return the list of subscriptions of the user
     */
    public List<String> getSubscriptions() {
        return new ArrayList<>(subscriptions);
    }

    /**
     * Gets the id of the user profile picture.
     * @return the id of the user profile picture
     */
    @SuppressWarnings("WeakerAccess")
    public int getProfilePictureId() {
        return profilePictureId;
    }

    /**
     * Sets the id of the new profile picture of this user.
     * @param profilePictureId the id of the new profile picture
     */
    public void setProfilePictureId(int profilePictureId) {
        //size of the profile picture array
        int size=GlobalApplication.getAppContext().getResources().getStringArray(R.array.profilePicturesNames).length;
        if(profilePictureId<0 || profilePictureId>=size) {//prevent out of bound exception
            this.profilePictureId = 0;
        }else{
            this.profilePictureId = profilePictureId;
        }
    }

    /**
     * Adds a recipe to the user.
     * If the recipe does not already exist.
     * @param recipeUuid the new recipe of the user
     */
    public void addRecipe(String recipeUuid) {
        if(recipeUuid!=null){
            recipes.forEach(uuid->{
                if(uuid.equals(recipeUuid)){
                    //noinspection UnnecessaryReturnStatement
                    return; //do not add it twice if existing
                }
            });
            recipes.add(recipeUuid);
        }
    }

    /**
     * Adds a favorite to the user.
     * @param recipe the new favorite of the user
     */
    public void addFavourite(String recipe){
        favourites.add(recipe);
    }

    /**
     * Removes a favorite to the user.
     * @param recipe the favorite to remove
     */
    public void removeFavourite(String recipe) {
        Preconditions.checkArgument(favourites.contains(recipe), "Can not remove recipe from favourite if it was not in favourite before");
        favourites.remove(recipe);
    }

    /**
     *Adds a subscription to the user.
     * @param user the new subscription
     */
    public void addSubscription(String user) {
        subscriptions.add(user);
    }

    /**
     * Removes a subscription from the user.
     * @param email the email of the subscription to remove
     */
    public void removeSubscription(String email) {
        Preconditions.checkArgument(subscriptions.contains(email), "There are no subscriptions with the given email.");
        subscriptions.remove(email);
    }

    /**
     *Adds a subscriber to the user.
     * @param user the new subscriber
     */
    public void addSubscriber(String user) {
        subscribers.add(user);
    }

    /**
     * Removes a subscriber from the user.
     * @param email the email of the subscriber to remove
     */
    public void removeSubscriber(String email) {
        Preconditions.checkArgument(subscribers.contains(email), "There are no subscribers with the given email.");
        subscribers.remove(email);
    }

    /**
     * Returns the profile picture chosen by the user to display.
     * @param userToDisplay the user that will be displayed
     * @return the profile picture of the user to display
     */
    public static int getResourceImageFromUser(User userToDisplay){
        Context context=GlobalApplication.getAppContext();
        int profilePictureId=userToDisplay.getProfilePictureId();
        String photoName=context.getResources().getStringArray(R.array.profilePicturesNames)[profilePictureId];

        //we return the resource image
        return context.getResources().getIdentifier(photoName, "drawable", context.getPackageName());
    }

    /**
     * Gets the rating of the user.
     * @return the rating of the user
     */
    public Rating getRating(){
        return userRating;
    }

    private void setRating(Rating rating){
        userRating = rating;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    @Override
    public boolean isUser() {
        return true;
    }

    @Exclude
    @Override
    public boolean isRecipe() {
        return false;
    }

    @Exclude
    public String getKey() {
        Preconditions.checkArgument(key != null, "User " + email + " has not been initialized correctly");
        return this.key;
    }

    @NonNull
    @Override
    public String toString() {
        return "User: \n"
                + "Email=" + email + ",\n"
                + "username=" + username + ",\n"
                + "recipes=" + recipes + ",\n"
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

            boolean isSamePerson = Objects.equals(email, other.email)
                    && Objects.equals(username, other.username);

            boolean hasSameRecipes = Objects.equals(recipes, other.recipes)
                    && Objects.equals(favourites, other.favourites);

            boolean hasSameSubs = Objects.equals(subscribers, other.subscribers)
                    && Objects.equals(subscriptions, other.subscriptions);

            boolean hasSamePhoto=other.profilePictureId==profilePictureId;

            return isSamePerson
                    && hasSameRecipes
                    && hasSameSubs
                    && hasSamePhoto;
        }

        return false;
    }

}

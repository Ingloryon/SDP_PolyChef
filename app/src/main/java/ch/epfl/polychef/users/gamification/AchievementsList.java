package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;
import java.util.function.Function;

import ch.epfl.polychef.users.User;

/**
 * Static achievements that can be obtained by the users.
 */
public final class AchievementsList {
    private static final int STANDARD_NB_LEVELS = 3;

    /**
     * The Cuistot achievement depends on the number of Recipes the user posted (levels: 0/1/10/50).
     */
    public static final Achievement cuistotAchievement = createCuistotAchievement();

    /**
     * The Followed achievement depends on the number of followers the user has (levels: 0/1/30/100).
     */
    public static final Achievement followedAchievement = createFollowedAchievement();

    /**
     * The Favorite achievement depends on the number of favorites the user has (levels:0/1/10/50).
     */
    public static final Achievement favoriteAchievement = createFavoriteAchievement();


    private static Achievement createCuistotAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("cuistot_default");
        picturesPaths.add("cuistot_bronze");
        picturesPaths.add("cuistot_silver");
        picturesPaths.add("cuistot_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesLabels.add("No recipes");
        picturesLabels.add("Starting Chef");
        picturesLabels.add("Casual Chef");
        picturesLabels.add("Legacy Chef");

        ArrayList<Integer> levelSteps = new ArrayList<>();
        levelSteps.add(1);
        levelSteps.add(10);
        levelSteps.add(50);

        Function<User, Integer> getUserNbRecipes = u -> u.getRecipes().size();

        return new Achievement(STANDARD_NB_LEVELS, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes);
    }

    private static Achievement createFollowedAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("followed_default");
        picturesPaths.add("followed_bronze");
        picturesPaths.add("followed_silver");
        picturesPaths.add("followed_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesLabels.add("No followers");
        picturesLabels.add("Followed Chef");
        picturesLabels.add("Famous Chef");
        picturesLabels.add("Renowned Chef");

        ArrayList<Integer> levelSteps = new ArrayList<>();
        levelSteps.add(1);
        levelSteps.add(30);
        levelSteps.add(100);

        Function<User, Integer> getUserNbFollowers = u -> u.getSubscribers().size();

        return new Achievement(STANDARD_NB_LEVELS, picturesPaths, picturesLabels, levelSteps, getUserNbFollowers);
    }

    private static Achievement createFavoriteAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("favorite_default");
        picturesPaths.add("favorite_bronze");
        picturesPaths.add("favorite_silver");
        picturesPaths.add("favorite_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesLabels.add("No favorites");
        picturesLabels.add("Curious Chef");
        picturesLabels.add("Local Chef");
        picturesLabels.add("Library Chef");

        ArrayList<Integer> levelSteps = new ArrayList<>();
        levelSteps.add(1);
        levelSteps.add(10);
        levelSteps.add(50);

        Function<User, Integer> getUserNbFavorites = u -> u.getFavourites().size();

        return new Achievement(STANDARD_NB_LEVELS, picturesPaths, picturesLabels, levelSteps, getUserNbFavorites);
    }
}

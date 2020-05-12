package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;
import java.util.function.Function;

import ch.epfl.polychef.users.User;

public final class AchievementsList {
    private static final int STANDART_NB_LEVELS = 3;

    public static final Achievement cuistotAchievement = createCuistotAchievement();
    public static final Achievement follwedAchievement = createFollowedAchievement();


    private static Achievement createCuistotAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("cuistot_bronze");
        picturesPaths.add("cuistot_silver");
        picturesPaths.add("cuistot_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesLabels.add("Starting Chef");
        picturesLabels.add("Casual Chef");
        picturesLabels.add("Legacy Chef");

        ArrayList<Integer> levelSteps = new ArrayList<>();
        levelSteps.add(1);
        levelSteps.add(10);
        levelSteps.add(50);

        Function<User, Integer> getUserNbRecipes = u -> u.getRecipes().size();

        return new Achievement("Cuistot", STANDART_NB_LEVELS, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes);
    }

    private static Achievement createFollowedAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("followed_bronze");
        picturesPaths.add("followed_silver");
        picturesPaths.add("followed_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesLabels.add("Followed Chef");
        picturesLabels.add("Famous Chef");
        picturesLabels.add("Renowned Chef");

        ArrayList<Integer> levelSteps = new ArrayList<>();
        levelSteps.add(1);
        levelSteps.add(30);
        levelSteps.add(100);

        Function<User, Integer> getUserNbFollowers = u -> u.getSubscribers().size();

        return new Achievement("Cuistot", STANDART_NB_LEVELS, picturesPaths, picturesLabels, levelSteps, getUserNbFollowers);
    }

    //"Followed"
    //"Stared"



}

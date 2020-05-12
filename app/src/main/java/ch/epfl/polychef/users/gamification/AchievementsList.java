package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;
import java.util.function.Function;

import ch.epfl.polychef.users.User;

public final class AchievementsList {
    private static final int STANDART_NB_LEVELS = 3;

    public static final Achievement Cuisto_Achievement = createCuistotAchievement();


    private static Achievement createCuistotAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("cuisto_bronze");
        picturesPaths.add("cuisto_silver");
        picturesPaths.add("cuisto_diamond");

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

    //"Followed"
    //"Stared"



}

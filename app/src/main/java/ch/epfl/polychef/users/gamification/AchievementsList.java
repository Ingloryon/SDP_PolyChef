package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;

public final class AchievementsList {
    private static final int STANDART_NB_LEVELS = 3;

    public static final Achievement Cuisto_Achievement = createCuistotAchievement();



    private static Achievement createCuistotAchievement(){

        ArrayList<String> picturesPaths = new ArrayList<>();
        picturesPaths.add("cuisto_bronze");
        picturesPaths.add("cuisto_silver");
        picturesPaths.add("cuisto_diamond");

        ArrayList<String> picturesLabels = new ArrayList<>();
        picturesPaths.add("cuisto_bronze");
        picturesPaths.add("cuisto_silver");
        picturesPaths.add("cuisto_diamond");





        return null; //new Achievement("Cuistot", STANDART_NB_LEVELS, picturesPaths, );
    }

    //"Followed"
    //"Stared"



}

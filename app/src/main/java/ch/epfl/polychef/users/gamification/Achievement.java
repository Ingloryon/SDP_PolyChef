package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.Preconditions;

/**
 * An Achievement that can be obtained by Users
 */
public final class Achievement {
    private static final int MAX_ACHIEVEMENT_LEVEL = 5;
    private final List<Integer> levelSteps = new ArrayList<>();
    private final List<String> picturesLabels = new ArrayList<>();
    private final List<String> picturesPaths = new ArrayList<>();
    private final int nbOfLevels;
    private final Function<User, Integer> evaluatedCriteria;


    public Achievement(int nbOfLevels, ArrayList<String> picturesPaths, ArrayList<String> picturesLabels, ArrayList<Integer> levelSteps, Function<User, Integer> evaluatedCriteria){
        Preconditions.checkArgument(0 < nbOfLevels && nbOfLevels <= MAX_ACHIEVEMENT_LEVEL, "The number of levelSteps must be strictly positive and less than the maximum number of levelSteps (" + MAX_ACHIEVEMENT_LEVEL + ")");
        Preconditions.checkArgument(picturesPaths.size() == nbOfLevels+1 && picturesLabels.size() == nbOfLevels+1 && levelSteps.size() == nbOfLevels);

        this.nbOfLevels = nbOfLevels;
        this.evaluatedCriteria = evaluatedCriteria;
        Collections.copy(this.levelSteps, levelSteps);
        Collections.copy(this.picturesLabels, picturesLabels);
        Collections.copy(this.picturesPaths, picturesPaths);
    }

    public int getAchivementLevel(User user){
        for(int i = 0 ; i < levelSteps.size() ; ++i){
            if(evaluatedCriteria.apply(user) < levelSteps.get(i)) return i;
        }
        return nbOfLevels;
    }


    public String getAchievementLevelImage(int level){
        Preconditions.checkArgument(0<=level && level<=nbOfLevels);

        return picturesPaths.get(level);
    }

    public String getAchievementLevelLabel(int level){
        Preconditions.checkArgument(0<=level && level<=nbOfLevels);

        return picturesLabels.get(level);
    }

}
